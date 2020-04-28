package com.mycompany.dataextractor;

import java.io.*;
import java.util.*;

import com.mycompany.model.SecurityBugData;
import org.supercsv.io.CsvMapReader;
import org.supercsv.io.CsvMapWriter;
import org.supercsv.io.ICsvMapReader;
import org.supercsv.io.ICsvMapWriter;
import org.supercsv.prefs.CsvPreference;

/**
 * To add security bugs found in FindBugs tool report to the csv(CK metrics) files of cloud project releases
 */
public class AddSecurityBugs {
    // Path of CK metrics CSV files
    private static final String CK_CSV_FILE = "C:/Cloud projects/Projects with CK and Security metrics calculated/CK metrics google cloud/";

    // Path of Bug reports CSV files
    private static final String BUG_REPORTS_DIRECTORY_PATH = "C:/Cloud projects/Projects with CK and Security metrics calculated/Security bugs report (google cloud java)/Reports(Security CSVs)/";

    // Name of project
    private static final String PROJECT_NAME = "google-cloud-java";

    public static void main(String[] args) throws IOException {
        List<SecurityBugData> sbd = new ArrayList<>();
        List<String> metricsFileNames = new ArrayList<String>();

        // Count number of bug report files
        int numOfReports = countBugReports(BUG_REPORTS_DIRECTORY_PATH);
        for (int i = 1; i <= numOfReports; i++) {
            try {
                sbd = readReports(sbd, i);
            } catch (Exception e) {
                continue;
            }
        }
        metricsFileNames = getMetricsFileNames(metricsFileNames);
        for (int k = 0; k < metricsFileNames.size(); k++) {
            addSecBugsToCSV(sbd, metricsFileNames.get(k).substring(0, metricsFileNames.get(k).lastIndexOf(".")));
        }

    }

