package org.hypergraphdb.app.wordnet;

import java.io.ByteArrayInputStream;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Logger;

import net.didion.jwnl.JWNL;
import net.didion.jwnl.JWNLException;
import net.didion.jwnl.data.Exc;
import net.didion.jwnl.data.IndexWord;
import net.didion.jwnl.data.POS;
import net.didion.jwnl.data.Pointer;
import net.didion.jwnl.data.PointerTarget;
import net.didion.jwnl.data.Synset;
import net.didion.jwnl.data.VerbFrame;
import net.didion.jwnl.data.Word;
import net.didion.jwnl.dictionary.Dictionary;

import org.hypergraphdb.HGConfiguration;
import org.hypergraphdb.HGEnvironment;
import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HGIndex;
import org.hypergraphdb.HGPersistentHandle;
import org.hypergraphdb.HGTypeSystem;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.HGQuery.hg;
import org.hypergraphdb.algorithms.HGDepthFirstTraversal;
import org.hypergraphdb.algorithms.SimpleALGenerator;
import org.hypergraphdb.app.wordnet.data.*;
import org.hypergraphdb.indexing.ByPartIndexer;
//import org.hypergraphdb.storage.bdb.BDBConfig;
import org.hypergraphdb.util.Pair;

/**
 * 
 * <p>
 * Use this class to load the WordNet database into HyperGraph. To use this class:
 * </p>
 * <ol>
 * <li>Download WordNet from http://http://wordnet.princeton.edu.
 * <li>Create a new instance.</li>
 * <li>Set the location of the WordNet dictionary with the <code>setDictionaryLocation</code>
 * method. This should be the directory from the WordNet distribution that contains all
 * WordNet data files (e.g. c:\wordnet\dict).
 * </li>
 * <li>Invoke the <code>loadWordNet(HyperGraph) method.</li> 
 * </ol>
 * @author Borislav Iordanov
 *
 */
public class HGWordNetLoader
{
	final static String PROPS_RESOURCE = "org/hypergraphdb/app/wordnet/configuration/file_properties.xml";
	final POS[] pos = new POS[] { POS.ADJECTIVE, POS.NOUN, POS.ADVERB, POS.VERB};
	final Comparator<POS> posComparator = new Comparator<POS>()
	{
		public int compare(POS left, POS right)
		{
			return left.getKey().compareTo(right.getKey());
		}
	};
	final Class<?>[] synset_link_classes = new Class<?>[] 
	{
		AdjSynsetLink.class,
		NounSynsetLink.class,
		AdverbSynsetLink.class,
		VerbSynsetLink.class				 
	};
	final String[] synset_alias = new String[] 
    { 
		"AdjSynset",
		"NounSynset",
		"AdverbSynset",
		"VerbSynset"	 		 
	};
	
	int n_exc = 0;
	long n_syn = 0;
	long n_point = 0;
	long n_word = 0;
	long n_vlinks = 0;
	
	private String typeAliasPrefix;
	private String dictionaryLocation; 
	private Logger logger = Logger.getAnonymousLogger();// global;
	private HashMap<Integer, HGPersistentHandle> verbFrames = new HashMap<Integer, HGPersistentHandle>();
	private HGIndex<String, HGPersistentHandle> wordIndex = null; 
	
	public HGWordNetLoader()
	{		
	}
	
	
	public String getDictionaryLocation()
	{
		return dictionaryLocation;
	}


	public void setDictionaryLocation(String dictionaryLocation)
	{
		this.dictionaryLocation = dictionaryLocation;
	}


	public String getTypeAliasPrefix()
	{
		return typeAliasPrefix;
	}


	public void setTypeAliasPrefix(String typeAliasPrefix)
	{
		this.typeAliasPrefix = typeAliasPrefix;
	}

