package com.mycompany.dataextractor;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import com.opencsv.CSVReader;
import org.supercsv.io.CsvMapReader;
import org.supercsv.io.CsvMapWriter;
import org.supercsv.io.ICsvMapReader;
import org.supercsv.io.ICsvMapWriter;
import org.supercsv.prefs.CsvPreference;

/**)
 *  To add security bugs found in FindBugs tool report to the csv(CK metrics) files of cloud project releases
 */
public class AddSecurityBugs {
    // Path of bug report csv file
    private static final String SEC_BUG_CSV_REPORT = "C:/Users/Urooj Isar/Documents/panasol/jhipster_registry.csv";
    // Path of CK metrics csv file
    private static final String CK_CSV_FILE = "C:/Users/Urooj Isar/Documents/panasol/v4.0.1.csv";
    // Path of new csv file with security metrics added to CK metrics
    private static final String SEC_CSV_FILE = "C:/Users/Urooj Isar/Documents/panasol/secbugs.csv";
    public static void main(String[] args) throws IOException {
        HashMap<String, Integer> secBugsWithFrequency = new HashMap<>();
        secBugsWithFrequency = readBugReports(secBugsWithFrequency);
        addSecBugsToCSV(secBugsWithFrequency);


    }
    /**
     * Read bugs from Findbugs Report (CSV file)
     */
    private static HashMap<String, Integer> readBugReports(HashMap<String, Integer> secBugsWithFrequency) {
        int bugfrequency;
        String bugName;
        try (
                Reader reader = new FileReader(SEC_BUG_CSV_REPORT);
                CSVReader csvReader = new CSVReader(reader);

        ) {
            String[] nextRecord;
            int counter = 0;
            int wightedSum = 0;
            while ((nextRecord = csvReader.readNext()) != null) {
                // Skip the header/ Column names in the file
                if(counter==0){
                    counter++;
                    continue;
                }
                else{
                    // First column of file (regarding Security bug names)
                    bugName = nextRecord[0];
                    // Add Frequency of each bug found
                    if(secBugsWithFrequency.containsKey(bugName)){
                        bugfrequency = secBugsWithFrequency.get(bugName);
                        secBugsWithFrequency.replace(bugName,bugfrequency+1);
                    }
                    else{
                        secBugsWithFrequency.put(bugName,1);
                    }
                }

            }
            // Add weighted sum of detected security metrics
            for(String key:secBugsWithFrequency.keySet()){
                wightedSum += secBugsWithFrequency.get(key);
            }
            secBugsWithFrequency.put("Weighted Sum", wightedSum);

//            for(String key:secBugsWithFrequency.keySet()){
//                System.out.println("Bug name: "+key);
//                System.out.println("Bug frequency: "+secBugsWithFrequency.get(key));
//            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return secBugsWithFrequency;
    }

    private static void addSecBugsToCSV(HashMap<String, Integer> secBugsWithFrequency) throws IOException {
        ICsvMapReader mapReader = null;
        ICsvMapWriter mapWriter = null;
        try {
            // STANDARD_PREFERENCE takes comma as delimiter
            CsvPreference prefs = CsvPreference.STANDARD_PREFERENCE;
            mapReader = new CsvMapReader(new FileReader(CK_CSV_FILE), prefs);
            mapWriter = new CsvMapWriter(new FileWriter(SEC_CSV_FILE), prefs);

            // header used to read the original file
            final String[] readHeader = mapReader.getHeader(true);

            // header used to write the new file
            // (same as 'readHeader', but with security bugs columns)
            final String[] writeHeader = new String[readHeader.length + secBugsWithFrequency.size()];
            System.arraycopy(readHeader, 0, writeHeader, 0, readHeader.length);

            // Add new columns starting at columns length
            int indexToWrite = readHeader.length;
            // Column headings
            for(Map.Entry<String, Integer> entry : secBugsWithFrequency.entrySet()){
                writeHeader[indexToWrite++] = entry.getKey();
            }
            mapWriter.writeHeader(writeHeader);

            Map<String, String> row;
            while( (row = mapReader.read(readHeader)) != null ) {
                for(Map.Entry<String, Integer> entry : secBugsWithFrequency.entrySet()){
                    // add security bug frequency
                    row.put(entry.getKey(), String.valueOf(entry.getValue()));
                }
                indexToWrite = readHeader.length;
                // Write data to new CSV file
                mapWriter.write(row, writeHeader);
            }

        }
        finally {
            if( mapReader != null ) {
                mapReader.close();
            }
            if( mapWriter != null ) {
                mapWriter.close();
            }
        }
    }

}
