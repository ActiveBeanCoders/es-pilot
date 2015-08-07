package com.activebeancoders.controller;

import com.activebeancoders.dao.ActivityDao;
import com.activebeancoders.entity.Activity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
public class ActivityController {

    @Autowired
    private ActivityDao activityDao;

    @RequestMapping(value = "/activity", method = RequestMethod.GET)
    public Activity getActivity(@RequestParam String id, Model model) {
        Activity activity = activityDao.get(id);
        model.addAttribute(activity); // TODO: what does this do?
        return activity;
    }

    @RequestMapping(value = "/activity", method = RequestMethod.POST)
    public String addActivity(@ModelAttribute Activity activity, Model model) {
        model.addAttribute("activity", activity); // TODO: what does this do?
        activityDao.save(activity);
        return "result";
    }

//    @RequestMapping(value = "/activityUpdate", method = RequestMethod.POST)
//    public String updateActivity(@ModelAttribute Activity activity, Model model) {
//        model.addAttribute("activity", activity); // TODO: what does this do?
//        activityDao.update(activity);
//        return "result";
//    }

}
