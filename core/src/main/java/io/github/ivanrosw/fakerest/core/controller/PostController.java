package io.github.ivanrosw.fakerest.core.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.ivanrosw.fakerest.core.model.ControllerData;
import io.github.ivanrosw.fakerest.core.model.ControllerConfig;
import io.github.ivanrosw.fakerest.core.model.ControllerMode;
import io.github.ivanrosw.fakerest.core.utils.JsonUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

public class PostController extends FakeModifyController {

    public PostController(ControllerMode mode, ControllerData controllerData, ControllerConfig controllerConfig, JsonUtils jsonUtils) {
        super(mode, controllerData, controllerConfig, jsonUtils);
    }

    @Override
    protected ResponseEntity<String> handleOne(HttpServletRequest request) {
        ResponseEntity<String> result;
        try {
            String body = readBody(request);
            if (body != null && !body.isBlank()) {
                result = saveOne(body);
            } else {
                result = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            result = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return result;
    }

    private ResponseEntity<String> saveOne(String body) {
        ResponseEntity<String> result;
        ObjectNode bodyJson = jsonUtils.toObjectNode(body);

        if (bodyJson != null && !bodyJson.isNull()) {
            String key = controllerData.buildKey(bodyJson, controllerConfig.getIdParams());

            if (!controllerData.containsKey(controllerConfig.getUri(), key)) {
                controllerData.putData(controllerConfig.getUri(), key, bodyJson);
                result = new ResponseEntity<>(bodyJson.toString(), HttpStatus.OK);
            } else {
                ObjectNode error = jsonUtils.createJson();
                jsonUtils.putString(error, DESCRIPTION_PARAM, String.format(KEY_ALREADY_EXIST, key));
                result = new ResponseEntity<>(error.toString(), HttpStatus.BAD_REQUEST);
            }
        } else {
            ObjectNode error = jsonUtils.createJson();
            jsonUtils.putString(error, DESCRIPTION_PARAM, String.format(DATA_NOT_JSON, body));
            result = new ResponseEntity<>(error.toString(), HttpStatus.BAD_REQUEST);
        }
        return result;
    }

}