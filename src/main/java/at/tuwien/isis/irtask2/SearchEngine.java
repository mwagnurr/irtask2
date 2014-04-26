package at.tuwien.isis.irtask2;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class SearchEngine {

	public SearchEngine() {

	}

	public void searchSimilarDocuments(String inputFilePath, String indexFilePath) {

		Path path = Paths.get(inputFilePath);
		try {
			Scanner scanner = new Scanner(path);

			int topicNr = 1;

			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();

				System.out.println("query: " + line);

				// TODO process

				topicNr++;
			}

			scanner.close();

		} catch (IOException e) {
			System.err.println("error with InputFile Scanner: " + e.getMessage());

			// e.printStackTrace();
		}
	}
}
