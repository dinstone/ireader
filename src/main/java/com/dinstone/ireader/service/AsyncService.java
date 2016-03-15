
package com.dinstone.ireader.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.dinstone.ireader.Configuration;
import com.dinstone.ireader.domain.Article;

@Service
public class AsyncService {

    @Resource
    private Configuration configuration;

    public ExecutorService executor = Executors.newFixedThreadPool(1);

    public void destroy() {
        executor.shutdownNow();
        try {
            executor.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
        }
    }

    public void updateArticle(Article article) {
        executor.submit(new ArticleUpdater(article, configuration));
    }

}
