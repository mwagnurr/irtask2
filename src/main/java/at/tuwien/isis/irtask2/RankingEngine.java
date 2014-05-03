package at.tuwien.isis.irtask2;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

/**
 * Main class of the ranking engine, which indexes a document collection or
 * performs searches on it using a previously-generated index.
 */
public class RankingEngine {

	/**
	 * CLI option for running the indexer
	 */
	private static final String INDEXER = "i";

	/**
	 * Path to the document index
	 */
	private static final String INDEX_PATH = "index";

	/**
	 * Path to the document collection
	 */
	private static final String COLLECTION_PATH = "collection";

	/**
	 * CLI option for running the search engine
	 */
	private static final String SEARCH = "s";

	/**
	 * Name of the text field where document content is stored
	 */
	public static final String CONTENT = "content";

	/**
	 * Name of the String field where document path is stored
	 */
	public static final String PATH = "path";

	/**
	 * Path to the topics
	 */
	public static final String TOPICS_PATH = "topics";

	// parameters for BM25 similarity
	private static final String K_PARAM = "k1";
	private static final String B_PARAM = "b";
	private static final String DELTA_PARAM = "d";

	/**
	 * Handle user arguments
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		// Create user options and command line parser
		Options options = new Options();
		options.addOption(INDEXER, false, "run indexer");
		options.addOption(SEARCH, false, "run search engine");
		options.addOption(K_PARAM, true, "k1 parameter for BM25 search");
		options.addOption(B_PARAM, true, "b parameter for BM25 search");
		options.addOption(DELTA_PARAM, true, "delta parameter for BM25L search");

		CommandLineParser parser = new PosixParser();

		try {
			// Parse user arguments
			CommandLine command = parser.parse(options, args);

			if (command.hasOption(INDEXER)) {
				System.out.println("Creating index...");
				Indexer indexer = new Indexer(INDEX_PATH);
				indexer.index(COLLECTION_PATH);
			} else if (command.hasOption(SEARCH)) {
				System.out.println("Beginning search...");
				SearchEngine searchEngine = new SearchEngine(INDEX_PATH);

				float k1 = command.hasOption(K_PARAM) ? Float
						.parseFloat(command.getOptionValue(K_PARAM)) : 1.2f;
				float b = command.hasOption(B_PARAM) ? Float.parseFloat(command
						.getOptionValue(B_PARAM)) : 0.75f;
				float delta = command.hasOption(DELTA_PARAM) ? Float
						.parseFloat(command.getOptionValue(DELTA_PARAM)) : 0;						

				searchEngine.search(k1, b, delta);
			} else {
				System.out.println("Invalid usage.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
