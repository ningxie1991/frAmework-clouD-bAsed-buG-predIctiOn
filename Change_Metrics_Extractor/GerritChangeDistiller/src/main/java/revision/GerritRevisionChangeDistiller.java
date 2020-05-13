package main.java.revision;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.api.FetchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.patch.FileHeader;
import org.eclipse.jgit.patch.HunkHeader;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

import ch.uzh.ifi.seal.changedistiller.ChangeDistiller;
import ch.uzh.ifi.seal.changedistiller.ChangeDistiller.Language;
import ch.uzh.ifi.seal.changedistiller.distilling.FileDistiller;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;

public class GerritRevisionChangeDistiller {

	private String fetchUrl;
	private String csvFile;
	final private String workDir = "/temp";
	final private File project1;
	final private File project2;
	private List<GerritReview> reviews;

	public GerritRevisionChangeDistiller(String projectName, String projectFetchUrl, String csvFile) {

		this.fetchUrl = projectFetchUrl;
		this.csvFile = csvFile;
		this.project1 = new File(workDir + "/firstPatch-" + projectName);
		this.project2 = new File(workDir + "/lastPatch-" + projectName);
	}

	public void run(ChangeCallbackIF callback) throws Exception {
		this.reviews = CSVReader.readReviewsFromCSV(this.csvFile);
		List<String[]> changedJavaFiles;
		cloneRepos();
		for (int i = 1; i < this.reviews.size(); i++) {
			System.out.println(i + " - " + (i + 1));
			GerritReview review1 = this.reviews.get(i);
			GerritReview review2 = this.reviews.get(i + 1);
			if (review1.getPatchSetNr() > review2.getPatchSetNr()) {
				GerritReview tmp = review2;
				review2 = review1;
				review1 = tmp;
			}
			System.out.println(review1.toString());
			System.out.println(review2.toString());

			checkoutRefs(review1.getRefPatchSet(), review2.getRefPatchSet());
			changedJavaFiles = getChangedJavaFiles(review1.getPatchRevisionNr(), review2.getPatchRevisionNr());

			distillChanges(changedJavaFiles, callback, review1.getChangeId());
			i++;
		}
	}

	private void cloneRepos() throws Exception {
		if (!this.project1.exists()) {
			Git.cloneRepository().setURI(this.fetchUrl).setDirectory(this.project1).call();
		}

		if (!this.project2.exists()) {
			Git.cloneRepository().setURI(this.fetchUrl).setDirectory(this.project2).call();
		}
	}

	private void checkoutRefs(String ref1, String ref2) throws IOException {
		Git git1 = new Git(Git.open(this.project1).getRepository());
		FetchCommand fetchCommand1 = git1.fetch();
		try {
			fetchCommand1.setRefSpecs(new RefSpec(ref1)).call();
			git1.checkout().setName("FETCH_HEAD").call();
		} catch (GitAPIException e1) {
			e1.printStackTrace();
		}

		Git git2 = new Git(Git.open(this.project2).getRepository());
		FetchCommand fetchCommand2 = git2.fetch();
		try {
			fetchCommand2.setRefSpecs(new RefSpec(ref2)).call();
			git2.checkout().setName("FETCH_HEAD").call();
		} catch (GitAPIException e1) {
			e1.printStackTrace();
		}
	}

	private List<String[]> getChangedJavaFiles(String firstPatchSetId, String lastPatchSetId) throws IOException {
		List<String[]> changedJavaFiles = new ArrayList<String[]>();
		Git repo1 = new Git(Git.open(this.project1).getRepository());
		Git repo2 = new Git(Git.open(this.project2).getRepository());
		try {
			AbstractTreeIterator repo1TreeParser = prepareTreeParser(repo1.getRepository(), firstPatchSetId);
			AbstractTreeIterator repo2TreeParser = prepareTreeParser(repo2.getRepository(), lastPatchSetId);
			List<DiffEntry> diff = repo1.diff().setOldTree(repo1TreeParser).setNewTree(repo2TreeParser).call();

			FileOutputStream stdout = new FileOutputStream(FileDescriptor.out);
			DiffFormatter diffFormatter = new DiffFormatter(stdout);
			diffFormatter.setRepository(repo1.getRepository());
			for (DiffEntry entry : diff) {
				if (entry.getOldPath().contains("/src/") && entry.getNewPath().contains("/src/")
						&& entry.getOldPath().endsWith(".java") && entry.getNewPath().endsWith(".java")) {

					System.out.println(entry);
					FileHeader fileHeader = diffFormatter.toFileHeader(entry);
					List<? extends HunkHeader> hunks = fileHeader.getHunks();
					for (HunkHeader hunk : hunks) {
						System.out.println(hunk);
					}
					EditList editList = fileHeader.toEditList();
					changedJavaFiles.add(new String[] { entry.getOldPath(), entry.getNewPath(), editList.toString() });
				}
			}
			for (String file[] : changedJavaFiles) {
				System.out.println(file[0] + " -> " + file[1]);
			}

		} catch (IOException | GitAPIException e1) {
			e1.printStackTrace();
		}
		return changedJavaFiles;
	}

