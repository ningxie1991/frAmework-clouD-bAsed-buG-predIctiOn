package main.java.revision;

public class GerritReview {
	private String changeId;
	private int patchSetNr;
	private String patchRevisionNr;
	private String refPatchSet;
	
	public GerritReview(String changeId, int patchSetNr, String patchRevisionNr, String refPatchSet) {
		this.changeId = changeId;
		this.patchSetNr = patchSetNr;
		this.patchRevisionNr = patchRevisionNr;
		this.refPatchSet = refPatchSet;
	}
	
	public String getRefPatchSet() {
		return refPatchSet;
	}
	
	public String getPatchRevisionNr() {
		return patchRevisionNr;
	}
	
	public int getPatchSetNr() {
		return patchSetNr;
	}
	
	public String getChangeId() {
		return changeId;
	}
	
	@Override
    public String toString() {
        return "Review [changeId=" + this.changeId + ", patchSetNr=" + this.patchSetNr + ", patchRevisionNr=" + this.patchRevisionNr + ", refPatchSet=" + this.refPatchSet + "]";
    }
}
