package org.hypergraphdb.app.tm;

import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HGQuery.hg;
import org.hypergraphdb.atom.HGRel;
import org.hypergraphdb.util.HGUtils;
import org.tmapi.core.Association;
import org.tmapi.core.AssociationRole;
import org.tmapi.core.Locator;
import org.tmapi.core.Occurrence;
import org.tmapi.core.TMAPIRuntimeException;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMapObject;
import org.tmapi.core.TopicName;
import org.tmapi.core.Variant;
import org.w3c.dom.*;

import java.net.URLDecoder;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class TMXMLProcessor
{
	private String version = "1.0";
	private HGTopicMapSystem system;
	private boolean merge;
	private String iri;
	
	private Set<String> getItemIdentities(Element el, boolean examineAllChildren)
	{
		HashSet<String> result = new HashSet<String>();	
		NodeList kids = el.getChildNodes();
		for (int i = 0; i < kids.getLength(); i++)
		{
			Node n = kids.item(i);
			if (! (n instanceof Element))
				continue;
			else if (n.getNodeName().equals("itemIdentity"))
				result.add(U.handleIRI(((Element)n).getAttribute("href")));
			else if (!examineAllChildren)
				break;
		}
		return result;
	}

	/**
	 * <p>
	 * Retrieve the list of <code>Locator</code>s corresponding to the list
	 * of locator references. If a locator with a given reference does not exist, 
	 * add it to the system.
	 * </p>
	 * 
	 * @param uris A list of well-formed locator references.
	 * @return A list of locators, all already stored in the graph.
	 */
	private Set<HGHandle> getStoredLocators(Set<String> uris)
	{
		HashSet<HGHandle> result = new HashSet<HGHandle>();
		for (String u : uris)
			result.add(U.ensureLocator(system.getGraph(), U.makeLocator(iri, u)));
		return result;
	}
	
	private void syncLocators(Set<HGHandle> locators, Set<HGHandle> toAdd, HGTopicMapObjectBase object)
	{
		if (!merge)
		{
			Set<Locator> existingLocators = object.getSourceLocators();
			for (Locator l : existingLocators)
			{
				if (!locators.contains(system.getGraph().getHandle(l)))
					object.removeSourceLocator(l);
			}
		}
		HGHandle oHandle = system.getGraph().getHandle(object);
		for (HGHandle h : toAdd)
			system.getGraph().add(new HGRel(HGTM.SourceLocator, 
										    new HGHandle[] { h, oHandle} ),
								  HGTM.hSourceLocator);		
	}
	
	private void syncScopes(HGScopedObject x, Set<HGTopic> scope)
	{
		Set<HGTopic> existingScope = x.getScope();
		for (HGTopic t : scope)
			if (!existingScope.contains(t))
				x.addScopingTopic(t);
		if (!merge)
			for (HGTopic t : existingScope)
				if (!scope.contains(t))
					x.removeScopingTopic(t);		
	}
	
	private HGTopicMapObjectBase locate(Set<HGHandle> locators, Class type, Set<HGHandle> missing)
	{
		HGTopicMapObjectBase result = null;
		for (HGHandle h : locators)
		{
			Object x = U.getOneRelated(system.getGraph(), HGTM.hSourceLocator, h, null);
			if (x == null)
				missing.add(h);
			else if (type.isAssignableFrom(x.getClass()))
			{
				if (result == null)
					result = (HGTopicMapObjectBase)x;
				else if (result != x)
					throw new RuntimeException("Attempt a add a topic map object '" + type.getName() + 
							"' with a locator '" +
							((Locator)system.getGraph().get(h)).getReference() + "'" +
							" that is already used for another object '" + x + "'");
			}
			else
				throw new RuntimeException("Attempt a add a topic map  object '" + type.getName() + 
						"' with a locator '" +
						((Locator)system.getGraph().get(h)).getReference() + "'" +
						" that is already used for another object '" + x + "'");
		}		
		return result;
	}

	private HGTopic getTopicRef(Element el, HGTopicMap map)
	{
		String href = U.handleIRI(el.getAttribute("href"));
		Locator l = U.ensureLocator(system.getGraph(), iri, href);
		HGHandle lh = system.getGraph().getHandle(l);
		HGTopic topic = (HGTopic)U.getOneRelated(system.getGraph(), HGTM.hSubjectIdentifier, lh, null);
		if (topic == null)
			topic = (HGTopic)U.getOneRelated(system.getGraph(), HGTM.hSourceLocator, lh, null);
		if (topic == null)
		{
			topic = (HGTopic)map.createTopic();
			topic.addSourceLocator(l);
		}
		return topic;
	}
	
	private Set<HGTopic> getTopicRefs(Element el, HGTopicMap map)
	{		
		HashSet<HGTopic> result = new HashSet<HGTopic>();
		NodeList kids = el.getChildNodes();
		for (int i = 0; i < kids.getLength(); i++)
		{
			Node n = kids.item(i);
			if (! (n instanceof Element))
				continue;
			else if (n.getNodeName().equals("topicRef"))
				result.add(getTopicRef((Element)n, map));
		}
		return result;
	}
	
	private void handleReifier(String reifier, HGTopicMap map, HGHandle object)
	{		
		if (reifier == null || reifier.length() == 0)
			return;
		reifier = U.handleIRI(reifier);
		Locator l = U.makeLocator(reifier);
		HGHandle rl = U.ensureLocator(system.getGraph(), l);
		HGTopic rtopic = (HGTopic)U.getOneRelated(system.getGraph(), HGTM.hSourceLocator, rl, null);
		if (rtopic == null)
		{
			rtopic = (HGTopic)map.createTopic();
			rtopic.addSourceLocator(l);
		}
		U.setReifierOf(system.getGraph(), 
					   object, 
					   system.getGraph().getHandle(rtopic));
	}
	
	private void load2(Element top) throws Exception
	{		
		Set<HGHandle> locators = getStoredLocators(getItemIdentities(top, false));
		HashSet<HGHandle> toAdd = new HashSet<HGHandle>();
		HGTopicMap tm = (HGTopicMap)system.getTopicMap(iri);
		if (tm == null)
			tm = (HGTopicMap)locate(locators, HGTopicMap.class, toAdd);		
		if (tm == null)
			tm = (HGTopicMap)system.createTopicMap(iri);
		else
		{
			tm.setBaseLocator(U.ensureLocator(system.getGraph(), null, iri));
			system.getGraph().update(tm);
		}
		syncLocators(locators, toAdd, tm);		
		NodeList kids = top.getChildNodes();
		for (int i = 0; i < kids.getLength(); i++)
		{
			Node n = kids.item(i);
			if (! (n instanceof Element))
				continue;
			else if (n.getNodeName().equals("topic"))
				loadTopic2((Element)n, tm);
			else if (n.getNodeName().equals("association"))
				loadAssociation2((Element)n, tm);
		}		
	}
	
	private void loadVariant2(Element el, HGTopicMap map, HGTopicName name)
	{
		Set<HGHandle> locators = getStoredLocators(getItemIdentities(el, false));
		Set<HGHandle> toAdd = new HashSet<HGHandle>();
		HGVariant var = (HGVariant)locate(locators, HGVariant.class, toAdd);
		String value = "";
		Locator dataType = null;
		Set<HGTopic> scope = new HashSet<HGTopic>();
		
		int idx;		
		NodeList kids = el.getChildNodes();
		for (idx = 0; idx < kids.getLength(); idx++)
		{
			Node n = kids.item(idx);
			if (! (n instanceof Element))
				continue;
			else if (n.getNodeName().equals("scope"))
			{
				scope = getTopicRefs((Element)n, map);
			}
			else if (n.getNodeName().equals("resourceRef"))
			{
				String href = ((Element)n).getAttribute("href");
				value = U.handleIRI(href);
				dataType = (Locator)system.getGraph().get(HGTM.hSchemaAnyURI);
			}
			else if (n.getNodeName().equals("resourceData"))
			{
				Element e = (Element)n;
				String dtAttr = e.getAttribute("datatype");
				if (dtAttr == null)
					dtAttr = HGTM.schemaStringType;
				dataType = U.ensureLocator(system.getGraph(), null, dtAttr);				
				if (dtAttr.equals(HGTM.schemaAnyType))
					value = TMXMLUtils.canonicalizeContent(e);
				else if (dtAttr.equals(HGTM.schemaAnyURI))
					value = U.handleIRI(n.getTextContent());
				else
					value = n.getTextContent();
			}
			else if (n.getNodeName().equals("itemIdentity"))
				continue;
			else
				break;
		}
		scope.addAll(name.getScope());
		if (var == null)
			var = (HGVariant)name.createVariant(value, dataType, scope);
		else 
		{
			var.setValue(value);
			var.setDataType(dataType);
			syncScopes(var, (Set<HGTopic>)scope);
			system.getGraph().update(var);
		}		
		syncLocators(locators, toAdd, var);
		handleReifier(el.getAttribute("reifier"), map, system.getGraph().getHandle(var));
	}
	
	private void loadName2(Element el, HGTopicMap map, HGTopic topic)
	{
		Set<HGHandle> locators = getStoredLocators(getItemIdentities(el, false));
		Set<HGHandle> toAdd = new HashSet<HGHandle>();
		HGTopicName name = (HGTopicName)locate(locators, HGTopicName.class, toAdd);
		String value = "";
		HGTopic type = null;
		Set<HGTopic> scope = null;
		
		int idx;		
		NodeList kids = el.getChildNodes();
		for (idx = 0; idx < kids.getLength(); idx++)
		{
			Node n = kids.item(idx);
			if (! (n instanceof Element))
				continue;
			else if (n.getNodeName().equals("type"))
			{
				Set<HGTopic> types = getTopicRefs((Element)n, map);
				if (types.size() > 0)
					type = types.iterator().next();
			}
			else if (n.getNodeName().equals("scope"))
			{
				scope = getTopicRefs((Element)n, map);
			}
			else if (n.getNodeName().equals("value"))
			{
				value = n.getTextContent();
			}
			else if (n.getNodeName().equals("itemIdentity"))
				continue;
			else
				break;
		}
		
		if (type == null)
			type = (HGTopic)system.getGraph().get(HGTM.hTopicNameTopic);
		
		if (name == null)
			name = (HGTopicName)topic.createTopicName(value, type, scope);
		else 
		{
			name.setType(type);
			name.setValue(value);
			system.getGraph().update(name);
			syncScopes(name, scope);
		}
		syncLocators(locators, toAdd, name);

		// 
		// Add variants
		//
		for (; idx < kids.getLength(); idx++)
		{
			Node n = kids.item(idx);
			if (! (n instanceof Element))
				continue;
			else if (n.getNodeName().equals("variant"))
			{
				loadVariant2((Element)el, map, name);
			}
			else
				throw new RuntimeException("Unexpected element '" + 
						n.getNodeName() + "' in topic name tag with value '" + 
						value + "'");
		}
		
		//
		// Once the name has been properly added, we can add the reifier property, is present.
		//
		handleReifier(el.getAttribute("reifier"), map, system.getGraph().getHandle(name));
	}
	
	private void loadOccurrence2(Element el, HGTopicMap map, HGTopic topic)
	{
		Set<HGHandle> locators = getStoredLocators(getItemIdentities(el, false));
		Set<HGHandle> toAdd = new HashSet<HGHandle>();
		HGOccurrence occ = (HGOccurrence)locate(locators, HGOccurrence.class, toAdd);
		String value = "";
		Locator dataType = null;
		HGTopic type = null;
		Set<HGTopic> scope = null;
		
		int idx;		
		NodeList kids = el.getChildNodes();
		for (idx = 0; idx < kids.getLength(); idx++)
		{
			Node n = kids.item(idx);
			if (! (n instanceof Element))
				continue;
			else if (n.getNodeName().equals("type"))
			{
				Set<HGTopic> types = getTopicRefs((Element)n, map);
				if (types.size() > 0)
					type = types.iterator().next();
			}
			else if (n.getNodeName().equals("scope"))
			{
				scope = getTopicRefs((Element)n, map);
			}
			else if (n.getNodeName().equals("resourceRef"))
			{
				String href = ((Element)n).getAttribute("href");
				value = U.handleIRI(href);
				dataType = (Locator)system.getGraph().get(HGTM.hSchemaAnyURI);
			}
			else if (n.getNodeName().equals("resourceData"))
			{
				Element e = (Element)n;
				String dtAttr = e.getAttribute("datatype");
				if (dtAttr == null)
					dtAttr = HGTM.schemaStringType;
				dataType = U.ensureLocator(system.getGraph(), null, dtAttr);				
				if (dtAttr.equals(HGTM.schemaAnyType))
					value = TMXMLUtils.canonicalizeContent(e);
				else if (dtAttr.equals(HGTM.schemaAnyURI))
					value = U.handleIRI(n.getTextContent());
				else
					value = n.getTextContent();
			}
			else if (n.getNodeName().equals("itemIdentity"))
				continue;
			else
				break;
		}
		
		if (occ == null)
			occ= (HGOccurrence)topic.createOccurrence(value, type, scope);
		else 
		{
			occ.setType(type);
			occ.setValue(value);
			occ.setDataType(dataType);
			system.getGraph().update(occ);
			syncScopes(occ, scope);			
		}
		syncLocators(locators, toAdd, occ);
		handleReifier(el.getAttribute("reifier"), map, system.getGraph().getHandle(occ));		
	}

	private HGAssociationRole loadRole2(Element el, HGAssociation ass, HGTopicMap map)
	{
		Set<HGHandle> locators = getStoredLocators(getItemIdentities(el, false));
		Set<HGHandle> toAdd = new HashSet<HGHandle>();
		HGAssociationRole role = (HGAssociationRole)locate(locators, HGAssociationRole.class, toAdd);
	  	HGTopic player = null, type = null;
		NodeList kids = el.getChildNodes();
		for (int i = 0; i < kids.getLength(); i++)
		{
			Node n = kids.item(i);
			if (! (n instanceof Element))
				continue;
			else if (n.getNodeName().equals("type"))
			{
				Set<HGTopic> refs = getTopicRefs((Element)n, map);
				if (refs.size() == 0) throw new RuntimeException("No role type defined for " + role);
				type = refs.iterator().next();
			}
			else if (n.getNodeName().equals("topicRef"))
			{
				 player = getTopicRef((Element)n, map);
			}
		}
		if (type == null)
			throw new RuntimeException("Role without a type " + role);
		if (player == null)
			throw new RuntimeException("Role without a player " + role);		
		if (role != null)
		{
			if (role.getAssociation() != ass)
				throw new RuntimeException("Attempting to reassign a role " + 
										   role + " to a different association.");
			boolean update = false;
			if (role.getPlayer() != player) { role.setPlayer(player); update = true; }
			if (role.getType() != type) { role.setType(type); update = true; }
			if (update) system.getGraph().update(role);
		}
		else
		{
			HGHandle [] roleTargets = new HGHandle[] { system.getGraph().getHandle(player), 
													   system.getGraph().getHandle(type), 
													   system.getGraph().getHandle(ass)};
			HGHandle hRole = hg.findOne(system.getGraph(), hg.and(hg.type(HGAssociationRole.class),
																  hg.orderedLink(roleTargets)));
			if (hRole != null)
				role = (HGAssociationRole)system.getGraph().get(hRole);
			else
			{
				role = new HGAssociationRole(roleTargets);
				role.graph = system.getGraph();
				system.getGraph().add(role);
			}
		}
		handleReifier(el.getAttribute("reifier"), map, system.getGraph().getHandle(role));		
		return role;
	}
	
	private void loadTopic2(Element el, HGTopicMap map)
	{
		String id = el.getAttribute("id");
		Set<HGHandle> locators = getStoredLocators(getItemIdentities(el, true));
		if (!HGUtils.isEmpty(id))
			locators.add(U.ensureLocator(system.getGraph(), U.makeLocator(iri + "#" + id)));
		Set<HGHandle> toAdd = new HashSet<HGHandle>();
		HGTopic topic = (HGTopic)locate(locators, HGTopic.class, toAdd);
		if (topic == null)
			topic = (HGTopic)map.createTopic();
		syncLocators(locators, toAdd, topic);
		Set<Locator> subjectLocators = topic.getSubjectLocators();
		Set<Locator> subjectIdentifiers = topic.getSubjectIdentifiers();
		NodeList kids = el.getChildNodes();
		for (int i = 0; i < kids.getLength(); i++)
		{
			Node n = kids.item(i);
			if (! (n instanceof Element))
				continue;
			else if (n.getNodeName().equals("subjectLocator"))
			{
				String href = U.handleIRI(((Element)n).getAttribute("href"));
				Locator l = U.ensureLocator(system.getGraph(), iri, href);
				if (!subjectLocators.contains(l))
					topic.addSubjectLocator(l);				
			}
			else if (n.getNodeName().equals("subjectIdentifier"))
			{
				String href = U.handleIRI(((Element)n).getAttribute("href"));
				Locator l = U.ensureLocator(system.getGraph(), iri, href);
				if (!subjectIdentifiers.contains(l))
					topic.addSubjectIdentifier(l);
			}
			else if (n.getNodeName().equals("instanceOf"))
			{
				Set<HGTopic> types = getTopicRefs((Element)n, map);
				Set<HGTopic> existingTypes = topic.getTypes();
				for (HGTopic t : types)
				{
					if (!existingTypes.contains(t))
						topic.addType(t);
				}
				if (!merge)
				{
					Set<Topic> existingCopy = new HashSet<Topic>();
					types.addAll(existingTypes);
					for (Topic t : existingCopy)
						if (!types.contains(t))
							topic.removeType(t);
				}
			}
			else if (n.getNodeName().equals("name"))
			{
				loadName2((Element)n, map, topic);
			}
			else if (n.getNodeName().equals("occurrence"))
			{
				loadOccurrence2((Element)n, map, topic);
			}
			else if (n.getNodeName().equals("itemIdentity"))
				continue;
			else
				throw new RuntimeException("Unexpected element '" + 
										   n.getNodeName() + "' in <topic>");			
		}
	}
	
	private void loadAssociation2(Element el, HGTopicMap map)
	{
		Set<HGHandle> locators = getStoredLocators(getItemIdentities(el, true)); 
		Set<HGHandle> toAdd = new HashSet<HGHandle>();
		HGAssociation ass = (HGAssociation)locate(locators, HGAssociation.class, toAdd);
		Set<HGAssociationRole> roles = new HashSet<HGAssociationRole>();
		Set<HGTopic> scope = new HashSet<HGTopic>();
		boolean isnew = false;
		if (ass == null)
		{
			ass = (HGAssociation)map.createAssociation();
			isnew = true;
		}
		syncLocators(locators, toAdd, ass);		
		NodeList kids = el.getChildNodes();
		for (int i = 0; i < kids.getLength(); i++)
		{
			Node n = kids.item(i);
			if (! (n instanceof Element))
				continue;
			else if (n.getNodeName().equals("type"))
			{
				Set<HGTopic> refs = getTopicRefs((Element)n, map);
				if (refs.size() == 0) throw new RuntimeException("No role type defined for " + ass);				
				ass.setType(refs.iterator().next());
			}
			else if (n.getNodeName().equals("scope"))
			{
				scope = getTopicRefs((Element)n, map);				
			}
			else if (n.getNodeName().equals("role"))
			{
				roles.add(loadRole2((Element)n, ass, map));
			}
			else if (n.getNodeName().equals("itemIdentity"))
				continue;
			else
				throw new RuntimeException("Unexpected element '" + 
										   n.getNodeName() + "' in <association>");
		}
		if (!roles.isEmpty())
		{
			Set<HGAssociationRole> existingRoles = new HashSet<HGAssociationRole>();
			existingRoles.addAll(ass.getAssociationRoles());
			Set<HGAssociationRole> removed = new HashSet<HGAssociationRole>();
			boolean setChanged = false;
			if (merge)
			{
				for (HGAssociationRole r : roles)
					if (!existingRoles.contains(r))
					{
						existingRoles.add(r);
						setChanged = true;
					}
			}
			else
			{
				for (HGAssociationRole r : existingRoles)
					if (!roles.contains(r))
					{
						setChanged = true;
						removed.add(r);
					}			
				setChanged = setChanged || existingRoles.size() != roles.size();
				if (setChanged)
					existingRoles = roles;
			}
			if (setChanged)
			{
				HGHandle [] newTargetSet = new HGHandle[existingRoles.size()];
				int i = 0;
				for (HGAssociationRole r : existingRoles)
					newTargetSet[i++] = system.getGraph().getHandle(r);
				ass.setTargetSet(newTargetSet);
				system.getGraph().update(ass);
				for (HGAssociationRole r : removed)
					try { r.remove(); } catch (Exception ex) { throw new TMAPIRuntimeException(ex); }
			}
		}
		else if (isnew)
			throw new RuntimeException("Association " + ass + " defined with no roles whatsoever.");
		this.syncScopes(ass, scope);
		handleReifier(el.getAttribute("reifier"), map, system.getGraph().getHandle(ass));		
	}
	
	public TMXMLProcessor(HGTopicMapSystem system, String iri)
	{
		this(system, false, iri);
	}
	
	public TMXMLProcessor(HGTopicMapSystem system, boolean merge, String iri)
	{
		if (system == null)
			throw new IllegalArgumentException("system parameter is null");
		if (iri == null || iri.length() == 0)
			throw new IllegalArgumentException("IRI of document is null or empty.");
		this.system = system;
		this.merge = merge;
		this.iri = U.handleIRI(iri);		
	}
	
	public void loadTo(Document doc)
	{
		try
		{
			Element top = doc.getDocumentElement();
			String vattr = top.getAttribute("version");
			if (vattr != null)
				version = vattr;
			if ("2.0".equals(version))
				load2(top);
			else 
				load1(top);
		}
		catch (Exception ex)
		{
			throw new RuntimeException(ex);
		}
	}
	
	/**
	 * <p>
	 * Export a topic map into an XML DOM element. 
	 * </p>
	 * 
	 * @param system The topic map system.
	 * @param iri The IRI of the topic map (i.e. its base locator).
	 * @param version The desired XTM version - either 1.0 or 2.0. If <code>null</code>,
	 * version 2.0 is assumed.
	 * @return A DOM <code>Element</code> of the topic map. 
	 */
	public Document getXmlDocument(String version)
	{
		try
		{
			HGTopicMap map = (HGTopicMap)system.locate(iri);
			if (map == null)
				return null;
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.newDocument();
			if (version == null || "2.0".equals(version))
			{
				Element el = doc.createElement("topicMap");
				el.setAttribute("version", "2.0");
				el.setAttribute("xmlns", "http://www.topicmaps.org/xtm/");
//				for (Locator l : map.getSourceLocators())
//					exportHref2(el, "itemIdentity", l.toExternalForm());
				for (Topic t : map.getTopics())
					exportTopic2(el, t);							
				for (Association a : map.getAssociations())
					exportAssociation2(el, a);
				doc.appendChild(el);
				return doc;
			}
			else
			{
				throw new IllegalArgumentException("Unsupport XTM version " + version);
			}			
		}		
		catch (Exception ex)
		{
			throw new RuntimeException(ex);
		}
	}
	
	private String getTopicHref(Topic t)
	{
		String href = ((Locator)t.getSourceLocators().iterator().next()).toExternalForm();
/*		if (href.startsWith(iri + "#"))
			return href.substring(iri.length() + 1);
		else */
			return href; 
	}
	
	private void exportTopic2(Element parentEl, Topic t)
	{
		Element el = parentEl.getOwnerDocument().createElement("topic");
		for (Object x : t.getSourceLocators())
		{
			String href = ((Locator)x).toExternalForm();			
			if (href.startsWith(iri + "#") && (el.getAttribute("id") == null || el.getAttribute("id").length() == 0))
				el.setAttribute("id", href.substring(iri.length() + 1));
			else
				exportHref2(el, "itemIdentity", href);
		}
		for (Object x : t.getSubjectLocators())
			exportHref2(el, "subjectLocator", ((Locator)x).toExternalForm());
		for (Object x : t.getSubjectIdentifiers())
			exportHref2(el, "subjectIdentifier", ((Locator)x).toExternalForm());
		if (t.getTypes() != null && t.getTypes().size() > 0)
		{
			Element typesEl = el.getOwnerDocument().createElement("instanceOf");
			el.appendChild(typesEl);
			for (Object x : t.getTypes())
			{
				Topic type = (Topic)x;
				if (type.getSourceLocators().isEmpty())
					continue;
				exportHref2(typesEl, "topicRef", getTopicHref(type));
			}
		}
		for (Object n : t.getTopicNames())
			exportName2(el, (TopicName)n);
		for (Object o : t.getOccurrences())
			exportOccurrence2(el, (Occurrence)o);
		//
		// TODO: what about the topic ID?
		//
		parentEl.appendChild(el);
	}
	
	private void exportName2(Element parentEl, TopicName name)
	{
		Element el = parentEl.getOwnerDocument().createElement("name");
		for (Object x : name.getSourceLocators())
			exportHref2(el, "itemIdentity", ((Locator)x).toExternalForm());
		Topic type = name.getType();
		if (name.getType() != null)
		{
			Set<Locator> typeIds = type.getSourceLocators();
			for (Locator l : typeIds)
				if (l.toExternalForm().equals(HGTM.topicNameIdentifier))
				{
					type = null;
					break;
				}
			if (type != null)
				exportType2(el, name.getType());
		}			
		if (name.getScope() != null && !name.getScope().isEmpty())
			exportScope2(el, name.getScope());
		exportValue2(el, name.getValue());
		if (name.getVariants() != null && !name.getVariants().isEmpty())
			exportVariants2(el, name.getVariants());
		if (name.getReifier() != null)
			el.setAttribute("reifier", getTopicHref(name.getReifier())); 
		parentEl.appendChild(el);		
	}
	
	private void exportType2(Element parentEl, Topic type)
	{
		Element el = parentEl.getOwnerDocument().createElement("type");
		exportHref2(el, "topicRef", getTopicHref(type));
		parentEl.appendChild(el);
	}
	
	private void exportScope2(Element parentEl, Collection scope)
	{
		Element el = parentEl.getOwnerDocument().createElement("scope");
		for (Object x : scope)
		{
			exportHref2(el, "topicRef", getTopicHref((Topic)x));
		}
		parentEl.appendChild(el);
	}
	
	private void exportValue2(Element parentEl, String value)
	{
		Element el = parentEl.getOwnerDocument().createElement("value");
		el.appendChild(parentEl.getOwnerDocument().createTextNode(value));
		parentEl.appendChild(el);
	}
	
	private void exportVariants2(Element parentEl, Collection variants)
	{
		for (Object x : variants)
		{
			Variant v = (Variant)x;
			Element el = parentEl.getOwnerDocument().createElement("variant");
			for (Object id : v.getSourceLocators())
				exportHref2(el, "itemIdentity", ((Locator)id).toExternalForm());
			if (v.getResource() != null  &&  v.getResource().toExternalForm().equals(HGTM.schemaAnyURI))
				exportHref2(el, "resourceRef", v.getValue());
			else
				exportResourceData2(el, v.getResource(), v.getValue());
			if (v.getReifier() != null)
				el.setAttribute("reifier", getTopicHref(v.getReifier()));
			parentEl.appendChild(el);
		}		
	}
	
	private void exportResourceData2(Element parentEl, Locator dataType, String value)
	{
		Element el = parentEl.getOwnerDocument().createElement("resourceData");
	    // tricky, we aren't really supporting "any markup"!
		el.appendChild(el.getOwnerDocument().createTextNode(value));
		parentEl.appendChild(el);		
	}
	
	private void exportOccurrence2(Element parentEl, Occurrence o)
	{
		Element el = parentEl.getOwnerDocument().createElement("occurrence");
		for (Object id : o.getSourceLocators())
			exportHref2(el, "itemIdentity", ((Locator)id).toExternalForm());
		if (o.getType() != null)
			exportType2(el, o.getType());
		if (o.getScope() != null && !o.getScope().isEmpty())
			exportScope2(el, o.getScope());		
		if (o.getResource() != null  &&  o.getResource().toExternalForm().equals(HGTM.schemaAnyURI))
			exportHref2(el, "resourceRef", o.getValue());
		else
			exportResourceData2(el, o.getResource(), o.getValue());
		if (o.getReifier() != null)
			el.setAttribute("reifier", getTopicHref(o.getReifier()));	
		parentEl.appendChild(el);
	}
	
	private void exportAssociation2(Element parentEl, Association a)
	{
		Element el = parentEl.getOwnerDocument().createElement("association");
		for (Object id : a.getSourceLocators())
			exportHref2(el, "itemIdentity", ((Locator)id).toExternalForm());
		if (a.getType() != null)
			exportType2(el, a.getType());
		if (a.getScope() != null && !a.getScope().isEmpty())
			exportScope2(el, a.getScope());
		for (Object r : a.getAssociationRoles())
			exportRole2(el, (AssociationRole)r);
		if (a.getReifier() != null)
			el.setAttribute("reifier", getTopicHref(a.getReifier()));			
		parentEl.appendChild(el);
	}
	
	private void exportRole2(Element parentEl, AssociationRole r)
	{
		Element el = parentEl.getOwnerDocument().createElement("role");
		for (Object id : r.getSourceLocators())
			exportHref2(el, "itemIdentity", ((Locator)id).toExternalForm());
		if (r.getType() != null)
			exportType2(el, r.getType());
		exportHref2(el, "topicRef", getTopicHref(r.getPlayer()));
		if (r.getReifier() != null)
			el.setAttribute("reifier", getTopicHref(r.getReifier()));			
		parentEl.appendChild(el);		
	}
	
	private void exportHref2(Element parentEl, String tagName, String href)
	{
		Element el = parentEl.getOwnerDocument().createElement(tagName);
		el.setAttribute("href", href);
		parentEl.appendChild(el);
	}	
	
	//-------------------------------------------------------------------------
	// XTM 1.0
	//-------------------------------------------------------------------------
	private void load1(Element top) throws Exception
	{
		if (!top.getNodeName().equals("topicMap"))
			throw new RuntimeException("Unrecognized document element: " + top.getNodeName());
		HashSet<HGHandle> toAdd = new HashSet<HGHandle>();
		HGTopicMap tm = (HGTopicMap)system.getTopicMap(iri);
		if (tm == null)
			tm = (HGTopicMap)system.createTopicMap(iri);
		else
		{
			tm.setBaseLocator(U.ensureLocator(system.getGraph(), null, iri));
			system.getGraph().update(tm);
		}
		NodeList kids = top.getChildNodes();
		for (int i = 0; i < kids.getLength(); i++)
		{
			Node n = kids.item(i);
			if (! (n instanceof Element))
				continue;
			else if (n.getNodeName().equals("topic"))
				loadTopic1((Element)n, tm);
			else if (n.getNodeName().equals("association"))
				loadAssociation1((Element)n, tm);
			else if (n.getNodeName().equals("mergeMap"))				
				mergeMap1((Element)n, tm);
		}
	}
	
	private void loadTopic1(Element el, HGTopicMap tm)
	{
		Locator l = U.makeLocalLocator(iri, el.getAttribute("id"));
		HGTopic topic = (HGTopic)system.locate(l); 
		if (topic == null)	
		{
			topic = (HGTopic)tm.createTopic();
			topic.addSourceLocator(l);
		}
		Element subjIdentityElement = TMXMLUtils.findChild(el, "subjectIdentity");
		if (subjIdentityElement != null)
			loadSubjectIdentity1(subjIdentityElement, topic);
		// TODO: we've established all identity "pointers"...time to do merging here...
		
		NodeList kids = el.getChildNodes();
		for (int i = 0; i < kids.getLength(); i++)
		{
			Node n = kids.item(i);
			if (! (n instanceof Element))
				continue;
			else if (n.getNodeName().equals("instanceOf"))
				topic.addType(getInstanceOf1((Element)n, tm));
			else if (n.getNodeName().equals("baseName"))				
				loadBaseName1((Element)n, topic, tm);
			else if (n.getNodeName().equals("occurrence"))				
				loadOccurrence1((Element)n, topic, tm);			
		}
	}
	
	private Topic getInstanceOf1(Element el, HGTopicMap tm)
	{
		NodeList kids = el.getChildNodes();
		for (int i = 0; i < kids.getLength(); i++)
		{
			Node n = kids.item(i);
			if (! (n instanceof Element))
				continue;
			else if (n.getNodeName().equals("topicRef"))
			{
				return this.getTopicByRef1((Element)n, tm);
			}
			else if (n.getNodeName().equals("subjectIndicatorRef"))				
			{
				return getTopicByIndicator1((Element)n, tm);
			}
		}	
		throw new RuntimeException("No topic specified in instanceOf tag.");
	}

	private void loadSubjectIdentity1(Element el, HGTopic topic)
	{
		NodeList kids = el.getChildNodes();
		for (int i = 0; i < kids.getLength(); i++)
		{
			Node n = kids.item(i);
			if (! (n instanceof Element))
				continue;
			else if (n.getNodeName().equals("resourceRef"))
			{
				Locator l = getHref1((Element)n);
				topic.addSubjectLocator(l);				
			}
			else if (n.getNodeName().equals("topicRef"))
			{
				Locator l = getHref1((Element)n);
				topic.addSourceLocator(l);
			}
			else if (n.getNodeName().equals("subjectIndicatorRef"))				
			{
				Locator l = getHref1((Element)n);
				topic.addSubjectIdentifier(l);
			}		
		}					
	}

	private void loadBaseName1(Element el, HGTopic topic, HGTopicMap tm)
	{
		Set<Topic> scope = null;
		String value = null;
		NodeList kids = el.getChildNodes();
		for (int i = 0; i < kids.getLength(); i++)
		{
			Node n = kids.item(i);
			if (! (n instanceof Element))
				continue;
			else if (n.getNodeName().equals("scope"))
			{
				scope = getScope1((Element)n, tm);
			}
			else if (n.getNodeName().equals("baseNameString"))
			{
				value = n.getTextContent();
			}		
		}	

		TopicName name = topic.createTopicName(value, scope);
		
		kids = el.getChildNodes();
		for (int i = 0; i < kids.getLength(); i++)
		{
			Node n = kids.item(i);			
			if (n.getNodeName().equals("variant"))
				loadVariant1((Element)n, name, new HashSet<Topic>(), tm);
		}
	}
	
	private void loadVariant1(Element el, TopicName name, Set<Topic> currentScope, HGTopicMap tm)
	{
		Set<Topic> thisScope = new HashSet<Topic>();
		Locator resourceRef = null;
		String resourceData = null;
		NodeList kids = el.getChildNodes();
		for (int i = 0; i < kids.getLength(); i++)
		{
			Node n = kids.item(i);			
			if (n.getNodeName().equals("parameters"))
				thisScope = this.getScope1((Element)el, tm); 
			else if (n.getNodeName().equals("variantName"))
			{
				NodeList vkids = ((Element)n).getChildNodes();
				for (int j = 0; j < vkids.getLength(); j++)
				{
					Node m = vkids.item(j);
					if (m.getNodeName().equals("resourceRef"))
						resourceRef = this.getHref1((Element)m);
					else if (m.getNodeName().equals("resourceData"))
						resourceData = m.getTextContent();
				}
			}
		}

		if (resourceRef != null)
			name.createVariant(resourceRef, thisScope);
		else if (resourceData != null)
			name.createVariant(resourceData, thisScope);
		else
			throw new RuntimeException("Neither resourceRef nor resourceData present in variant.");
		
		kids = el.getChildNodes();
		for (int i = 0; i < kids.getLength(); i++)
		{
			Node n = kids.item(i);			
			if (n.getNodeName().equals("variant"))
				loadVariant1((Element)n, name, thisScope, tm);
		}
	}
	
	private void loadOccurrence1(Element el, HGTopic topic, HGTopicMap tm)
	{
		Set<Topic> scope = null;
		Locator resourceRef = null;
		String resourceData = null;
		Topic type = null;
		NodeList kids = el.getChildNodes();
		for (int i = 0; i < kids.getLength(); i++)
		{
			Node n = kids.item(i);
			if (! (n instanceof Element))
				continue;
			else if (n.getNodeName().equals("scope"))
			{
				scope = getScope1((Element)n, tm);
			}
			else if (n.getNodeName().equals("instanceOf"))
			{
				type = getInstanceOf1((Element)n, tm);
			}		
			else if (n.getNodeName().equals("resourceRef"))
				resourceRef = this.getHref1((Element)n);
			else if (n.getNodeName().equals("resourceData"))
				resourceData = n.getTextContent();			
		}	
		
		if (resourceRef != null)
			topic.createOccurrence(resourceRef, type, scope);
		else if (resourceData != null)
			topic.createOccurrence(resourceData, type, scope);
		else
			throw new RuntimeException("No reference or data specified in occurrence for topic " + topic);
	}
	
	private void loadAssociation1(Element el, HGTopicMap tm)
	{
		Association ass = tm.createAssociation();
		NodeList kids = el.getChildNodes();		
		for (int i = 0; i < kids.getLength(); i++)
		{
			Node n = kids.item(i);
			if (! (n instanceof Element))
				continue;
			else if (n.getNodeName().equals("scope"))
			{
				for (Topic t : getScope1((Element)n, tm))
					ass.addScopingTopic(t);
			}
			else if (n.getNodeName().equals("instanceOf"))
			{
				ass.setType(getInstanceOf1((Element)n, tm));
			}		
			else if (n.getNodeName().equals("member"))
			{
				Topic type = null;
				Set<Topic> players = new HashSet<Topic>();
				NodeList mkids = ((Element)n).getChildNodes();
				for (int j = 0; j < mkids.getLength(); j++)
				{
					Node m = mkids.item(j);
					if (m.getNodeName().equals("roleSpec"))
						type = getInstanceOf1((Element)m, tm);
					else if (m.getNodeName().equals("topicRef"))
						players.add(getTopicByRef1((Element)m, tm));
					else if (m.getNodeName().equals("resourceRef"))
						players.add(this.getTopicByLocator1((Element)m, tm));
					else if (m.getNodeName().equals("subjectIndicatorRef"))
						players.add(this.getTopicByIndicator1((Element)m, tm));
				}
				for (Topic player : players)
					ass.createAssociationRole(player, type);
			}
		}			
	}
	
	private void mergeMap1(Element el, HGTopicMap tm)
	{
		throw new UnsupportedOperationException("Topic map merging not supported yet.");
	}
	
	private HGTopic getTopicByRef1(Element topicRef, HGTopicMap tm)
	{
		Locator l = getHref1(topicRef);
		TopicMapObject result = system.locate(l);
		if (result != null)
		{
			if (! (result instanceof HGTopic))
				throw new RuntimeException("topicRef with href " + topicRef.getAttributeNS("xlink", "href") +
						" does not refer to a topic, but to a " + result.getClass().getName());
			else
				return (HGTopic)result;
		}	
		result = tm.createTopic();
		result.addSourceLocator(l);
		return (HGTopic)result;
	}
	
	private HGTopic getTopicByIndicator1(Element el, HGTopicMap tm)
	{
		Locator l = getHref1(el);
		TopicMapObject result = system.locateByIndicator(l);
		if (result != null)
		{
			if (! (result instanceof HGTopic))
				throw new RuntimeException("topicRef with href " + el.getAttributeNS("xlink", "href") +
						" does not refer to a topic, but to a " + result.getClass().getName());
			else
				return (HGTopic)result;
		}	
		HGTopic t = (HGTopic)tm.createTopic();
		t.addSubjectIdentifier(l);
		return t;		
	}
	
	private HGTopic getTopicByLocator1(Element el, HGTopicMap tm)
	{
		Locator l = getHref1(el);
		TopicMapObject result = system.locateBySubject(l);
		if (result != null)
		{
			if (! (result instanceof HGTopic))
				throw new RuntimeException("topicRef with href " + el.getAttributeNS("xlink", "href") +
						" does not refer to a topic, but to a " + result.getClass().getName());
			else
				return (HGTopic)result;
		}	
		HGTopic t = (HGTopic)tm.createTopic();
		t.addSubjectLocator(l);
		return t;		
	}
	
	private Locator getHref1(Element el)
	{
		String id = el.getAttribute("xlink:href");
		if (id == null || id.length() == 0)
			throw new RuntimeException("Missing or empty (but expected!) href attribute in el " + el);
		if (id.charAt(0) == '#')
			id = iri + id;
		return U.ensureLocator(system.getGraph(), null, id);
	}
	
	private Set<Topic> getScope1(Element el, HGTopicMap tm)
	{
		Set<Topic> result = new HashSet<Topic>();
		NodeList kids = el.getChildNodes();
		for (int i = 0; i < kids.getLength(); i++)
		{
			Node n = kids.item(i);
			if (! (n instanceof Element))
				continue;
			else if (n.getNodeName().equals("resourceRef"))
			{
				result.add(getTopicByLocator1((Element)n, tm));
			}
			else if (n.getNodeName().equals("topicRef"))
			{
				result.add(getTopicByRef1((Element)n, tm));
			}
			else if (n.getNodeName().equals("subjectIndicatorRef"))				
			{
				result.add(getTopicByIndicator1((Element)n, tm));
			}		
		}
		return result;
	}
	
	public static void toElement1(HGTopic t, Element el, String iri)
	{
		// Types
		for (Topic type : t.getTypes())
		{
			Element inst = el.getOwnerDocument().createElement("instanceOf");
			Element ref = el.getOwnerDocument().createElement("topicRef");
			ref.setAttribute("xlink:href", getTopicId(type, iri));			
			inst.appendChild(ref);
			el.appendChild(inst);
		}
		
		// Identity
		String id = getTopicId(t, iri);
		if (id != null && id.length() > 0)
			el.setAttribute("id", id.substring(1));	
		Element sid = el.getOwnerDocument().createElement("subjectIdentity");
		for (Locator l : t.getSourceLocators())
		{
			if (id != null && id.length() > 0 && l.toExternalForm().endsWith(id))
				continue;
			Element e = el.getOwnerDocument().createElement("topicRef");			
			e.setAttribute("xlink:href", getLocalRef(iri, l.toExternalForm()));
			sid.appendChild(e);
			
		}		
		for (Locator l  : t.getSubjectLocators())
		{
			Element e = el.getOwnerDocument().createElement("resourceRef");
			e.setAttribute("xlink:href", getLocalRef(iri, l.toExternalForm()));
			sid.appendChild(e);
		}
		for (Locator l : t.getSubjectIdentifiers())
		{
			Element e = el.getOwnerDocument().createElement("subjectIndicatorRef");
			e.setAttribute("xlink:href", getLocalRef(iri, l.toExternalForm()));
			sid.appendChild(e);
			
		}		
		el.appendChild(sid);
		
		// Names
		for (HGTopicName n : t.getTopicNames())
			exportTopicName1(n, el, iri);
		
		// Occurrences
		for (HGOccurrence occ : t.getOccurrences())
			exportOccurrence1(occ, el, iri);
	}
	
	public static void exportTopicName1(TopicName n, Element parentEl, String iri)
	{
		Element ne = parentEl.getOwnerDocument().createElement("baseName");
		parentEl.appendChild(ne);
		scopeEntity1(ne, n.getScope(), iri);			
		Element nes = parentEl.getOwnerDocument().createElement("baseNameString");
		nes.appendChild(parentEl.getOwnerDocument().createTextNode(n.getValue()));
		ne.appendChild(nes);
		for (Object o : n.getVariants())
		{
			Variant v = (Variant)o;
			Element ve = parentEl.getOwnerDocument().createElement("variant");
			ne.appendChild(ve);
			Element pve = parentEl.getOwnerDocument().createElement("parameters");
			ve.appendChild(pve);				
			for (Object x : v.getScope())
			{
				Topic scope = (Topic)x;
				for (Object y : scope.getSourceLocators())
				{
					Locator l = (Locator)y;
					Element le = parentEl.getOwnerDocument().createElement("topicRef");
					le.setAttribute("xlink:href", getLocalRef(iri, l.toExternalForm()));
					pve.appendChild(le);
				}					
				for (Object y : scope.getSubjectIdentifiers())
				{
					Locator l = (Locator)y;
					Element le = parentEl.getOwnerDocument().createElement("subjectIndicatorRef");
					le.setAttribute("xlink:href", getLocalRef(iri, l.toExternalForm()));
					pve.appendChild(le);
				}					
			}
			Element vname = parentEl.getOwnerDocument().createElement("variantName");
			ve.appendChild(vname);
			if (v.getValue() != null && v.getValue().length() > 0)
			{
				Element x = parentEl.getOwnerDocument().createElement("resourceData");
				x.appendChild(parentEl.getOwnerDocument().createTextNode(v.getValue()));
				vname.appendChild(x);
			}
			else
			{
				Element x = parentEl.getOwnerDocument().createElement("resourceRef");
				x.setAttribute("xlink:href", getLocalRef(iri, v.getResource().toExternalForm()));
				vname.appendChild(x);
			}
		}		
	}
	
	public static void exportOccurrence1(Occurrence occ, Element parentEl, String iri)
	{
		Element oe = parentEl.getOwnerDocument().createElement("occurrence");
		parentEl.appendChild(oe);		
		if (occ.getType() != null)
		{
			Element inst = parentEl.getOwnerDocument().createElement("instanceOf");
			Element ref = parentEl.getOwnerDocument().createElement("topicRef");
			ref.setAttribute("xlink:href", getTopicId(occ.getType(), iri));
			inst.appendChild(ref);
			oe.appendChild(inst);
		}		
		scopeEntity1(oe, occ.getScope(), iri);
		if (occ.getValue() != null && occ.getValue().length() > 0)
		{
			Element x = parentEl.getOwnerDocument().createElement("resourceData");
			x.appendChild(parentEl.getOwnerDocument().createTextNode(occ.getValue()));
			oe.appendChild(x);
		}
		else if (occ.getResource() != null)
		{
			Element x = parentEl.getOwnerDocument().createElement("resourceRef");
			x.setAttribute("xlink:href", getLocalRef(iri, occ.getResource().toExternalForm()));
			oe.appendChild(x);
		}					
	}
	
	public static void scopeEntity1(Element entity, Set<HGTopic> scope, String iri)
	{
		if (scope.size() == 0)
			return;
		Element soe = entity.getOwnerDocument().createElement("scope");			
		for (HGTopic s : scope)
		{
			for (Locator l : s.getSourceLocators())
			{
				Element le = entity.getOwnerDocument().createElement("topicRef");
				le.setAttribute("xlink:href", getLocalRef(iri, l.toExternalForm()));
				soe.appendChild(le);
			}					
			for (Locator l : s.getSubjectIdentifiers())
			{
				Element le = entity.getOwnerDocument().createElement("subjectIndicatorRef");
				le.setAttribute("xlink:href", getLocalRef(iri, l.toExternalForm()));
				soe.appendChild(le);
			}					
		}		
		entity.appendChild(soe);
	}
	
	public static void toElement1(HGAssociation a, Element el, String iri)
	{
		if (a.getType() != null)
		{
			Element ref = el.getOwnerDocument().createElement("topicRef");
			el.appendChild(ref);
			ref.setAttribute("xlink:href", getTopicId(a.getType(), iri));			
		}
		scopeEntity1(el, a.getScope(), iri);
		for (HGAssociationRole r : a.getAssociationRoles())
		{
			Element re = el.getOwnerDocument().createElement("member");
			el.appendChild(re);
			if (r.getType() != null)
			{
				Element spec = el.getOwnerDocument().createElement("roleSpec");
				re.appendChild(spec);
				Element ref = el.getOwnerDocument().createElement("topicRef");
				spec.appendChild(ref);
				ref.setAttribute("xlink:href", getTopicId(r.getType(), iri));
			}
			Element pref = el.getOwnerDocument().createElement("topicRef");
			pref.setAttribute("xlink:href", getTopicId(r.getPlayer(), iri));
			re.appendChild(pref);
		}
	}
	
	public static String getTopicId(Topic t, String iri)
	{
		String href = null;
		for (Object x : t.getSourceLocators())
		{
			href = ((Locator)x).toExternalForm();			
			if (href.startsWith(iri + "#"))
				return href.substring(iri.length());
			else if (href.startsWith(iri + "%23"))
				return "#" + href.substring(iri.length() + 3);
		}		
		return URLDecoder.decode(href);
	}
	
	public static String getLocalRef(String iri, String href)
	{
		if (href.startsWith(iri + "#"))
			return href.substring(iri.length());
		else if (href.startsWith(iri + "%23"))
			return "#" + href.substring(iri.length() + 3);
		else
			return URLDecoder.decode(href);
	}
}