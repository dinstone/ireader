package ireader;

import java.io.File;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class LuceneTest {

    @Test
    public void createIndex() throws Exception {
        //1、创建一个Director对象，指定索引库保存的位置。
        //把索引库保存在内存中
        //Directory directory = new RAMDirectory();
        //把索引库保存在磁盘
        Directory directory = FSDirectory.open(new File("logs").toPath());

        //2、基于Directory对象创建一个IndexWriter对象
        IndexWriterConfig config = new IndexWriterConfig(new IKAnalyzer());
        IndexWriter indexWriter = new IndexWriter(directory, config);

        indexWriter.deleteAll();

        //3、读取磁盘上的文件，对应每个文件创建一个文档对象。
        //取文件名
        String title = "《宋朝那些事呀》";
        //文件的路径
        String url = "https://yiduzkk.com/art_35437_8661.html";
        //文件的内容
        String author = "_小鹿撞撞_";

        //创建Field
        //参数1：域的名称，参数2：域的内容，参数3：是否存储
        Field fieldTitle = new TextField("title", title, Field.Store.YES);
        Field fieldAuthor = new TextField("author", author, Field.Store.YES);

        //不需要分词和索引，只进行存储
        Field fieldPath = new StoredField("path", url);

        //存储长整型数据，做运算使用，不能取值
        //Field fieldSizeValue = new LongPoint("size", fileSize);
        //只存储
        //Field fieldSizeStore = new StoredField("size", fileSize);

        //创建文档对象
        Document document = new Document();
        //向文档对象中添加域
        document.add(fieldAuthor);
        document.add(fieldTitle);
        document.add(fieldPath);

        //document.add(fieldSizeValue);
        //document.add(fieldSizeStore);

        //5、把文档对象写入索引库
        indexWriter.addDocument(document);
        indexWriter.flush();

        //6、关闭indexwriter对象
        indexWriter.close();
    }

    @Test
    public void searchIndex() throws Exception {
        //1、创建一个Director对象，指定索引库的位置
        Directory directory = FSDirectory.open(new File("logs").toPath());
        //2、创建一个IndexReader对象
        IndexReader indexReader = DirectoryReader.open(directory);
        //3、创建一个IndexSearcher对象，构造方法中的参数indexReader对象。
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        //4、创建一个Query对象，TermQuery
        Query query = new TermQuery(new Term("title", "那些"));
        //5、执行查询，得到一个TopDocs对象
        //参数1：查询对象 参数2：查询结果返回的最大记录数
        TopDocs topDocs = indexSearcher.search(query, 10);
        //6、取查询结果的总记录数
        System.out.println("查询总记录数：" + topDocs.totalHits);
        //7、取文档列表
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        //8、打印文档中的内容
        for (ScoreDoc doc : scoreDocs) {
            //取文档id
            int docId = doc.doc;
            //根据id取文档对象
            Document document = indexSearcher.doc(docId);
            System.out.println(document.get("author"));
            System.out.println(document.get("title"));
            System.out.println(document.get("path"));
            //System.out.println(document.get("content"));
            System.out.println("-----------------");
        }
        //9、关闭IndexReader对象
        indexReader.close();
    }


}
