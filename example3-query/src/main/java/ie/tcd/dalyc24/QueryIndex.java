package ie.tcd.dalyc24;

import java.io.IOException;

import java.nio.file.Paths;
import java.nio.file.Files;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;


import org.apache.lucene.document.Document;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import org.apache.lucene.index.Term;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.search.similarities.BM25Similarity;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;
import java.io.File;
import java.io.FileWriter;   
import java.io.IOException;  
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class QueryIndex
{
	static String query_dir = "../cran/cran.qry";
	// Limit the number of search results we get
	private static int MAX_RESULTS = 10;
	static String index_dir = "../index";
	static String file_dir = "../cran/cran.all.1400";
	static String dir_path="../index";


    public static HashMap<Integer, String> queries_statement(String dir) throws IOException {
		BufferedReader break_line = new BufferedReader(new FileReader(dir));
		String line=null;
		String last_line = null;
        int count=0;
        HashMap<Integer, String> processed = new HashMap<Integer, String>();
		processed.clear();
		while((line = break_line.readLine())!=null) {  
			String append_line=null;

			if(line.startsWith(".I")) {
				count++;	
				last_line = ".I";
			}
			else if(line.startsWith(".W")) 
			{
					last_line= ".W";
				}
			else {
				if(last_line == ".W") {
					if(processed.get(count)!=null) {
						append_line = processed.get(count);
						processed.put(count, append_line+" "+line);
					}
					else
						processed.put(count, line);
				}
			}
			}
		
		break_line.close();
		return processed;
	}

	public static File file_creater() {
		try {
		      File myObj = new File("../cran/Query.results");
		      if (myObj.createNewFile()) {
		        System.out.println("File created: " + myObj.getName());
		      } else {
		        System.out.println("File already exists.");
		      }
		      return myObj;
		    } catch (IOException e) {
		      System.out.println("An error occurred.");
		      e.printStackTrace();
		    }
		return null;
	}

	
	public static void main(String[] args) throws Exception 
	{
		BufferedReader break_line = new BufferedReader(new FileReader(file_dir));
		String line=null;
		String last_line = null;
        int count=0;
        HashMap<Integer, HashMap<String, String>> docSet = new HashMap<Integer, HashMap<String, String>>();
		docSet.clear();
        while((line = break_line.readLine())!=null) {  
			String append_line;
			if(line.startsWith(".I")) {
				count++;
				docSet.put(count, new HashMap<String, String>());			
			}
			else if(line.startsWith(".A")) 
			{
				last_line = ".A";
			}
			
			else if(line.startsWith(".T")) 
				{
						
				last_line = ".T";
				
				}
			else if(line.startsWith(".B")) 
				{
				last_line = ".B";
				
				}
			else if(line.startsWith(".W")) 
				{
					last_line= ".W";
					}
			else {
				if(last_line == ".A") {
					if(docSet.get(count).get("Author")!=null) {
						append_line = docSet.get(count).get("Author");
						docSet.get(count).put("Author", append_line+" "+line);}
						else{
							docSet.get(count).put("Author", line);}
							}
				
			else if(last_line == ".T") {
					if(docSet.get(count).get("Title")!=null) {
						append_line = docSet.get(count).get("Title");
						docSet.get(count).put("Title", append_line+" "+line);
				}
					else {
					docSet.get(count).put("Title", line);
					}}
			else if (last_line == ".B") {
					if(docSet.get(count).get("BioGraphy")!=null) {
							append_line = docSet.get(count).get("BioGraphy");
							docSet.get(count).put("BioGraphy", append_line+" "+line);
						}
						else
						{
							docSet.get(count).put("BioGraphy", line);
					}
					}
			else if (last_line == ".W") {
				if(docSet.get(count).get("Text")!=null) {
					append_line = docSet.get(count).get("Text");
					docSet.get(count).put("Text", append_line+" "+line);
				}
				else
				{
					docSet.get(count).put("Text", line);
			}
				
			}
				
					
					
				}
			};break_line.close();
	    try {
	      Directory dir = FSDirectory.open(Paths.get(dir_path));
	      Analyzer analyzer = new EnglishAnalyzer(EnglishAnalyzer.getDefaultStopSet());
	      IndexWriterConfig indexingConfig = new IndexWriterConfig(analyzer);
		  indexingConfig.setOpenMode(OpenMode.CREATE);
		  indexingConfig.setSimilarity(new BM25Similarity());
	      IndexWriter writer = new IndexWriter(dir, indexingConfig);
	      for (Map.Entry<Integer, HashMap<String, String>> entry : docSet.entrySet()) {
	        Document doc = new Document();
	        doc.add(new TextField("Id", Integer.toString(entry.getKey()), Field.Store.YES));
	        for(Map.Entry<String, String> properties : docSet.get(entry.getKey()).entrySet()) {
	        	doc.add(new TextField(properties.getKey(), properties.getValue(), Field.Store.YES));
	        	
	        }
	        writer.addDocument(doc);
	      }
	      writer.close();
	
	    } catch (IOException e) {
	      System.out.println(e);
	    }


		HashMap<Integer, String> queries = queries_statement(query_dir);

		// File newFile = file_creater();
		// search(processed_qrys,index_dir);

		System.out.println("Aditya:"+queries.size());
	    IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(Paths.get(index_dir))));
		searcher.setSimilarity(new BM25Similarity());
	    Analyzer analyzer =  new EnglishAnalyzer(EnglishAnalyzer.getDefaultStopSet());
	    HashMap<String, Float> score = new HashMap<String, Float>();
	    score.put("Title", 0.60f);
	    score.put("Author", 0.045f);
	    score.put("BioGraphy", 0.025f);
	    score.put("Text", 0.45f);
	    MultiFieldQueryParser queryParser = new MultiFieldQueryParser(
			new String[] {"Title", "Author", "BioGraphy", "Text"},
				analyzer, score);
	    File newFile = file_creater();
	    FileWriter myWriter = new FileWriter(newFile, false);
		
	    for (Map.Entry<Integer, String> q : queries.entrySet()) {
	    	
	    	String qry = q.getValue();
	    	//System.out.println(qry);
			Query query = queryParser.parse(QueryParser.escape(qry));
	          
	        TopDocs topDocs = searcher.search(query, 1000);
	        ScoreDoc[] hits = topDocs.scoreDocs;
	        for(ScoreDoc sd:hits)
	        {
	        	Document docc = searcher.doc(sd.doc);
	        	int s = Integer.parseInt(docc.get("Id"));  
	        	//System.out.println((q.getKey())+ s +sd.score);  
	        	myWriter.write((q.getKey())+ " Q0 "+s+ " 0 " +sd.score +" EXP\n");
	        }  
	      }       
	    myWriter.close();
	}
}
