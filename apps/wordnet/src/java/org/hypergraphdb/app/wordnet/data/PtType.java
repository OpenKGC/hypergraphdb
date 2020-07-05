package org.hypergraphdb.app.wordnet.data;

import java.io.Serializable;
import java.util.*;

import net.didion.jwnl.data.POS;
import net.didion.jwnl.util.Resolvable;

/**
 * Instances of this class enumerate the possible WordNet pointer types,
 * and are used to label <code>PtType</code>s. Each <code>PtType</code>
 * carries additional information: a human-readable label, an optional reflexive
 * type that labels links pointing the opposite direction, an encoding of
 * parts-of-speech that it applies to, and a short string that represents it in
 * the dictionary files.
 */
public final class PtType implements Serializable 
{
	static final long serialVersionUID = 220886251671304256L;
	// Flags for tagging a pointer type with the POS types it apples to.
	private static final int N = 1;
	private static final int V = 2;
	private static final int ADJ = 4;
	private static final int ADV = 8;
	private static final int LEXICAL = 16;

	// All categories
	public static final PtType ANTONYM = 
		new PtType(Antonym.class, "ANTONYM", "ANTONYM_KEY", N | V | ADJ | ADV | LEXICAL);
    public static final PtType IN_CATEGORY = 
    	new PtType(InCategory.class, "IN_CATEGORY", "CATEGORY_DOMAIN_KEY", N | V | ADJ | ADV | LEXICAL);
    public static final PtType IN_REGION = 
    	new PtType(InRegion.class, "IN_REGION", "REGION_DOMAIN_KEY", N | V | ADJ | ADV | LEXICAL);
    public static final PtType USAGE = 
    	new PtType(Usage.class, "USAGE_DOMAIN", "USAGE_DOMAIN_KEY", N | V | ADJ | ADV | LEXICAL);

/*    public static final PtType CATEGORY_MEMBER = 
    	new PtType(CategoryMember.class, "CATEGORY_MEMBER", "CATEGORY_MEMBER_KEY", N);
    public static final PtType REGION_MEMBER = 
    	new PtType(RegionMember.class, "REGION_MEMBER", "REGION_MEMBER_KEY", N);
    public static final PtType USAGE_MEMBER = 
    	new PtType(UsageMember.class, "USAGE_MEMBER", "USAGE_MEMBER_KEY", N); */
    
	// Nouns and Verbs

	public static final PtType KIND_OF = 
		new PtType(KindOf.class, "KIND_OF", "HYPERNYM_KEY", N | V);
//	public static final PtType HYPONYM = 
//		new PtType(Hyponim.class, "HYPONYM", "HYPONYM_KEY", N | V);
    public static final PtType NOMINALIZATION = 
    	new PtType(Nominalization.class, "NOMINALIZATION", "NOMINALIZATION_KEY", N | V);

	// Nouns and Adjectives

	public static final PtType ATTRIBUTE = 
		new PtType(Attribute.class, "ATTRIBUTE", "ATTRIBUTE_KEY", N | ADJ);
	public static final PtType SEE_ALSO = 
		new PtType(AlsoSee.class, "ALSO_SEE", "ALSO_SEE_KEY", N | V | ADJ | LEXICAL);

    // Nouns

    public static final PtType MEMBER_OF = 
    	new PtType(MemberOf.class, "MEMBER_OF", "MEMBER_HOLONYM_KEY", N);
//    public static final PtType MEMBER_MERONYM = 
//    	new PtType(MemberMeronym.class, "MEMBER_MERONYM", "MEMBER_MERONYM_KEY", N);    
    public static final PtType SUBSTANCE_OF = 
    	new PtType(SubstanceOf.class, "SUBSTANCE_OF", "SUBSTANCE_HOLONYM_KEY", N);
//    public static final PtType SUBSTANCE_MERONYM = 
//    	new PtType(SubstanceMeronym.class, "SUBSTANCE_MERONYM", "SUBSTANCE_MERONYM_KEY", N);    
    public static final PtType PART_OF = 
    	new PtType(PartOf.class, "PART_OF", "PART_HOLONYM_KEY", N);
//    public static final PtType PART_MERONYM = 
//    	new PtType(PartMeronym.class, "PART_MERONYM", "PART_MERONYM_KEY", N);

    //since WordNet 2.1
    public static final PtType INSTANCE_OF = 
    	new PtType(InstanceOf.class, "INSTANCE_OF", "INSTANCE_HYPERNYM_KEY", N);
//    public static final PtType INSTANCE_HYPONYM = 
//    	new PtType(InstanceHyponym.class, "INSTANCE_HYPONYM", "INSTANCE_HYPONYM_KEY", N);
  
	// Verbs

