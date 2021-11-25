package io.github.ivanrosw.fakerest.core.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.ivanrosw.fakerest.core.model.ControllerConfig;
import io.github.ivanrosw.fakerest.core.model.ControllerData;
import io.github.ivanrosw.fakerest.core.model.ControllerMode;
import io.github.ivanrosw.fakerest.core.utils.JsonUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

public class DeleteController extends FakeModifyController {

    public DeleteController(ControllerMode mode, ControllerData controllerData, ControllerConfig controllerConfig, JsonUtils jsonUtils) {
        super(mode, controllerData, controllerConfig, jsonUtils);
    }

    @Override
    protected ResponseEntity<String> handleOne(HttpServletRequest request) {
        ResponseEntity<String> result;

        String key = controllerData.buildKey(getUrlIds(request), controllerConfig.getIdParams());
        if (controllerData.containsKey(controllerConfig.getUri(), key)) {
            ObjectNode data = controllerData.getData(controllerConfig.getUri(), key);
            controllerData.deleteData(controllerConfig.getUri(), key);

            result = new ResponseEntity<>(data.toString(), HttpStatus.OK);
        } else {
            ObjectNode error = jsonUtils.createJson();
            jsonUtils.putString(error, DESCRIPTION_PARAM, String.format(KEY_NOT_FOUND, key));
            result = new ResponseEntity<>(error.toString(), HttpStatus.NOT_FOUND);
        }

        return result;
    }
}
