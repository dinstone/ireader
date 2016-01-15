
package com.dinstone.ireader.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.dinstone.ireader.service.RepositoryManager;
import com.dinstone.ireader.service.RepositoryService;

@Service
@RequestMapping(value = "/repository")
public class RepositoryController {

    @Resource
    private RepositoryService repositoryService;

    @RequestMapping(value = "/init")
    public ModelAndView init() {
        // repositorySyncTask.execute();

        repositoryService.updateRepository(RepositoryManager.getInstance().getRepository());

        ModelAndView mav = new ModelAndView("error");
        mav.addObject("message", "文章库正在初始化");

        System.out.println("init");
        return mav;
    }
}
