package com.zhutc;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.FieldType.NumericType;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexReaderContext;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.flexible.core.nodes.FieldQueryNode;
import org.apache.lucene.queryparser.flexible.standard.nodes.TermRangeQueryNode;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;


public class LuceneDemo {
	
	 private static String filePath = "/home/beecloud/LuceneTest/test";
	 private static String indexPath = "/home/beecloud/LuceneTest/index";
	
	  
	 public static void main(String[]args) throws IOException, ParseException{
		 Indexer indexer = new Indexer(indexPath);
//		 int n=indexer.indexFile(filePath, new FileFilter() {
//			
//			@Override
//			public boolean accept(File pathname) {
//				// TODO Auto-generated method stub
//				return pathname.getName().endsWith(".txt");
//			}
//		});
//		 indexer.closeWriter();
//		 System.out.println("n-----"+n);
		 String queryString="\"class SnowballFilter\"";
		 indexer.search(queryString);
		 indexer.closeReader();
		 indexer.closeDirectory();
		 
//		Analyzer analyzer = new StandardAnalyzer();
//
//		// Store the index in memory:
//		Directory directory = new RAMDirectory();
//		// To store an index on disk, use this instead:
//		// Directory directory = FSDirectory.open("/tmp/testindex");
//		IndexWriterConfig config = new IndexWriterConfig(analyzer);
//		IndexWriter iwriter = new IndexWriter(directory, config);
//		Document doc = new Document();
//		String text = "This is the text to be indexed.";
//		doc.add(new Field("fieldname", text, TextField.TYPE_STORED));
//		iwriter.addDocument(doc);
//		iwriter.close();
//
//		// Now search the index:
//		DirectoryReader ireader = DirectoryReader.open(directory);
//		IndexSearcher isearcher = new IndexSearcher(ireader);
//		// Parse a simple query that searches for "text":
//		QueryParser parser = new QueryParser("fieldname", analyzer);
//		Query query = parser.parse("text");
//		ScoreDoc[] hits = isearcher.search(query, null, 1000).scoreDocs;
//		System.out.println(hits.length);
//		// Iterate through the results:
//		for (int i = 0; i < hits.length; i++) {
//			Document hitDoc = isearcher.doc(hits[i].doc);
//			System.out.println(hitDoc.get("fieldname"));
//		}
//		ireader.close();
//		directory.close();
	 }
}
class Indexer{
	 private StandardAnalyzer analyzer = null;
	 private IndexWriter indexWriter=null;
	 private IndexReader indexReader=null;
	 private IndexSearcher indexSearcher=null;
	 private Directory directory=null;
	 private IndexWriterConfig confg = null;
	 public Indexer(String indexPath) throws IOException{
		   directory = FSDirectory.open(new File(indexPath).toPath());
		   analyzer=new StandardAnalyzer();
		   confg=new IndexWriterConfig(analyzer);
	 }
	 public void closeWriter() throws IOException{
		 if(null!=indexWriter){
			 indexWriter.close();
		 }
	 }
	 public void closeReader() throws IOException{
		 if(indexReader!=null)
			 indexReader.close();
	 }
	 public void closeDirectory() throws IOException{
		 if(directory!=null)
			 directory.close();
	 }
	 public int indexFile(String filePath,FileFilter filter) throws IOException{
		 if(this.indexWriter==null)
			 this.indexWriter=new IndexWriter(directory, confg);
		 File[] files = new File(filePath).listFiles();
		 for(File file:files){
			 if(!file.isDirectory()&&!file.isHidden()&&file.exists()&&file.canRead()&&(filter==null||filter.accept(file))){
				 Document doc=getDocument(file);
				 indexWriter.addDocument(doc);
			 }	 
		 }
		 return this.indexWriter.numDocs();
	 }
	 public int indexFiles(String[] filePaths,FileFilter filter) throws IOException{
		 int n=0;
		 for(String filePath:filePaths)
			 n+=this.indexFile(filePath, filter);
		 return n;
	 }
	 public void search(String queryString) throws ParseException, IOException{
		 indexReader=DirectoryReader.open(directory);
		 indexSearcher=new IndexSearcher(indexReader);
		 String[] fields = {"fieldname","content"};
		  QueryParser queryParser = new MultiFieldQueryParser(fields,analyzer);
		  Query query = queryParser.parse(queryString);
//		  query= new TermRangeQuery("fieldname", new BytesRef("test1".getBytes()), new BytesRef("test2".getBytes()), true,true);
//		  query=new TermQuery(new Term("fieldname", "test2.txt"));
//		  query=new PrefixQuery(new Term("filePath","beeclou"));
//		  query=new TermQuery(new Term("filePath", "beeclou"));
		  
		  //======================================
		  PhraseQuery phraseQuery = new PhraseQuery();
		  
		//  phraseQuery.setSlop(5);
		  //进行短语匹配
//		  phraseQuery.add(new Term("content","added"),0);
//		  phraseQuery.add(new Term("content","java"),3);
//		  phraseQuery.add(new Term("content","having"),7);
		 
	  
//		  phraseQuery.setSlop(4);
//		  phraseQuery.add(new Term("content","java"));
//		  phraseQuery.add(new Term("content","added"));
		  
		  System.out.println(query.toString());
		 WildcardQuery wildcardQuery = new WildcardQuery(new Term("fieldname","test2*"));
		 
		 
		 BooleanQuery booleanQuery=new BooleanQuery();
		 booleanQuery.add(new TermQuery(new Term("filePath","test")), Occur.MUST);
		 booleanQuery.add(new TermQuery(new Term("filePath","test1")), Occur.MUST);
		  TopDocs docs = indexSearcher.search(query, 100000);
		  System.out.println("搜索到:"+docs.totalHits+"条结果");
		  printResult(docs);
	 }
	private void printResult(TopDocs docs) {
		// TODO Auto-generated method stub
		ScoreDoc[] sds = docs.scoreDocs;
		for(ScoreDoc sd:sds){
			try {
				Document document = indexSearcher.doc(sd.doc);
				printDocument(document);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	private void printDocument(Document document) {
		System.out.println("fieldname---"+document.get("fieldname"));
		System.out.println("filePath---"+document.get("filePath"));
		String content = document.get("content");
		System.out.println("content---");
		System.out.println(content);
		
		System.out.println("=========================================================================");
	}
	private Document getDocument(File file) throws IOException {
		// TODO Auto-generated method stub
		Document doc=new Document();
		doc.add((new Field("fieldname", file.getName(), TextField.TYPE_STORED)));
		doc.add(new Field("content",new FileReader(file) , TextField.TYPE_NOT_STORED));
		doc.add(new Field("filePath", file.getCanonicalPath(), TextField.TYPE_STORED));
		return doc;
	}
	 
}
