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
package io.github.ivanrosw.fakerest.core.utils;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class HttpUtils {

    private static final String URL_ID_PATTERN = "(?<=\\{)[\\w]*(?=\\})";

    public Map<String, String> getUrlIds(HttpServletRequest request) {
        return (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
    }

    public List<String> getIdParams(String url) {
        Pattern pattern = Pattern.compile(URL_ID_PATTERN);
        Matcher matcher = pattern.matcher(url);

        List<String> idParams = new ArrayList<>();
        while (matcher.find()) {
            idParams.add(matcher.group());
        }

        return idParams;
    }

    public String getBaseUri(String uri) {
        return uri.substring(0, uri.indexOf("{"));
    }

    public String readBody(HttpServletRequest request) throws IOException {
        return request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
    }

    public HttpHeaders readHeaders(HttpServletRequest request) {
        HttpHeaders httpHeaders = new HttpHeaders();

        Enumeration<String> headersNames = request.getHeaderNames();
        if (headersNames != null) {
            while (headersNames.hasMoreElements()) {
                String headerName = headersNames.nextElement();

                Enumeration<String> headerValues = request.getHeaders(headerName);
                while (headerValues.hasMoreElements()) {
                    String headerValue = headerValues.nextElement();
                    httpHeaders.add(headerName, headerValue);
                }
            }
        }

        return httpHeaders;
    }
}
