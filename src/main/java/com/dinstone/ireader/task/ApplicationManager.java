
package com.dinstone.ireader.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

import com.dinstone.ireader.service.RepositoryManager;

@Service
public class ApplicationManager implements ApplicationListener<ApplicationContextEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationManager.class);

    @Override
    public void onApplicationEvent(ApplicationContextEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();
        if (applicationContext.getParent() != null) {
            return;
        }

        if (event instanceof ContextRefreshedEvent) {
            LOG.info("init repository start");

            try {
                applicationContext.getBean(RepositoryManager.class).createRepository();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            LOG.info("init repository finish");
        }
    }

}
