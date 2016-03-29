package com.activebeancoders.fitness.data.controller;

import com.activebeancoders.fitness.data.BaseTestIT;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Scanner;

/**
 * @author Dan Barrese
 */
public class DataLoadControllerIT extends BaseTestIT {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    public DataLoadController dataLoadController;

    @Test
    public void reloadActivities() throws Exception {
        dataLoadController.reloadActivities("100");
        new Scanner(System.in).nextLine();
    }

}