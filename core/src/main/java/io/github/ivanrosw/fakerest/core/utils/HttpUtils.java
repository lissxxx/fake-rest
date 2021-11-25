package io.github.ivanrosw.fakerest.core.utils;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
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
}
