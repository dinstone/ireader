
package com.dinstone.ireader.controller;

import java.text.SimpleDateFormat;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.dinstone.ireader.task.RepositorySyncTask;

@Service
@RequestMapping(value = "/repository")
public class RepositoryController {

    private static SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Resource
    private RepositorySyncTask repositorySyncTask;

    @RequestMapping(value = "/init")
    public ModelAndView init() {
        // repositorySyncTask.execute();

        ModelAndView mav = new ModelAndView("error");
        mav.addObject("message", "文章库正在初始化");

        System.out.println("init");
        return mav;
    }
}
