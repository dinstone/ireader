
package com.dinstone.ireader.task;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.dinstone.ireader.service.RepositoryManager;
import com.dinstone.ireader.service.RepositoryService;

@Service
public class RepositorySyncTask {

    private static final Logger LOG = LoggerFactory.getLogger(RepositorySyncTask.class);

    @Resource
    private RepositoryService repositoryService;

    public void execute() {
        LOG.info("repository sync task begin");
        try {
            repositoryService.updateRepository(RepositoryManager.getInstance().getRepository());
        } catch (Exception e) {
            LOG.warn("repository sync task error", e);
        }
        LOG.info("repository sync task finish");
    }

}
