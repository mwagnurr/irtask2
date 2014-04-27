package at.tuwien.isis.irtask2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class SearchEngine {

	private static int MAX_RESULT_COUNT = 100;

	private static String COLLECTION_DIRECTORY = "collection";

	private String indexDirectory;

	public SearchEngine(String indexDirectory) {
		this.indexDirectory = indexDirectory;
	}

	public void search() throws IOException, ParseException {

		IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(indexDirectory)));
		IndexSearcher searcher = new IndexSearcher(reader);
		QueryParser parser = new QueryParser(Version.LUCENE_47, RankingEngine.CONTENT, new StandardAnalyzer(
				Version.LUCENE_47));

		// TODO
		searcher.setSimilarity(new BM25Similarity());

		// Create list of all topics
		ArrayList<String> topicList = new ArrayList<String>();
		File root = new File(RankingEngine.TOPICS_PATH);
		File[] list = root.listFiles();
		for (File file : list) {
			topicList.add(file.getName());
		}

		String topicBasePath = RankingEngine.TOPICS_PATH + "/";

		for (String topic : topicList) {

			String topicPath = topicBasePath + topic;

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

				// TODO topic Q0 document-id rank score run-name

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
				outputString.append("TEST");

				System.out.println(outputString.toString());
			}

			in.close();
		}

		reader.close();
	}
}
