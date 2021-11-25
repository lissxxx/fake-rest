package io.github.ivanrosw.fakerest.core.utils;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class UrlUtils {

    private static final String URL_ID_PATTERN = "(?<=\\{)[\\w]*(?=\\})";

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
}
