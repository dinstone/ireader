
package com.dinstone.ireader.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.dinstone.ireader.domain.Repository;

@Service
public class RepositoryManager {

    @Resource
    private RepositoryService repositoryService;

    private Repository repository;

    public synchronized void createRepository() throws Exception {
        initRepository();
    }

    public synchronized void updateRepository() throws Exception {
        initRepository();
    }

    protected void initRepository() throws Exception {
        if (repository == null) {
            repository = repositoryService.loadRepository();
            if (repository == null) {
                repository = repositoryService.createRepository();
            }
        } else {
            repositoryService.updateRepository(repository);
        }
    }

    public Repository getRepository() {
        return repository;
    }

}
