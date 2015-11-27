package com.activebeancoders.fitness.controller;

import com.activebeancoders.fitness.BaseTest;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Dan Barrese
 */
public class ActivityControllerIT extends BaseTest {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    public ActivityController activityController;

    @Test
    public void get() throws Exception {
        activityController.get("1");
    }

}