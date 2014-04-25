package irsucks;



import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

/**
 * Main class of the ranking engine, which indexes a document collection or performs searches on it
 * using a previously-generated index.
 */
public class RankingEngine {

	/**
	 * CLI option for running the indexer
	 */
	private static final String INDEXER = "i";

	/**
	 * CLI option for setting the input topics list
	 */
	private static final String TOPICS = "t";


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
	 * Handle user arguments
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		// Create user options and command line parser
		Options options = new Options();
		options.addOption(INDEXER, false, "run indexer");
		options.addOption(SEARCH, false, "run search engine");
		Option topics = new Option(TOPICS, true, "list of input topics");
		options.addOption(topics);

		CommandLineParser parser = new PosixParser();

		try {

			// Parse user arguments
			CommandLine command = parser.parse(options, args);

			if (command.hasOption(INDEXER)) {

				System.out.println("starting to index that shit");
				
				Indexer bla = new Indexer();
				
				try {
					bla.indexDocumentCollection(COLLECTION_PATH);
					System.out.println("indexing done");
					bla.closeIndex();
					
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			} else if (command.hasOption(SEARCH)) {
				System.out.println("sarching ok start");
				
				//TODO do search
			} else {
				System.out.println("Invalid usage.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Retrieve the value of the topics option or throw an exception if it was not entered
	 * 
	 * @param command
	 * @return
	 * @throws MissingOptionException
	 */
	private static String getTopicList(CommandLine command) throws MissingOptionException {
		if (command.hasOption(TOPICS)) {
			return command.getOptionValue(TOPICS);
		} else {
			throw new MissingOptionException("Topic list was not specified. Please use the -" + TOPICS + " option");
		}
	}
}
