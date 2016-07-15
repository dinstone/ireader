
package com.dinstone.ireader.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

import com.dinstone.ireader.domain.Repository;
import com.dinstone.ireader.service.RepositoryManager;
import com.dinstone.ireader.service.RepositoryService;

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

            RepositoryService service = applicationContext.getBean(RepositoryService.class);
            try {
                Repository repository = service.loadRepository();
                if (repository == null) {
                    repository = service.createRepository();
                }

                service.writeRepository(repository);
                RepositoryManager.getInstance().setRepository(repository);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            LOG.info("init repository finish");
        }
    }

}
