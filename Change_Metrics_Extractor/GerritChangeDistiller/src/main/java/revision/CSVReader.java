package main.java.revision;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class CSVReader {
	
	public static List<GerritReview> readReviewsFromCSV(String fileName) {
		List<GerritReview> reviews  = new ArrayList<>();
		Path pathToFile = Paths.get(fileName);
		 
		// create an instance of BufferedReader
        try (BufferedReader br = Files.newBufferedReader(pathToFile,
                StandardCharsets.US_ASCII)) {

            // read header of text file
            String header = br.readLine();
            // read the first line from the text file
            String line = br.readLine();
            // loop until all lines are read
            while (line != null) {

                // use string.split to load a string array with the values from
                // each line of the file, using a comma as the delimiter
                String[] attributes = line.split(",");

                GerritReview review = createReview(attributes);

                reviews.add(review);

                line = br.readLine();
            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return reviews;
	}
	
	private static GerritReview createReview(String[] metadata) {
        String changeId = metadata[0];
        int patchSetNr = Integer.parseInt(metadata[1]);
        String patchRevisionNr = metadata[2];
        String refPatchSet = metadata[3];

        return new GerritReview(changeId, patchSetNr, patchRevisionNr, refPatchSet);
    }

}