	@SuppressWarnings("unchecked")
	public void loadWordNet(HyperGraph graph)
	{
		try
		{
			// First, we get the file_properties.xml that JWNL needs as a String,
			// then put the local directory path to the WordNet dictionary, and then
			// initialize the JWNL library.
			StringBuffer propsBuffer = new StringBuffer();
			InputStream inStream = this.getClass().getResourceAsStream(PROPS_RESOURCE);
			if (inStream == null)
				inStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(PROPS_RESOURCE);
			if (inStream == null)
				throw new RuntimeException("Could not load configuration resource " + PROPS_RESOURCE);
			InputStreamReader in = new InputStreamReader(inStream);
			char [] buf = new char[256];
			for (int n = in.read(buf); n > 0; n = in.read(buf) )
				propsBuffer.append(buf, 0, n);				
			JWNL.initialize(new ByteArrayInputStream(propsBuffer.toString().replace("LOCAL_DICTIONARY_PATH", dictionaryLocation).getBytes()));
			logger.info("Creating WordNet Indices");
			createIndices(graph);
			Dictionary d = Dictionary.getInstance();
			logger.info("Adding Verb Frames");
			addVerbFrames(graph);
			logger.info("Adding words");			
			for(int i = 0; i < pos.length; i++)
			{
				logger.info("Adding words of kind " + pos[i]);
				addWords(graph, d.getIndexWordIterator(pos[i]));
			}
			logger.info("Adding synsets");
			for (int i = 0; i < pos.length; i++)
			{
				logger.info("Adding synsets of kind " + pos[i]);
				addSynsets(graph, d.getSynsetIterator(pos[i]), i);
			}
			logger.info("Adding pointers");
			for (int i = 0; i < pos.length; i++)
			{
				logger.info("Adding pointers for synsets of kind " + pos[i]);
				String typeAlias = getTypeAliasPrefix() + synset_alias[i];
				addPointers(graph, d.getSynsetIterator(pos[i]), typeAlias);
			}
			logger.info("Adding exceptions ");
			for (int i = 0; i < pos.length; i++)
				addExceptions(graph, d.getExceptionIterator(pos[i]), i);
		}
		catch (RuntimeException ex) { throw ex; }
		catch (Exception ex) { throw new RuntimeException(ex); }
	}
	
	private HGPersistentHandle lookup(HyperGraph graph, 
									  String typeAlias,
									  String keyProperty, 
									  Object keyValue)
	{
		HGHandle typeHandle = graph.getTypeSystem().getTypeHandle(typeAlias);
		ByPartIndexer byProperty = new ByPartIndexer(typeHandle, new String[] { keyProperty });	
		HGIndex<Object, HGPersistentHandle> index = graph.getIndexManager().getIndex(byProperty);
		return (HGPersistentHandle) index.findFirst(keyValue);
	}
	
	private void createIndices(HyperGraph graph)
	{
		HGTypeSystem ts = graph.getTypeSystem();
		HGPersistentHandle typeH = graph.getPersistentHandle(
				ts.getTypeHandle(org.hypergraphdb.app.wordnet.data.Word.class));
		HGHandle check = ts.getTypeHandle(typeAliasPrefix + "word");
		if (check == null)
			ts.addAlias(typeH, typeAliasPrefix + "word");
		else if (!check.equals(typeH))
			throw new RuntimeException("Alias already in use: " + typeAliasPrefix + "word" + ", please change WordNet prefix.");
		wordIndex = graph.getIndexManager().register(new ByPartIndexer(typeH, new String[] { "lemma" }));
		for (int i = 0; i < pos.length; i++)
		{
			typeH = graph.getPersistentHandle(ts.getTypeHandle(synset_link_classes[i]));
			check = ts.getTypeHandle(typeAliasPrefix + synset_alias[i]);
			if (check == null)
				ts.addAlias(typeH, typeAliasPrefix + synset_alias[i]);
			else if (!check.equals(typeH))
				throw new RuntimeException("Alias already in use: " + typeAliasPrefix + synset_alias[i] + ", please change WordNet prefix.");
			graph.getIndexManager().register(new ByPartIndexer(typeH, new String[] { "id" }));
		}
		typeH = graph.getPersistentHandle(ts.getTypeHandle(org.hypergraphdb.app.wordnet.data.VerbFrame.class));
		check = ts.getTypeHandle(typeAliasPrefix + "frame");
		if (check == null)
			ts.addAlias(typeH, typeAliasPrefix + "frame");
		else if (!check.equals(typeH))
			throw new RuntimeException("Alias already in use: " + typeAliasPrefix + "frame" + ", please change WordNet prefix.");
		graph.getIndexManager().register(new ByPartIndexer(typeH, new String[] { "text" }));
		graph.runMaintenance();
	}
	
	private HGHandle addWord(HyperGraph graph, String lemma)
	{
		return graph.add(new org.hypergraphdb.app.wordnet.data.Word(lemma));		
	}
	
