package main.java.revision;

import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;

public interface ChangeCallbackIF {
	public void handleChange(SourceCodeChange change, String changeId, String file, boolean methodInMultipleClasses, String editList);
}
