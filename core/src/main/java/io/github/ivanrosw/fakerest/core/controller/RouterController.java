/*
 * Copyright 2021 Ivan Rosinskii
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package io.github.ivanrosw.fakerest.core.controller;

import io.github.ivanrosw.fakerest.core.model.RouterConfig;
import io.github.ivanrosw.fakerest.core.utils.HttpUtils;
import io.github.ivanrosw.fakerest.core.utils.RestClient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;

@Slf4j
@AllArgsConstructor
public class RouterController {

    private RouterConfig conf;
    private HttpUtils httpUtils;
    private RestClient restClient;

    public ResponseEntity<String> handle(HttpServletRequest request) {
        ResponseEntity<String> result;
        try {
            HttpMethod method = HttpMethod.resolve(conf.getMethod().name());
            URI uri = buildUri(request);
            String body = httpUtils.readBody(request);
            HttpHeaders headers = httpUtils.readHeaders(request);

            if (method != null) {
                result = restClient.execute(method, uri, headers, body);

            } else {
                log.error("Cant convert method [{}] to httpMethod", conf.getMethod());
                result = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            log.error("Error while redirecting from [{}] to [{}]", conf.getUri(), conf.getToUrl(), e);
            result = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return result;
    }

    private URI buildUri(HttpServletRequest request) throws URISyntaxException {
        URI result;
        if (conf.getToUrl().contains("://")) {
            result = new URI(conf.getToUrl());
        } else {
            String url = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
            if (conf.getToUrl().charAt(0) == '/' || conf.getToUrl().charAt(0) == '\\') {
                url = url + conf.getToUrl();
            } else {
                url = url + "/" + conf.getToUrl();
            }

            result = new URI(url);
        }
        return result;
    }

}