	private void addWords(HyperGraph graph, Iterator<?> it)
	{  
	    int i = 0;
		while (it.hasNext())
		{
			IndexWord word = (IndexWord) it.next();
			String lemma = cleanupLemma(word.getLemma());
			System.out.println(lemma);
			if (wordIndex.findFirst(lemma) == null)
				addWord(graph, lemma);
			//System.out.println("Added " + (++i) + "th word " + lemma);
		}
	}
	
	private void addSynsets(HyperGraph graph, Iterator<Synset> it, int pos)
	{
		while (it.hasNext())
		{
			Synset s = (Synset) it.next();
			HGHandle h = makeSynsetLinkHandle(graph, s, pos);
			if (this.pos[pos] == POS.VERB)
				connectSynsetVerbFrames(graph, h, s);
		}
	}

	private HGHandle makeSynsetLinkHandle(HyperGraph graph, Synset syn, int pos)
	{
		Word[] words = syn.getWords();
		HGHandle[] targets = new HGHandle[words.length];
		for (int i = 0; i < words.length; i++)
		{
			String lemma = cleanupLemma(words[i].getLemma());
			targets[i] = getWordHandle(graph, lemma);
		}		
		SynsetLink link = createSynsetLink(syn);
		link.setTargets(targets);
		HGHandle sh = graph.add(link);
		return sh;
	}
	
	private String cleanupLemma(String lemma)
	{		
		// some adjectives are stored with markers for syntax position (e.g. proof(p))
		int markPos = lemma.indexOf('(');
		if (markPos > 0)
			return lemma.substring(0, markPos);
		else
			return lemma;
	}
	
	private SynsetLink createSynsetLink(Synset syn)
	{
		SynsetLink link = null;
		if (syn.getPOS().equals(POS.ADJECTIVE))
			link = new AdjSynsetLink();
		else if (syn.getPOS().equals(POS.ADVERB))
			link = new AdverbSynsetLink();
		else if (syn.getPOS().equals(POS.NOUN))
			link = new NounSynsetLink();
		else if (syn.getPOS().equals(POS.VERB)) 
			link = new VerbSynsetLink();
		link.setGloss(syn.getGloss());
		link.setId(syn.getOffset());
		return link;
	}

	private void addPointers(HyperGraph graph, Iterator<Synset> it, String synTypeAlias) throws JWNLException
	{
		PtType.initialize();
		while (it.hasNext())
		{
			Synset syn = (Synset) it.next();
			HGHandle synHandle = lookup(graph, synTypeAlias, "id", syn.getOffset());
			if (synHandle == null)
			{
				logger.warning("Could find synset " + syn + " in HGDB while trying to add a pointers to it.");
				continue;
			}
			Pointer[] ps = syn.getPointers();
			for (int i = 0; i < ps.length; i++)
			{
				n_point++;
				SemanticLink semlink = makeSemanticLink(graph, synHandle, ps[i]);
				if (semlink != null)
					graph.add(semlink);
			}
		}
	}

	private void addExceptions(HyperGraph graph, Iterator<Exc> it, int pos)
	{
		while (it.hasNext())
		{
			Exc exc = (Exc) it.next();
			String[] excs = exc.getExceptionArray();
			HGHandle[] targets = new HGHandle[excs.length + 1];
			String lemma = cleanupLemma(exc.getLemma());
			targets[0] = getWordHandle(graph, lemma); 
			for (int i = 0; i < excs.length; i++)
			{
				targets[i + 1] = getWordHandle(graph, cleanupLemma(excs[i]));
			}
			graph.add(createExcLink(targets, pos));
			n_exc++;
		}
	}

	// POS.NOUN, POS.VERB, POS.ADVERB,POS.ADJECTIVE
	private ExcLink createExcLink(HGHandle[] targets, int pos)
	{
		ExcLink link = null;
		if (pos == 0)
			link = new AdjExcLink();
		else if (pos == 1)
			link = new NounExcLink();
		else if (pos == 2)
			link = new AdverbExcLink();
		else if (pos == 3) 
			link = new VerbExcLink();
		link.setTargets(targets);
		return link;
	}


