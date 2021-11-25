package io.github.ivanrosw.fakerest.core.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.ivanrosw.fakerest.core.model.ControllerConfig;
import io.github.ivanrosw.fakerest.core.model.ControllerData;
import io.github.ivanrosw.fakerest.core.model.ControllerMode;
import io.github.ivanrosw.fakerest.core.utils.JsonUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class PutController extends FakeModifyController {

    public PutController(ControllerMode mode, ControllerData controllerData, ControllerConfig controllerConfig, JsonUtils jsonUtils) {
        super(mode, controllerData, controllerConfig, jsonUtils);
    }

    @Override
    protected ResponseEntity<String> handleOne(HttpServletRequest request) {
        ResponseEntity<String> result;
        try {
            String body = readBody(request);
            if (body != null && !body.isBlank()) {
                result = updateOne(request, body);
            } else {
                result = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            result = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return result;
    }

    private ResponseEntity<String> updateOne(HttpServletRequest request, String body) {
        ResponseEntity<String> result;
        ObjectNode bodyJson = jsonUtils.toObjectNode(body);

        if (bodyJson != null && !bodyJson.isNull()) {
            Map<String, String> ids = getUrlIds(request);
            String key = controllerData.buildKey(ids, controllerConfig.getIdParams());

            if (controllerData.containsKey(controllerConfig.getUri(), key)) {
                ids.forEach((id, value) -> jsonUtils.putString(bodyJson, id, value));

                controllerData.putData(controllerConfig.getUri(), key, bodyJson);
                result = new ResponseEntity<>(bodyJson.toString(), HttpStatus.OK);
            } else {
                ObjectNode error = jsonUtils.createJson();
                jsonUtils.putString(error, DESCRIPTION_PARAM, String.format(KEY_NOT_FOUND, key));
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
