
package com.dinstone.ireader.task;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.dinstone.ireader.service.SynchronizeService;

@Service
public class RepositorySyncTask {

    private static final Logger LOG = LoggerFactory.getLogger(RepositorySyncTask.class);

    @Resource
    private SynchronizeService synchronizeService;

    public void execute() {
        LOG.info("repository sync task begin");
        try {
            synchronizeService.extractCategorys();
            synchronizeService.extractArticles();
            synchronizeService.updateArticles();
            synchronizeService.buildCategroys();
        } catch (Exception e) {
            LOG.warn("repository sync task error", e);
        }
        LOG.info("repository sync task finish");
    }

}