	public void changeDistiller(){
		FileDistiller distiller = ChangeDistiller.createFileDistiller(Language.JAVA);
//		for (String file[] : changedJavaFiles) {
//			File left = new File(project1.getAbsolutePath() + "/" + file[0]);
//			File right = new File(project2.getAbsolutePath() + "/" + file[1]);
//		File left = new File("D:/tmp/firstPatch-acceleo/plugins/org.eclipse.acceleo.common.ide/src/org/eclipse/acceleo/common/ide/authoring/AcceleoModelManager.java");
//		File right = new File("D:/tmp/lastPatch-acceleo/plugins/org.eclipse.acceleo.common.ide/src/org/eclipse/acceleo/common/ide/authoring/AcceleoModelManager.java");


//		File left = new File(project1.getAbsolutePath()+"/"+"plugins/org.eclipse.acceleo.common.ide/src/org/eclipse/acceleo/common/ide/authoring/AcceleoModelManager.java");
//		File right = new File(project2.getAbsolutePath()+"/"+"plugins/org.eclipse.acceleo.common.ide/src/org/eclipse/acceleo/common/ide/authoring/AcceleoModelManager.java");

		File left = new File("D:/temp/firstPatch-acceleo/query/plugins/org.eclipse.acceleo.query/src/org/eclipse/acceleo/query/runtime/impl/AbstractService.java");
        File right = new File("D:/temp/lastPatch-acceleo/query/plugins/org.eclipse.acceleo.query/src/org/eclipse/acceleo/query/runtime/impl/AbstractService.java");

		try {
				distiller.extractClassifiedSourceCodeChanges(left, right);
				List<SourceCodeChange> changes = distiller.getSourceCodeChanges();
				System.out.println("testing");
			} catch (Exception e) {
				/*
				 * An exception most likely indicates a bug in ChangeDistiller. Please file a
				 * bug report at https://bitbucket.org/sealuzh/tools-changedistiller/issues and
				 * attach the full stack trace along with the two files that you tried to
				 * distill.
				 */
				System.err.println("Warning: error while change distilling. " + e.getMessage());
			}
//		}
	}

	private void distillChanges(List<String[]> changedJavaFiles, ChangeCallbackIF callback, String changeId) {
		FileDistiller distiller = ChangeDistiller.createFileDistiller(Language.JAVA);
		for (String file[] : changedJavaFiles) {
			File left = new File(project1.getAbsolutePath() + "/" + file[0]);
			File right = new File(project2.getAbsolutePath() + "/" + file[1]);

//		File left = new File("C:/Users/Urooj/Desktop/AcceleoModelManager.java");
//        File right = new File("C:/Users/Urooj/Documents/AcceleoModelManager.java");
			try {
				distiller.extractClassifiedSourceCodeChanges(left, right);
				List<SourceCodeChange> changes = distiller.getSourceCodeChanges();
				for (SourceCodeChange change : changes) {
					boolean methodInMultipleClasses = false;
					for (SourceCodeChange change2 : changes) {
						if (!change.getRootEntity().getUniqueName().equals(change2.getRootEntity().getUniqueName())
								&& !change.getChangeType().equals(change2.getChangeType())
								&& change.getRootEntity().getUniqueName().indexOf(".") > -1
								&& change2.getRootEntity().getUniqueName().indexOf(".") > -1
								&& change.getRootEntity().getUniqueName()
										.substring(change.getRootEntity().getUniqueName().lastIndexOf("."),
												change.getRootEntity().getUniqueName().length())
										.equals(change2.getRootEntity().getUniqueName().substring(
												change2.getRootEntity().getUniqueName().lastIndexOf("."),
												change2.getRootEntity().getUniqueName().length()))) {
							methodInMultipleClasses = true;
						}
					}
					callback.handleChange(change, changeId, file[0], methodInMultipleClasses, file[2]);
				}

			} catch (Exception e) {
				/*
				 * An exception most likely indicates a bug in ChangeDistiller. Please file a
				 * bug report at https://bitbucket.org/sealuzh/tools-changedistiller/issues and
				 * attach the full stack trace along with the two files that you tried to
				 * distill.
				 */
				System.err.println("Warning: error while change distilling. " + e.getMessage());
			}
		}
	}

	public List<SourceCodeChange> distilledChanges(String[] changedJavaFiles) {
		FileDistiller distiller = ChangeDistiller.createFileDistiller(Language.JAVA);
		List<SourceCodeChange> changes = new ArrayList<>();
//
		File left = new File(project1.getAbsolutePath() + "/" + changedJavaFiles[0]);
		File right = new File(project2.getAbsolutePath() + "/" + changedJavaFiles[1]);


//		File left = new File("C:/AbstractService1.java");
//		File right = new File("C:/AbstractService2.java");
		try {
			distiller.extractClassifiedSourceCodeChanges(left, right);
			changes = distiller.getSourceCodeChanges();
		} catch (Exception e) {
			/*
			 * An exception most likely indicates a bug in ChangeDistiller. Please file a
			 * bug report at https://bitbucket.org/sealuzh/tools-changedistiller/issues and
			 * attach the full stack trace along with the two files that you tried to
			 * distill.
			 */
			System.err.println("Warning: error while change distilling. " + e.getMessage());
			changes.add(null);
		}
		return changes;
//		}
	}

	private static AbstractTreeIterator prepareTreeParser(Repository repository, String objectId)
			throws IOException, MissingObjectException, IncorrectObjectTypeException {
		// from the commit we can build the tree which allows us to construct
		// the TreeParser
		// source:
		// https://github.com/centic9/jgit-cookbook/blob/master/src/main/java/org/dstadler/jgit/porcelain/ShowFileDiff.java
		RevWalk walk = new RevWalk(repository);
		RevCommit commit = walk.parseCommit(ObjectId.fromString(objectId));
		RevTree tree = walk.parseTree(commit.getTree().getId());

		CanonicalTreeParser oldTreeParser = new CanonicalTreeParser();
		ObjectReader oldReader = repository.newObjectReader();
		try {
			oldTreeParser.reset(oldReader, tree.getId());
		} finally {
			oldReader.release();
		}

		walk.dispose();

		return oldTreeParser;
	}
}
