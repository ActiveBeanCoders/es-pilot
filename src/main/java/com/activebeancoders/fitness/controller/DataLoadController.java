package com.activebeancoders.fitness.controller;

import com.activebeancoders.fitness.common.JsonResponse;
import com.activebeancoders.fitness.service.DataLoaderWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Dan Barrese
 */
//@RestController
// TODO: how to do this now?
//@PreAuthorize("hasAuthority('ROLE_DOMAIN_USER')")
public class DataLoadController {

    private static final Logger log = LoggerFactory.getLogger(DataLoadController.class);

    @Autowired
    private DataLoaderWorker indexerWorker;

    /**
     * Rebilds the index with random data.
     */
    @RequestMapping(value = RestEndpoint.RELOAD, method = RequestMethod.POST)
    @ResponseBody
    public JsonResponse reloadActivities(@RequestParam(required = false, defaultValue = "20") final String count) {
        indexerWorker.loadRandomRecords(Long.valueOf(count));
        JsonResponse jsonResponse = new JsonResponse();
        jsonResponse.value = "Okay, I'm working on it!";
        return jsonResponse;
    }

    @RequestMapping(value = RestEndpoint.RELOAD_STATUS, method = RequestMethod.GET)
    @ResponseBody
    public JsonResponse reloadActivitiesStatus() {
        JsonResponse jsonResponse = new JsonResponse();
        jsonResponse.value =  indexerWorker.getLastKnownStatus();
        return jsonResponse;
    }

}
