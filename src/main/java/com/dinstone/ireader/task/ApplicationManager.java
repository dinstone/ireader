
package com.dinstone.ireader.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

@Service
public class ApplicationManager implements ApplicationListener<ApplicationContextEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationManager.class);

    @Override
    public void onApplicationEvent(ApplicationContextEvent event) {
        LOG.info("onApplicationEvent {}", event);
        ApplicationContext applicationContext = event.getApplicationContext();
        if (applicationContext.getParent() != null) {
            return;
        }

        if (event instanceof ContextRefreshedEvent) {
            // RepositorySyncTask repositorySyncTask = applicationContext.getBean(RepositorySyncTask.class);
            // repositorySyncTask.execute();
        }
    }

}
