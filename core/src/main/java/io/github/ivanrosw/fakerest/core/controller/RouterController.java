package io.github.ivanrosw.fakerest.core.controller;

import io.github.ivanrosw.fakerest.core.model.RouterConfig;
import io.github.ivanrosw.fakerest.core.utils.HttpUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;

@Slf4j
@AllArgsConstructor
public class RouterController {

    private RouterConfig conf;
    private HttpUtils httpUtils;
    private RestTemplate restTemplate;

    public ResponseEntity<String> handle(HttpServletRequest request) {
        ResponseEntity<String> result;
        try {
            HttpMethod method = HttpMethod.resolve(conf.getMethod().name());
            if (method != null) {
                RequestEntity<String> requestEntity = RequestEntity
                        .method(method, buildUri(request))
                        .headers(createHeaders(request))
                        .body(httpUtils.readBody(request));
                result = restTemplate.exchange(requestEntity, String.class);

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

    private HttpHeaders createHeaders(HttpServletRequest request) {
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
