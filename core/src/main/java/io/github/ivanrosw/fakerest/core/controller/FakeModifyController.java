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

import io.github.ivanrosw.fakerest.core.model.ControllerMode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public abstract class FakeModifyController extends FakeController {

    private static final String LOG_INFO = "Got request \r\nMethod: [{}] \r\nUri: [{}] \r\nBody: [{}]";

    @Override
    public final ResponseEntity<String> handle(HttpServletRequest request) {
        delay();

        ResponseEntity<String> result = null;
        String body = null;
        try {
            body = httpUtils.readBody(request);
        } catch (Exception e) {
            result = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (log.isTraceEnabled()) log.trace(LOG_INFO, request.getMethod(), request.getRequestURI(), body);

        if (result == null) {
            result = processRequest(request, body);
        }
        return result;
    }
    
    private ResponseEntity<String> processRequest(HttpServletRequest request, String body) {
        ResponseEntity<String> result;
        if (mode == ControllerMode.COLLECTION_ONE) {
            result = handleOne(request, body);
        } else {
            result = returnAnswerOrBody(body);
        }
        return result;
    }

    protected ResponseEntity<String> returnAnswerOrBody(String body) {
        ResponseEntity<String> result;
        if (controllerConfig.getAnswer() != null) {
            result = new ResponseEntity<>(controllerConfig.getAnswer(), HttpStatus.OK);
        }else if (body != null && !body.isEmpty()) {
            result = new ResponseEntity<>(body, HttpStatus.OK);
        } else {
            result = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return result;
    }

    protected abstract ResponseEntity<String> handleOne(HttpServletRequest request, String body);
}
