
package com.dinstone.ireader.task;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dinstone.ireader.service.RepositoryService;

@Service
public class RepositorySyncTask {

    private static final Logger LOG = LoggerFactory.getLogger(RepositorySyncTask.class);

    @Autowired
    private RepositoryService repositoryService;

    public void execute() {
        waitMoment();

        LOG.info("repository sync task begin");
        try {
            repositoryService.updateRepository();
        } catch (Exception e) {
            LOG.warn("repository sync task error", e);
        }
        LOG.info("repository sync task finish");
    }

    private void waitMoment() {
        Random r = new Random();
        int s = r.nextInt(60);
        try {
            Thread.sleep(s * 1000);
        } catch (InterruptedException ignored) {
        }
    }

}
