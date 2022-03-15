/*
 * Copyright (C) 2022 Ivan Rosinskii
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

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DeleteController extends FakeModifyController {

    @Override
    protected ResponseEntity<String> handleOne(HttpServletRequest request, String body) {
        ResponseEntity<String> result;

        String key = controllerData.buildKey(httpUtils.getUrlIds(request), controllerConfig.getIdParams());
        if (controllerData.containsKey(controllerConfig.getUri(), key)) {
            ObjectNode data = controllerData.getData(controllerConfig.getUri(), key);
            controllerData.deleteData(controllerConfig.getUri(), key);

            result = new ResponseEntity<>(data.toString(), HttpStatus.OK);
        } else {
            ObjectNode error = jsonUtils.createJson();
            jsonUtils.putString(error, DESCRIPTION_PARAM, String.format(KEY_NOT_FOUND, key));
            result = new ResponseEntity<>(error.toString(), HttpStatus.BAD_REQUEST);
        }

        return result;
    }
}
