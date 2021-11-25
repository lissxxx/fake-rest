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

package io.github.ivanrosw.fakerest.core.utils;

import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.net.URI;

@Component
public class RestClient {

    private RestTemplate restTemplate;

    @PostConstruct
    private void init() {
        restTemplate = new RestTemplate();
    }

    public ResponseEntity<String> execute(HttpMethod method, URI url, HttpHeaders headers, String body) {
        ResponseEntity<String> result;
        try {
            RequestEntity<String> requestEntity = RequestEntity
                    .method(method, url)
                    .headers(headers)
                    .body(body);
            result = restTemplate.exchange(requestEntity, String.class);
        } catch (HttpStatusCodeException e) {
            result = ResponseEntity.status(e.getRawStatusCode())
                    .headers(e.getResponseHeaders())
                    .body(e.getResponseBodyAsString());
        } catch (ResourceAccessException e) {
            result = ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).build();
        } catch (Exception e) {
            result = ResponseEntity.status(0).body(e.getMessage());
        }
        return result;
    }

}
