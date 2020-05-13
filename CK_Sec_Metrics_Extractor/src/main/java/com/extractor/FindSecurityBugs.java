package com.extractor;

import com.model.MatrixData;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.ObjectStream;
import org.eclipse.jgit.lib.Repository;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FindSecurityBugs {
    private static Pattern randomBug = Pattern.compile("new Random()");
    private static Pattern unencryptedSocketBug = Pattern.compile("new Socket\\(");
    private static Pattern unencryptedServerSocketBug = Pattern.compile("new ServerSocket\\(");
    private static Pattern unsafeHashEqualsBug = Pattern.compile("\\.equals\\(");
    private static Pattern externalFileDirBug = Pattern.compile("\\.getExternalFilesDir\\(");
    private static Pattern userInputUrlBug = Pattern.compile("new URL\\(String url\\)\\.openConnection()");
    private static Pattern stackTraceBug = Pattern.compile("\\.printStackTrace\\(");
    private static Pattern weakHashFunctionsBug = Pattern.compile("MessageDigest\\.getInstance\\(");
    //    //    Bug Pattern: PATH_TRAVERSAL_IN
    private static Pattern pathTraversalInBug1 = Pattern.compile("new File\\(");
    private static Pattern pathTraversalInBug2 = Pattern.compile("FilenameUtils\\.|FileUtils\\.");   // A Bug, if it is not used

    //    //    Bug Pattern: COMMAND_INJECTION
    private static Pattern commandInjectionBug1 = Pattern.compile("Runtime\\.getRuntime()");
    private static Pattern commandInjectionBug2 = Pattern.compile("\\.exec\\(");

    //    //  Bug Pattern: XXE_XMLSTREAMREADER
    private static Pattern xxe_xmlStreamReaderBug1 = Pattern.compile("XMLInputFactory\\.newFactory()");
    private static Pattern xxe_xmlStreamReaderBug2 = Pattern.compile("\\.setProperty\\(XMLInputFactory\\.IS_SUPPORTING_EXTERNAL_ENTITIES, false\\)"); // A Bug, if it is not used

    //    // Bug Pattern: SQL_INJECTION
    private static Pattern sqlInjectionBug1 = Pattern.compile("createQuery\\(");
    private static Pattern sqlInjectionBug2 = Pattern.compile("Encoder\\.encodeForSQL");    // A Bug, if it is not used

    //    //    Bug Pattern: SQL_INJECTION_JDBC
    private static Pattern sqlInjectionJDBCBug = Pattern.compile("\\.createStatement()");

    //    //  Bug Pattern: CRLF_INJECTION_LOGS
    private static Pattern crlfInjectionLogsBug = Pattern.compile("^.*\\.info\\(((?!.replaceAll).)*$");

    public static void main(String[] args) throws IOException {

    }

    public static void checkSecurityVulnerabilities(MatrixData matrixData, ObjectId objectId, Repository repo) throws IOException {

        // Set all Security vulnerabilities for current file
        matrixData.setRandomBug(checkvulnerability(objectId, randomBug, repo));
        matrixData.setUnencryptedSocketBug(checkvulnerability(objectId, unencryptedSocketBug, repo));
        matrixData.setUnencryptedServerSocketBug(checkvulnerability(objectId, unencryptedServerSocketBug, repo));
        matrixData.setUnsafeHashEqualsBug(checkvulnerability(objectId, unsafeHashEqualsBug, repo));
        matrixData.setExternalFileDirBug(checkvulnerability(objectId, externalFileDirBug, repo));
        matrixData.setUserInputUrlBug(checkvulnerability(objectId, userInputUrlBug, repo));
        matrixData.setStackTraceBug(checkvulnerability(objectId, stackTraceBug, repo));
        matrixData.setWeakHashFunctionsBug(checkvulnerability(objectId, weakHashFunctionsBug, repo));
        matrixData.setSqlInjectionJDBCBug(checkvulnerability(objectId, sqlInjectionJDBCBug, repo));
        matrixData.setPathTraversalInBug(SecurityVulnerabilities(objectId, pathTraversalInBug1, pathTraversalInBug2, repo));
        matrixData.setCommandInjectionBug(SecurityVulnerabilities(objectId, commandInjectionBug1, commandInjectionBug2, repo));
        matrixData.setXxe_xmlStreamReaderBug(SecurityVulnerabilities(objectId, xxe_xmlStreamReaderBug1, xxe_xmlStreamReaderBug2, repo));
        matrixData.setSqlInjectionBug(SecurityVulnerabilities(objectId, sqlInjectionBug1, sqlInjectionBug2, repo));
        matrixData.setCrlfInjectionBug(checkvulnerability(objectId, crlfInjectionLogsBug, repo));
    }

    public static int checkvulnerability(ObjectId objectId, Pattern bugPattern, Repository repo) throws IOException {

        // loader object will open the file with given ID(objectId)
        ObjectLoader loader = repo.open(objectId);
        // Open stream for the file to read its contents
        ObjectStream loaderstream = loader.openStream();
//        loader.copyTo(System.out);
        // Create a reader for file
        BufferedReader reader = new BufferedReader(new InputStreamReader(loaderstream));

        String line;
        int bugOccurrences = 0;

        // Bug pattern matchers for 12 type of bugs
        while ((line = reader.readLine()) != null) {
            if (line.trim().startsWith("/*") || line.trim().startsWith("*") || line.trim().startsWith("//") ||
                    line.trim().isEmpty() || line.trim().startsWith("import") || line.trim().startsWith("package")) {
                continue;
            }
            boolean patternDetected = false;
            Matcher matcher = bugPattern.matcher(line);

            while (matcher.find()) {
                patternDetected = true;
            }
            if (patternDetected) {
                bugOccurrences++;
            }
        }
        reader.close();
        return bugOccurrences;
    }

    public static int SecurityVulnerabilities(ObjectId objectId, Pattern bugPattern1, Pattern bugPattern2, Repository repo) throws IOException {

        // loader object will open the file with given ID(objectId)
        ObjectLoader loader = repo.open(objectId);
        // Open stream for the file to read its contents
        ObjectStream loaderstream = loader.openStream();
        // Create a reader for file
        BufferedReader reader = new BufferedReader(new InputStreamReader(loaderstream));

        String line;
        int bugOccurrences = 0;
        int pattern1 = 0;
        while ((line = reader.readLine()) != null) {
            if (line.trim().startsWith("/*") || line.trim().startsWith("*") || line.trim().startsWith("//") ||
                    line.trim().isEmpty() || line.trim().startsWith("import") || line.trim().startsWith("package")) {
                continue;
            }
            Matcher matcher1 = bugPattern1.matcher(line);
            Matcher matcher2 = bugPattern2.matcher(line);

            boolean patternDetected = false;
            if (pattern1 > 0) {
                while (matcher2.find()) {
                    patternDetected = true;
                }
                if (!patternDetected) {
                    bugOccurrences++;
                }
                pattern1 = 0;
                while (matcher1.find()) {
                    pattern1++;
                }
            } else {

                while (matcher1.find()) {
                    patternDetected = true;
                }
                if (patternDetected) {
                    pattern1++;
                }
            }
        }
        reader.close();
        return bugOccurrences;

    }

}
