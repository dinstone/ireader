
package ireader;

import java.util.TreeMap;
import java.util.TreeSet;

import com.dinstone.ireader.domain.Article;

public class SortedTest {

    public static void main(String[] args) {
        TreeSet<Article> ts = new TreeSet<Article>();
        for (int i = 0; i < 4; i++) {
            Article a = new Article();
            a.id = "art-" + i;
            a.read = i;
            ts.add(a);
        }

        Article a = new Article();
        a.id = "art-" + 2;
        a.read = 15;
        ts.add(a);

        System.out.println(ts);

        treeset();

        TreeMap<String, String> tm = new TreeMap<String, String>();
        tm.put("artc-99", "排行榜");
        tm.put("artc-2", "舞文");
        tm.put("artc-4", "情感");
        tm.put("artc-1", "杂谈");
        tm.put("artc-3", "鬼话");
        tm.put("artc-7", "军事");
        tm.put("artc-9", "同行");
        tm.put("artc-98", "全集");
        tm.put("artc-5", "奇幻");
        tm.put("artc-6", "商道");
        tm.put("artc-8", "历史");

        System.out.println(tm.keySet() + " : " + tm.values());
    }

    protected static void treeset() {
        TreeSet<String> ts = new TreeSet<String>();

        ts.add("artc-2");
        ts.add("artc-4");
        ts.add("artc-1");
        ts.add("artc-3");
        ts.add("artc-7");
        ts.add("artc-9");
        ts.add("artc-2");
        ts.add("artc-0");
        ts.add("artc-5");
        ts.add("artc-6");
        ts.add("artc-8");

        System.out.println("tree set " + ts);
    }

}
