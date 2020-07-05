package org.hypergraphdb.app.wordnet.data;

/**
 * 
 * <p>
 * Represents a lexical unit, a token whose string value is stored in the <code>lemma</code>
 * property.
 * </p>
 *
 * @author Borislav Iordanov
 *
 */
public class Word
{
    // static final long serialVersionUID = 8591237840924027785L;
    private String _lemma;

    public Word()
    {
    }

    public Word(String lemma)
    {
        _lemma = lemma;
    }

    public boolean equals(Object object)
    {
        return (object instanceof Word)
               && ((Word) object).getLemma().equals(getLemma());
    }

    public int hashCode()
    {
        return getLemma().hashCode();
    }

    public String toString()
    {
        return getLemma();
    }

    public String getLemma()
    {
        return _lemma;
    }

    public void setLemma(String lemma)
    {
        _lemma = lemma;
    }
}