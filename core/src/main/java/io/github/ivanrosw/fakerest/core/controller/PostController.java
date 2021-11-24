package io.github.ivanrosw.fakerest.core.controller;

import io.github.ivanrosw.fakerest.core.conf.ConfigException;
import io.github.ivanrosw.fakerest.core.model.ControllerData;
import io.github.ivanrosw.fakerest.core.model.ControllerConfig;
import io.github.ivanrosw.fakerest.core.utils.JsonUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

public class PostController extends FakeController {

    public PostController(ControllerConfig controllerConfig, ControllerData controllerData, JsonUtils jsonUtils) throws ConfigException {
        super(controllerConfig, controllerData, jsonUtils);
    }

    @Override
    public ResponseEntity<String> handle(HttpServletRequest request) {
        return new ResponseEntity<>(controllerConfig.getAnswer(), HttpStatus.OK);
    }
}
