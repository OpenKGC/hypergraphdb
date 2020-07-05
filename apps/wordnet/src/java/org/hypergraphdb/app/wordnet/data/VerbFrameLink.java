package org.hypergraphdb.app.wordnet.data;

import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HGPlainLink;

/**
 * <p>
 * Associates a verb sense to a {@VerbFrame} document proper usage in that sense.
 *  </p>
 */
public final class VerbFrameLink extends HGPlainLink
{

    // private VerbFrame frame;

    public VerbFrameLink()
    {
        super();
    }

    public VerbFrameLink(HGHandle[] outgoingSet)
    {
        super(outgoingSet);
    }

    /*
     * public VerbFrameLink(VerbFrame _frame, HGHandle[] outgoingSet) {
     * super(outgoingSet); frame = _frame; }
     * 
     * public void setVerbFrame(VerbFrame value) { this.frame = value; }
     * 
     * public VerbFrame getVerbFrame() { return frame; }
     * 
     * public void setTargets(HGHandle [] targets) { outgoingSet = targets; }
     * public boolean equals(Object object) { if (object == null || !(object
     * instanceof VerbFrameLink)) return false; VerbFrameLink link =
     * (VerbFrameLink) object; if
     * (!link.getVerbFrame().equals(this.getVerbFrame())) return false; if
     * (link.getArity() != this.getArity()) return false; for (int i = 0; i <
     * this.getArity(); i++) if
     * (!link.getTargetAt(i).equals(this.getTargetAt(i))) return false; return
     * true; }
     */

}