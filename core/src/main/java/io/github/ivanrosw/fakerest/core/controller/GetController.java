/*
 * Copyright (C) 2021 Ivan Rosinskii
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.ivanrosw.fakerest.core.controller;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.ivanrosw.fakerest.core.model.ControllerMode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Slf4j
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GetController extends FakeController {

    private static final String LOG_INFO = "Got request \r\nMethod: [{}] \r\nUri: [{}]";

    @Override
    public ResponseEntity<String> handle(HttpServletRequest request) {
        if (log.isTraceEnabled()) log.trace(LOG_INFO, request.getMethod(), request.getRequestURI());

        ResponseEntity<String> result;
        if (mode == ControllerMode.COLLECTION_ALL) {
            result = handleAll();
        } else if (mode == ControllerMode.COLLECTION_ONE) {
            result = handleId(request);
        } else {
            result = handleNoId();
        }
        return result;
    }

    private ResponseEntity<String> handleAll() {
        ResponseEntity<String> result;
        Map<String, ObjectNode> allData = controllerData.getAllData(controllerConfig.getUri());
        if (allData.size() > 0) {
            ArrayNode array = jsonUtils.createArray();
            allData.forEach((key, data) -> array.add(data));
            result = new ResponseEntity<>(array.toString(), HttpStatus.OK);
        } else {
            result = new ResponseEntity<>(jsonUtils.createArray().toString(), HttpStatus.OK);
        }
        return result;
    }

    private ResponseEntity<String> handleId(HttpServletRequest request) {
        ResponseEntity<String> result;

        Map<String, String> urlIds = httpUtils.getUrlIds(request);
        String key = controllerData.buildKey(urlIds, controllerConfig.getIdParams());

        if (controllerData.containsKey(controllerConfig.getUri(), key)) {
            ObjectNode data = controllerData.getData(controllerConfig.getUri(), key);
            result = new ResponseEntity<>(data.toString(), HttpStatus.OK);
        } else {
            ObjectNode error = jsonUtils.createJson();
            jsonUtils.putString(error, DESCRIPTION_PARAM, String.format(KEY_NOT_FOUND, key));
            result = new ResponseEntity<>(error.toString(), HttpStatus.NOT_FOUND);
        }
        return result;
    }


    private ResponseEntity<String> handleNoId() {
        return new ResponseEntity<>(controllerConfig.getAnswer(), HttpStatus.OK);
    }

}