	public static final PtType ENTAILS = 
		new PtType(Entails.class, "ENTAILS", "ENTAILMENT_KEY", V);
	public static final PtType CAUSE = 
		new PtType(Cause.class, "CAUSE", "CAUSE_KEY", V);
	public static final PtType VERB_GROUP = 
		new PtType(VerbGroup.class, "VERB_GROUP", "VERB_GROUP_KEY", V);

	// Adjectives
	public static final PtType SIMILAR_TO = 
		new PtType(Similar.class, "SIMILAR", "SIMILAR_KEY", ADJ);
	public static final PtType PARTICIPLE_OF = 
		new PtType(ParticipleOf.class, "PARTICIPLE_OF", "PARTICIPLE_OF_KEY", ADJ | LEXICAL);
	public static final PtType PERTAINYM = 
		new PtType(Pertainym.class, "PERTAINYM", "PERTAINYM_KEY", ADJ | LEXICAL);

	// Adverbs
	public static final PtType DERIVED = 
		new PtType(DerivedFrom.class, "DERIVED", "DERIVED_KEY", ADV);

	/** A list of all <code>PtType</code>s. */
	private static final List<PtType> ALL_TYPES = Collections.unmodifiableList(Arrays.asList(new PtType[] {
		ANTONYM, IN_CATEGORY, IN_REGION, USAGE, KIND_OF, NOMINALIZATION, ATTRIBUTE, SEE_ALSO,
		MEMBER_OF, SUBSTANCE_OF, PART_OF, INSTANCE_OF, ENTAILS, CAUSE, VERB_GROUP, SIMILAR_TO,
		PARTICIPLE_OF, PERTAINYM, DERIVED
    }));

    private static final Map<POS, Integer> POS_TO_MASK_MAP = new HashMap<POS, Integer>();
    private static final Map<String, PtType> KEY_TO_POINTER_TYPE_MAP = new HashMap<String, PtType>();
    private static final Map<Class<? extends SemanticLink>, PtType> CLASS_TO_POINTER_TYPE_MAP = 
    	new HashMap<Class<? extends SemanticLink>, PtType>();

    private static boolean _initialized = false;

    public static void initialize() {
        if (!_initialized) {
            POS_TO_MASK_MAP.put(POS.NOUN, new Integer(N));
            POS_TO_MASK_MAP.put(POS.VERB, new Integer(V));
            POS_TO_MASK_MAP.put(POS.ADJECTIVE, new Integer(ADJ));
            POS_TO_MASK_MAP.put(POS.ADVERB, new Integer(ADV));

            for (Iterator<PtType> itr = ALL_TYPES.iterator(); itr.hasNext();) 
            {
                PtType pt = itr.next();
                KEY_TO_POINTER_TYPE_MAP.put(pt.getKey(), pt);
                CLASS_TO_POINTER_TYPE_MAP.put(pt.getClazz(), pt);
            }
            _initialized = true;
        }
    }

    /**
	 * Return the <code>PtType</code> whose key matches <var>key</var>.
	 */
    public static PtType getPointerTypeForKey(String key) {
		return (PtType) KEY_TO_POINTER_TYPE_MAP.get(key);
	}
    
	public static PtType getPointerTypeForClass(Class<? extends SemanticLink> key) {
		return (PtType) CLASS_TO_POINTER_TYPE_MAP.get(key);
	}

    public static List<PtType> getAllPointerTypes() {
        return ALL_TYPES;
    }

    public static List<PtType> getAllPointerTypesForPOS(POS pos) 
    {
        List<PtType> types = new ArrayList<PtType>();
        for (Iterator<PtType> itr = ALL_TYPES.iterator(); itr.hasNext();) 
        {
            PtType pt = itr.next();
            if (pt.appliesTo(pos)) 
            {
                types.add(pt);
            }
        }
        return Collections.unmodifiableList(types);
    }

	private static int getPOSMask(POS pos) {
		return ((Integer) POS_TO_MASK_MAP.get(pos)).intValue();
	}

	private Resolvable _label;
	private Resolvable _key;
	private int _flags;
	private Class<? extends SemanticLink> clazz;

	private PtType(Class<? extends SemanticLink> clazz, String label, String key, int flags) {
		_label = new Resolvable(label);
		_key = new Resolvable(key);
		_flags = flags;
		this.setClazz(clazz);
	}

	public String toString() 
	{
		return getLabel() + "-" + getKey() + "-" + getClazz().getName();
	}

	public String getKey() {
		return _key.toString().trim();
	}

	public String getLabel() {
		return _label.toString().trim();
	}

	private void setClazz(Class<? extends SemanticLink> clazz) {
		this.clazz = clazz;
	}

	public Class<? extends SemanticLink> getClazz() {
		return clazz;
	}

	/** Whether or not this PtType can be associated with <code>pos</code> */
	public boolean appliesTo(POS pos) {
		return (_flags & getPOSMask(pos)) != 0;
	}

	public int hashCode() {
		return getLabel().hashCode();
	}
}