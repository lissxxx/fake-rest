package io.github.ivanrosw.fakerest.core.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.ivanrosw.fakerest.core.conf.ConfigException;
import io.github.ivanrosw.fakerest.core.model.ControllerData;
import io.github.ivanrosw.fakerest.core.model.ControllerMode;
import io.github.ivanrosw.fakerest.core.model.ControllerConfig;
import io.github.ivanrosw.fakerest.core.utils.JsonUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class GetController extends FakeController {

    public GetController(ControllerConfig controllerConfig, ControllerData controllerData, JsonUtils jsonUtils) throws ConfigException {
        super(controllerConfig, controllerData, jsonUtils);
    }

    @Override
    public ResponseEntity<String> handle(HttpServletRequest request) {
        if (mode == ControllerMode.ID) {
            return handleId(request);
        } else {
            return handleNoId();
        }
    }

    private ResponseEntity<String> handleId(HttpServletRequest request) {
        ResponseEntity<String> result;

        Map<String, String> urlIds = getUrlIds(request);
        String key = controllerData.buildKey(urlIds, controllerConfig.getIdParams());
        ObjectNode data = controllerData.getData(controllerConfig.getUri(), key);

        if (data != null) {
            result =  new ResponseEntity<>(data.toString(), HttpStatus.OK);
        } else {
            result = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return result;
    }


    private ResponseEntity<String> handleNoId() {
        return new ResponseEntity<>(controllerConfig.getAnswer(), HttpStatus.OK);
    }

}
