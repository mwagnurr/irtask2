package at.tuwien.isis.irtask2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import at.tuwien.isis.irtask2.lucene.BM25LSimilarity;

public class SearchEngine {

	private static int MAX_RESULT_COUNT = 100;

	private static String COLLECTION_DIRECTORY = "collection";

	private String indexDirectory;

	public SearchEngine(String indexDirectory) {
		this.indexDirectory = indexDirectory;
	}

	public void search(boolean useDefault, float k1, float b, float delta) throws IOException, ParseException {

		System.out.println("Preparing Search with k1 = " + k1 + " , b = " + b + " and delta = " + delta);

		IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(indexDirectory)));
		IndexSearcher searcher = new IndexSearcher(reader);
		QueryParser parser = new QueryParser(Version.LUCENE_47, RankingEngine.CONTENT, new StandardAnalyzer(
				Version.LUCENE_47));

		String runName = "";

		if (useDefault) {
			System.out.println("using default Lucene similarity");
			runName += "default-run";
		} else if (delta == 0) {
			System.out.println("delta = 0, using normal BM25 similarity");
			searcher.setSimilarity(new BM25LSimilarity(k1, b, delta));
			runName += "BM25-run";
		} else {
			System.out.println("delta = " + delta + " , using BM25L similarity");
			searcher.setSimilarity(new BM25LSimilarity(k1, b, delta));
			runName += "BM25L-run";
		}

		// Create list of all topics
		ArrayList<String> topicList = new ArrayList<String>();
		File root = new File(RankingEngine.TOPICS_PATH);
		File[] list = root.listFiles();
		for (File file : list) {
			topicList.add(file.getName());
		}

		String topicBasePath = RankingEngine.TOPICS_PATH + "/";

		String runDescription = useDefault ? "Lucene-default" : "k_" + k1 + "_b_" + b + "_d_" + delta;

		// create output file path
		String outputFilePath = "output/" + runDescription + "_" + "exercise2_group3" + ".txt";

		File outFile = new File(outputFilePath);
		outFile.getParentFile().mkdirs();
		PrintWriter writer = new PrintWriter(outFile);

		for (String topic : topicList) {

			String topicPath = topicBasePath + topic;
			System.out.println(topicPath);

			File file = new File(topicPath);
			System.out.println(file.getAbsolutePath());

			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(topicPath), "UTF-8"));

			String currentLine;
			String line = "";
			StringTokenizer tokenizer;

			// escape special characters
			while ((currentLine = in.readLine()) != null) {
				tokenizer = new StringTokenizer(currentLine, "+ - && || ! ( ) { } [ ] ^ \" ~ * ? : \\ /");
				while (tokenizer.hasMoreTokens()) {
					String currentWord = tokenizer.nextToken();
					line += currentWord + " ";
				}
			}

			Query query = parser.parse(line);
			System.out.println("Searching for: " + query.toString(RankingEngine.CONTENT));

			TopDocs results = searcher.search(query, MAX_RESULT_COUNT);
			ScoreDoc[] hits = results.scoreDocs;
			int numTotalHits = results.totalHits;
			System.out.println(numTotalHits + " matching documents found.");

			int count = Math.min(numTotalHits, MAX_RESULT_COUNT);

			for (int i = 0; i < count; i++) {

				Document doc = searcher.doc(hits[i].doc);
				String path = doc.get(RankingEngine.PATH);

				// topic Q0 document-id rank score run-name

				// Create output string for result
				StringBuilder outputString = new StringBuilder();

				// Append topic name
				outputString.append(topic + " Q0 ");

				// Append shortened path
				int index = path.indexOf(COLLECTION_DIRECTORY);
				String outputPath = path.substring(index + COLLECTION_DIRECTORY.length() + 1);
				outputString.append(outputPath + " ");

				// Append rank
				outputString.append((i + 1) + " ");

				// Append score
				outputString.append(hits[i].score + " ");

				// Append run name
				outputString.append(runName);

				writer.write(outputString.toString().replace("\\", "/"));
				writer.write("\n");

				// System.out.println(outputString.toString());
			}

			in.close();

			System.out.println("result file for " + outputFilePath + " created");
		}

		writer.close();
		reader.close();
	}
}
