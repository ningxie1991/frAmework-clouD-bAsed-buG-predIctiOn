package revision;

import java.io.File;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import ch.uzh.ifi.seal.changedistiller.model.classifiers.ChangeType;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;
import ch.uzh.ifi.seal.changedistiller.model.entities.StructureEntityVersion;

public class Starter {

	public static void main(String args[]) {

		String projectName = "acceleo";
//		String projectFetchUrl = "https://git.eclipse.org/r/cdt/org.eclipse.cdt";
		String projectFetchUrl = "https://git.eclipse.org/r/acceleo/org.eclipse.acceleo";
//		String projectFetchUrl = "https://github.com/eclipse/acceleo.git";
//		String projectFetchUrl = "3df59e8b8ebb8bf299eb561786e3a29ea59eab231551435989";
		// https://git.eclipse.org/r/m2e/m2e-core
		// https://git.eclipse.org/r/bpel/org.eclipse.bpel
		// https://git.eclipse.org/r/acceleo/org.eclipse.acceleo
		// https://git.eclipse.org/r/egit/egit-pde
		// https://git.eclipse.org/r/cdt/org.eclipse.cdt
		String dirInput = "D:/GitHub/BugPrediction/GerritDataAnalyzer/dataInputForDistiller/";
		String dirOutput = "D:/GitHub/BugPrediction/GerritDataAnalyzer/dataOutputFromDistiller/";
		String csvFile = dirInput + projectName + "/" + projectName + "_first_last_patches_for_distilling.csv";
		File outputFile = new File(dirOutput + projectName+ "/" + projectName + "_distiller_output.csv");

		try {
			new Starter().run(projectName, projectFetchUrl, csvFile, outputFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run(String projectName, String projectFetchUrl, String csvFile, File outputFile) throws Exception {
		GerritRevisionChangeDistiller gerritDistiller = new GerritRevisionChangeDistiller(projectName, projectFetchUrl,
				csvFile);
		final PrintWriter pw = new PrintWriter(outputFile);
		pw.write(
				"ChangeId, ChangedFile, ChangeType, ChangeTypeDetail, ChangeSeverity, ChangeEdits, ChangeClassified, ChangeEffectedEntity \n");
		gerritDistiller.run(new ChangeCallbackIF() {
			@Override
			public void handleChange(SourceCodeChange change, String changeId, String file,
					boolean methodInMultipleClasses, String editList) {

				StructureEntityVersion root = change.getRootEntity();
				String csvChangeId = changeId;
				String csvChangedFileName = file;
				String csvChangeTypeName = root.getType().name() + "_" + change.getChangeType().name();
				String csvSeverity = change.getSignificanceLevel().name();
				String csvClassifiedType = getClassifiedType(change.getChangeType(), methodInMultipleClasses);
				String csvEffectedEntity = root.getUniqueName();
				String csvEditList = editList.replace(",", " ");
				String csvChangeTypeDetailName = change.getChangedEntity().getType().toString();

				String line = csvChangeId + "," + csvChangedFileName + "," + csvChangeTypeName + ","
						+ csvChangeTypeDetailName + "," + csvSeverity + "," + csvEditList + "," + csvClassifiedType
						+ "," + "\"" + csvEffectedEntity + "\"";

				pw.write(line + "\n");
			}

		});

		pw.flush();
		pw.close();

		System.out.println("Finished analysis");
	}

	private String getClassifiedType(ChangeType ct, boolean methodInMultipleClasses) {

		List<ChangeType> methodSignature = Arrays.asList(ChangeType.INCREASING_ACCESSIBILITY_CHANGE,
				ChangeType.DECREASING_ACCESSIBILITY_CHANGE, ChangeType.PARAMETER_DELETE, ChangeType.PARAMETER_INSERT,
				ChangeType.PARAMETER_ORDERING_CHANGE, ChangeType.PARAMETER_RENAMING, ChangeType.PARAMETER_TYPE_CHANGE,
				ChangeType.RETURN_TYPE_CHANGE, ChangeType.RETURN_TYPE_DELETE, ChangeType.RETURN_TYPE_INSERT);

		List<ChangeType> inheritanceChange = Arrays.asList(ChangeType.PARENT_CLASS_CHANGE,
				ChangeType.PARENT_CLASS_DELETE, ChangeType.PARENT_CLASS_INSERT, ChangeType.PARENT_INTERFACE_CHANGE,
				ChangeType.PARENT_INTERFACE_DELETE, ChangeType.PARENT_INTERFACE_INSERT);

		if (methodSignature.contains(ct)) {
			return "METHOD_SIGNATURE_CHANGE";
		} else if (inheritanceChange.contains(ct)) {
			return "INHERITANCE_CHANGE";
		} else if (ChangeType.CLASS_RENAMING.equals(ct)) {
			return "CLASS_RENAMED";
		} else if (ChangeType.METHOD_RENAMING.equals(ct)) {
			return "METHOD_RENAME";
		} else if (methodInMultipleClasses) {
			return "METHOD_MOVE";
		}

		return "OTHER";
	}
}
