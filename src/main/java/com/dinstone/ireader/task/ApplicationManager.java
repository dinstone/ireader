
package com.dinstone.ireader.task;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

import com.dinstone.ireader.service.RepositoryService;

@Service
public class ApplicationManager implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationManager.class);

    @Autowired
    private RepositoryService repositoryService;


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        LOG.info("init repository start");

        try {
            repositoryService.updateRepository();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        LOG.info("init repository finish");
    }
}