	private SemanticLink makeSemanticLink(HyperGraph graph, HGHandle sourceHandle, Pointer p) throws JWNLException
	{
		PtType ptType = PtType.getPointerTypeForKey(p.getType().getKey()); 
		if (ptType == null)
		{
			return null;
		}
		Class<? extends SemanticLink> clazz = ptType.getClazz();
		SemanticLink semlink = null;
		try
		{
			semlink = clazz.newInstance();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			throw new RuntimeException(
					"Could not instantiate pointer with class: " + clazz);
		}
		HGHandle[] ptrTargets = new HGHandle[2];
		ptrTargets[0] = sourceHandle;
		Synset s1 = getSynsetForPointerTarget(p.getTarget());
		ptrTargets[1] = lookup(graph, 
				   			   this.getTypeAliasPrefix() + 
				   			   synset_alias[Arrays.binarySearch(pos, s1.getPOS(), posComparator)], 
				   			   "id", 
				   			   s1.getOffset()); 
		if (ptrTargets[1] == null)
		{
			logger.warning("Could not create pointer " + p + " because target synset " + s1 + " is not in HGDB.");
			return null;
		}
		semlink.setTargets(ptrTargets);
		return semlink;
	}

	private Synset getSynsetForPointerTarget(PointerTarget p)
	{
		if (p instanceof Synset) return (Synset) p;
		return ((Word) p).getSynset();
	}

	private HGHandle getWordHandle(HyperGraph graph, String lemma)
	{
		HGHandle h = wordIndex.findFirst(lemma);
		if (h == null)
		{
			h = addWord(graph, lemma);
		}
		return h;
	}

	private void addVerbFrames(HyperGraph graph)
	{
		for (int i = 1; i <= VerbFrame.getVerbFramesSize(); i++)
		{
			verbFrames.put(i,
						   graph.getPersistentHandle(
								   graph.add(new org.hypergraphdb.app.wordnet.data.VerbFrame(
										   VerbFrame.getFrame(i), i))));
		}
	}

	private void connectSynsetVerbFrames(HyperGraph graph, HGHandle synHandle, Synset syn)
	{
		BitSet bs = syn.getVerbFrameFlags();
		if (bs == null)
			return;
		int[] fr_inds = VerbFrame.getVerbFrameIndicies(bs);
		for (int i = 0; i < fr_inds.length; i++)
		{
			HGHandle[] targets = new HGHandle[2];
			targets[1] = synHandle;			
			targets[0] = verbFrames.get(fr_inds[i]);
			VerbFrameLink link = new VerbFrameLink(targets);
			graph.add(link);
			n_vlinks++;
		}
	}
	
	
	public static void traverse(HyperGraph graph)
	{
	    WNGraph wng = new WNGraph(graph);	
	    HGHandle word1 = wng.findWord("travel");
	    HGDepthFirstTraversal traversal = 
	        new HGDepthFirstTraversal(word1, new SimpleALGenerator(graph));	                 
        while (traversal.hasNext() ) 
        {   
            Pair<HGHandle, HGHandle> current = traversal.next();
	        org.hypergraphdb.app.wordnet.data.Word atom = graph.get((HGHandle) current.getSecond());
	        System.out.println("Visiting atom  " + atom +   " pointed to by " + 
	                graph.get(current.getSecond()));
        }
	}
	
	public static void main(String [] args)
	{
		if (args.length != 2)
		{
			System.out.println("Usage: HGWordnetLoader dictionaryLocation hypergraphDBLocation");
			System.exit(0);
		}
		//traverse(HGEnvironment.get(args[1]));
		//if (1==1)return;
		
		HGWordNetLoader loader = new HGWordNetLoader();
		loader.setDictionaryLocation(args[0]);
		try
		{
		    HGConfiguration config = new HGConfiguration();
//		    BDBConfig bdbConfig = (BDBConfig)config.getStoreImplementation().getConfiguration();
		    // Change the storage cache from the 20MB default to 500MB
//		    bdbConfig.getEnvironmentConfig().setCacheSize(1000*1024*1024);
		    config.setTransactional(false);
			HyperGraph graph = HGEnvironment.get(args[1]);
			System.out.println("Loading WordNet to " + args[1] + " from dictionary " + args[0]);
			System.out.println("Atom count before : " + hg.count(graph, hg.all()));
			long startTime = System.currentTimeMillis();			
			loader.loadWordNet(graph);
			long endTime = System.currentTimeMillis();
			System.out.println("WordNet sucessfully loaded - " + (endTime - startTime)/1000/60 + " minutes.");
			System.out.println("Atom count after : " + hg.count(graph, hg.all()));
		}
		catch (Throwable t)
		{
			t.printStackTrace();
		}
	}
}