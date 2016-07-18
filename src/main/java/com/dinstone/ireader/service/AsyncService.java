
package com.dinstone.ireader.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

@Service
public class AsyncService {

    public ExecutorService executor = Executors.newFixedThreadPool(1);

    public void destroy() {
        executor.shutdownNow();
        try {
            executor.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
        }
    }

    public void updateArticle(ArticleUpdater articleUpdater) {
        executor.submit(articleUpdater);
    }

}
