package com.mycompany.dataextractor;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SecurityMetrics {
    private static final String SEC_BUG_CSV_REPORT = "C:/Users/Urooj Isar/Documents/panasol/jhipster_registry.csv";
    private static final String CK_CSV_FILE = "C:/Users/Urooj Isar/Documents/panasol/v4.0.1.csv";
    private static final String SEC_CSV_FILE = "C:/Users/Urooj Isar/Documents/panasol/secbugs.csv";
    public static void main(String[] args) throws IOException {
        HashMap<String, Integer> secBugsWithFrequency = new HashMap<>();
        secBugsWithFrequency = ReadBugReports(secBugsWithFrequency);
        addSecBugs(secBugsWithFrequency);

    }

    /**
     * Add security bugs to CSV files
     */
    private static void addSecBugs(HashMap<String, Integer> secBugsWithFrequency) throws IOException {
        List<String[]> list = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(CK_CSV_FILE))) {
            String[] entries ;
            List<String> columnsdata = new ArrayList<>();
            String[] dataToAppend = new String[50];
//            int column = 0;
            int headerRow = 0;
            int counter = 0;
            while ((entries = reader.readNext()) != null) {
                columnsdata = new ArrayList<>(Arrays.asList(entries));
//                column = entries.length;

                for(String key: secBugsWithFrequency.keySet()){
                    if (headerRow<secBugsWithFrequency.size()){
                        columnsdata.add(key);
                        headerRow++;
                    }
                    else{
                        columnsdata.add(String.valueOf(secBugsWithFrequency.get(key)));
                    }
                }
                for(int i=0;i<columnsdata.size();i++){
                    System.out.println("columnsdata = " + columnsdata.get(i));
                   dataToAppend[i] = columnsdata.get(i);
                }
                list.add(dataToAppend);
                System.out.println("List appended!");
            }
            try(CSVWriter writer = new CSVWriter(new FileWriter(SEC_CSV_FILE))){
                System.out.println("writing data");
                writer.writeAll(list);
            }

            catch (IOException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Read bugs from Findbugs Report (CSV file)
     */
    private static HashMap<String, Integer> ReadBugReports(HashMap<String, Integer> secBugsWithFrequency) {
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
                if(counter==0){
                    counter++;
                    continue;
                }
                else{
                    bugName = nextRecord[0];
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
}
