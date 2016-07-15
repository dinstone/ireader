
package com.dinstone.ireader.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.dinstone.ireader.service.RepositoryManager;

@Service
@RequestMapping(value = "/repository")
public class RepositoryController {

    @Resource
    private RepositoryManager repositoryManager;

    @RequestMapping(value = "/init")
    public ModelAndView init() {
        ModelAndView mav = new ModelAndView("error");
        try {
            repositoryManager.updateRepository();
        } catch (Exception e) {
            e.printStackTrace();
            mav.addObject("message", "文章库更新失败");
        }

        mav.addObject("message", "文章库更新成功");

        return mav;
    }
}
