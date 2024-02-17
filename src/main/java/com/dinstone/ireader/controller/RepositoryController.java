
package com.dinstone.ireader.controller;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.dinstone.ireader.service.RepositoryService;

@Service
@RequestMapping(value = "/view/repository")
public class RepositoryController {

    @Autowired
    private RepositoryService repositoryService;

    @RequestMapping(value = "/init")
    public ModelAndView init() {
        ModelAndView mav = new ModelAndView("error");
        try {
            repositoryService.updateRepository();
        } catch (Exception e) {
            mav.addObject("message", "文章库更新失败");
        }

        mav.addObject("message", "文章库更新成功");

        return mav;
    }
}
