package at.tuwien.isis.irtask2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class Indexer {

	private IndexWriter indexWriter;
	
	private String indexDirectory;

	public Indexer(String indexDirectory) {

		try {
			File indexFileDir = new File(indexDirectory);
			this.indexDirectory = indexFileDir.getAbsolutePath();
			System.out.println("Path to be indexed: " + indexFileDir.getAbsolutePath());
			Directory dir = FSDirectory.open(indexFileDir);
			IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_47, new StandardAnalyzer(
					Version.LUCENE_47));

			indexWriter = new IndexWriter(dir, indexWriterConfig);

			System.out.println("IndexWriter created.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void index(String path) {
		try {
			indexDocumentCollection(path);
			indexWriter.close();
			System.out.println("Indexing completed. \nIndex written to: " + indexDirectory);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void indexDocumentCollection(String path) throws IOException {
		File root = new File(path);
		File[] list = root.listFiles();

		for (File file : list) {
			if (file.isDirectory()) {
				System.out.println("Indexing files in directory: " + file.getAbsoluteFile());
				indexDocumentCollection(file.getAbsolutePath());
			} else {
				Document doc = new Document();

				Field pathField = new StringField("file_path", file.getPath(), Field.Store.YES);
				doc.add(pathField);

				BufferedReader buff = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
				Field contentField = new TextField("file_content", buff);
				doc.add(contentField);
				indexWriter.addDocument(doc);
			}
		}
	}
}
