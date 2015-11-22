
package com.dinstone.ireader.service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dinstone.ireader.domain.Article;
import com.dinstone.ireader.domain.Pagenation;

public class PagenationUpdater implements Callable<Pagenation> {

    private static final Logger LOG = LoggerFactory.getLogger(PagenationUpdater.class);

    private Pagenation page;

    private List<Article> articles;

    public PagenationUpdater(Pagenation page) {
        this.page = page;
        this.articles = page.articles;
    }

    @Override
    public Pagenation call() throws Exception {
        for (Article article : articles) {
            try {
                update(article);
            } catch (IOException e) {
                LOG.warn("update " + article + " error", e);
            }
        }

        return page;
    }

    private void update(Article article) throws IOException {
        String url = article.href;
        int tryCount = 1;
        while (true) {
            try {
                Document doc = Jsoup.connect(url).get();

                // extract auth,category,status
                Elements bases = doc.select("span.TA");
                for (Element base : bases) {
                    // 作者: 御风楼主人 　 分类: 鬼话 　 [全文完]
                    String[] bp = base.text().trim().replaceAll("　", "").split(" +");
                    if (bp.length >= 5) {
                        article.auth = bp[1];
                        article.status = bp[4];
                    }
                }

                // // extract parts
                // List<Part> parts = new LinkedList<Part>();
                // Elements links = doc.select("a[href]");
                // for (Element link : links) {
                // String href = link.attr("abs:href");
                // if (href.contains("read_")) {
                // String name = link.text();
                // int index = findIndex(name);
                //
                // Part part = new Part();
                // part.index = index;
                // part.name = name;
                // part.url = href;
                //
                // parts.add(part);
                // }
                // }
                //
                // Part[] array = parts.toArray(new Part[0]);
                // Arrays.sort(array, new Comparator<Part>() {
                //
                // @Override
                // public int compare(Part o1, Part o2) {
                // return o1.index - o2.index;
                // }
                // });
                //
                // article.parts = array;

                break;
            } catch (Exception e) {
                tryCount++;
                if (tryCount > 3) {
                    throw e;
                }
            }
        }
    }

    private static int findIndex(String name) {
        try {
            return Integer.parseInt(name.replaceAll("\\D+", ""));
        } catch (Exception e) {
            return 0;
        }
    }

    public static void main(String[] args) throws IOException {
        // String base = " 作者: 御风楼主人 　 分类: 鬼话 　 [全文完]";
        // String[] bp = base.trim().replaceAll("　", "").split(" +");
        // for (String p : bp) {
        // System.out.println(p);
        // }

        PagenationUpdater updater = new PagenationUpdater(null);
        Article article = new Article();
        article.href = "http://www.yi-see.com/art_14990_7513.html";
        updater.update(article);

        System.out.println(article);
    }
}
