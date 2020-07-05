package org.hypergraphdb.app.wordnet.data;

/**
 * 
 * <p>
 * Enumerates the possible parts of speech in WordNet: noun, verb, adverb and
 * adjective.
 * </p>
 *
 * @author Borislav Iordanov
 *
 */
public enum Pos
{
	noun("noun"),
	verb("verb"),
	adverb("adverb"),
	adjective("adjective");
	
	private String name;
	
	private Pos(String name) { this.name = name; }
	
	public String toString() { return name; }
	
	public static Pos fromName(String name)
	{
		if (name.charAt(0) == 'n')
			return noun;
		else if (name.charAt(0) == 'v')
			return verb;
		else if (name.startsWith("adv"))
			return adverb;
		else if (name.startsWith("adj"))
			return adjective;
		else
			throw new IllegalArgumentException("Unrecognized part of speech name " + name);
	}
}