    /**
     * Get all the CK metrics file names in given directory
     *
     * @param metricsFileNames
     * @return names of files found
     */
    private static List<String> getMetricsFileNames(List<String> metricsFileNames) {
        File folder = new File(CK_CSV_FILE);
        File[] listOfFiles = folder.listFiles();
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                metricsFileNames.add(listOfFiles[i].getName());
            }
        }
        return metricsFileNames;
    }

    /**
     * @param bugReportsDirectoryPath CSV bug reports
     * @return total number of bug report files
     */
    private static int countBugReports(String bugReportsDirectoryPath) {
        int numOfReports = new File(bugReportsDirectoryPath).listFiles().length;
        return numOfReports;
    }

    /**
     * Read bugs from Findbugs Report (CSV file) to get class name, bug name and bug frequency from files.
     */
    private static List<SecurityBugData> readReports(List<SecurityBugData> sbd, int file_num) {
        String bugName;
        // Path of bug report CSV file
        String filePath = BUG_REPORTS_DIRECTORY_PATH + PROJECT_NAME + "-" + file_num + ".csv";
        ICsvMapReader fileReader;
        try {
            // STANDARD_PREFERENCE takes comma as delimiter
            CsvPreference prefs = CsvPreference.STANDARD_PREFERENCE;
            fileReader = new CsvMapReader(new FileReader(filePath), prefs);

            // header used to read the original file
            final String[] readHeader = fileReader.getHeader(true);

            Map<String, String> row;
            String className;
            Boolean duplicate = false;

            // Read the file row wise
            while ((row = fileReader.read(readHeader)) != null) {
                SecurityBugData sbData = new SecurityBugData();

                // Get column containing Bug name
                bugName = row.get("ShortMessage");

                // Get class name containing bug
                className = row.get("Class/_classname");

                for (SecurityBugData sb : sbd) {
                    // If seurity bug list has corresponding entries (Bug and Class name) as in bug report, add 1 to frequency
                    if (bugName.equals(sb.getBugName()) && className.equals(sb.getClassName())) {
                        duplicate = true;
                        // Add Frequency of each bug found
                        sb.setFrequency(sb.getFrequency() + 1);
                    } else {
                        duplicate = false;
                        continue;
                    }
                }
                // If bug list has no entry for the bug found in bug report file.
                if (!duplicate) {
                    // Add bug name to list
                    sbData.setBugName(bugName);
                    // Add class/package name to bug
                    sbData.setClassName(className);
                    // Set bug frequency
                    sbData.setFrequency(1);
                }
//                }
                // Add current security bug into bug list
                if (!duplicate) {
                    sbd.add(sbData);
                }


            }

        } catch (FileNotFoundException fnf) {
            System.out.println("Bug report file not found");
            return sbd;
        } catch (IOException e) {
            e.printStackTrace();

        }
        return sbd;
    }

    private static void addSecBugsToCSV(List<SecurityBugData> sbd, String filename) throws IOException {
        ICsvMapReader mapReader = null;
        ICsvMapReader mapReader1 = null;
        ICsvMapWriter mapWriter = null;

        // Make new directory to save combined metrics files
        File directory = new File(CK_CSV_FILE.substring(0, CK_CSV_FILE.lastIndexOf("/") + 1) + "/CK-SEC/");
        if (!directory.exists()) {
            directory.mkdir();
        }
        // Path of new file with combined CK and Security metrics
        String ckSecMetricsFile = CK_CSV_FILE.substring(0, CK_CSV_FILE.lastIndexOf("/") + 1) + "CK-SEC/" + filename + "(CK-SEC).csv";
        // Path of CK metrics file
        String ckMetricsFile = CK_CSV_FILE.substring(0, CK_CSV_FILE.lastIndexOf("/") + 1) + filename + ".csv";
        try {

            // STANDARD_PREFERENCE takes comma as delimiter
            CsvPreference prefs = CsvPreference.STANDARD_PREFERENCE;
            mapReader = new CsvMapReader(new FileReader(ckMetricsFile), prefs);
            mapWriter = new CsvMapWriter(new FileWriter(ckSecMetricsFile), prefs);

            // header used to read the original file
            final String[] readHeader = mapReader.getHeader(true);

            // header used to write the new file
            // (same as 'readHeader', but with security bugs columns)
            final String[] writeHeader = new String[readHeader.length + sbd.size()];
            System.arraycopy(readHeader, 0, writeHeader, 0, readHeader.length);

            // Add new columns starting at current columns length
            int indexToWrite = readHeader.length;
            int requiredIndex = indexToWrite;

            Map<String, String> row;
            int counter = 0;
            int rowCount = 0;
            int bugsAdded = 0;
            Boolean colAdded = false;
            List<String> bugList = new ArrayList<>();
            // Read CK metrics file to match class name with the class names found in bug reports
            while ((row = mapReader.read(readHeader)) != null) {
                for (SecurityBugData s : sbd) {

                    // To compare class name in csv file with class name in bug report
                    if (row.get("name").equals(s.getClassName())) {
                        if (!bugList.contains(s.getBugName())) {
                            writeHeader[indexToWrite++] = s.getBugName();
                            bugList.add(s.getBugName());
                            colAdded = true;
                        }
                    }
//                    }
                }
            }
            // If CK metrics file contains buggy classes
            if (colAdded) {
                //Add column for weighted sum
                writeHeader[indexToWrite] = "Weighted Sum";
                mapWriter.writeHeader(writeHeader);

                // Add frequency against each bug added
                mapReader1 = new CsvMapReader(new FileReader(ckMetricsFile), prefs);
                final String[] readHeader1 = mapReader1.getHeader(true);
                while ((row = mapReader1.read(readHeader1)) != null) {
                    for (SecurityBugData s : sbd) {

                        // To compare class name in csv file with class name in bug report
                        if (row.get("name").equals(s.getClassName())) {

                            // add security bug frequency
                            row.put(s.getBugName(), String.valueOf(s.getFrequency()));

                            rowCount++;
                            // If there is a bug in current class/row/record in csv
                            bugsAdded += 1;


                        }
                    }
                    for (int i = requiredIndex; i < indexToWrite; i++) {
                        if (row.get(writeHeader[i]) == null) {
                            row.put(writeHeader[i], "0");
                        }
                    }
                    // Add weighted sum of security metrics
                    int weightedSum = 0;
                    for (int j = requiredIndex; j < indexToWrite; j++) {
                        // Get frequency of each bug column and add it to weighted sum
                        weightedSum += Integer.parseInt(row.get(writeHeader[j]));
                    }
                    row.put(writeHeader[indexToWrite], String.valueOf(weightedSum));
                    // Write data to new CSV file
                    mapWriter.write(row, writeHeader);
                    bugsAdded = 0;
                }
            }

        } finally {
            if (mapReader != null) {
                mapReader.close();
            }
            if (mapWriter != null) {
                mapWriter.close();
                File file = new File(ckSecMetricsFile);
                // If no security bug is added to new file(delete the empty file)
                if (file.exists() && file.length() == 0) {
                    System.out.println("No security metrics(file not created)" + file.getName());
                    file.delete();
                } else {
                    System.out.println("File created(with Security metrics) " + file.getName());
                }

            }
        }
    }

}
