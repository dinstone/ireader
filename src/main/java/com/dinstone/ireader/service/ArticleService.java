
package com.dinstone.ireader.service;

import java.util.Date;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.dinstone.ireader.Configuration;
import com.dinstone.ireader.domain.Article;
import com.dinstone.ireader.domain.Repository;

@Service
public class ArticleService {

    private static final Logger LOG = LoggerFactory.getLogger(ArticleService.class);

    @Resource
    private Configuration configuration;

    @Resource
    private AsyncService asyncService;

    public Article findAticle(Repository repository, String articleId) {
        Article article = repository.articleMap.get(articleId);
        if (article != null && needUpdate(article.update)) {
            asyncService.updateArticle(article);
        }
        return article;
    }

    private boolean needUpdate(Date update) {
        if (update == null) {
            return true;
        }

        long diff = new Date().getTime() - update.getTime();
        int interval = configuration.getUpdateInterval();
        if (diff > interval * 1000) {
            return true;
        }

        return false;
    }

}
