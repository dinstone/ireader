
package com.dinstone.ireader.service;

import com.dinstone.ireader.domain.Repository;

public class RepositoryManager {

    private static RepositoryManager instance = new RepositoryManager();

    public static RepositoryManager getInstance() {
        return instance;
    }

    private Repository repository;

    private RepositoryManager() {
    }

    public synchronized void setRepository(Repository repository) {
        this.repository = repository;
    }

    public synchronized Repository getRepository() {
        return repository;
    }

}
