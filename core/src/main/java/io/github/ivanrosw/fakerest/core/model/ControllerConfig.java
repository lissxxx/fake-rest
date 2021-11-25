package io.github.ivanrosw.fakerest.core.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class ControllerConfig {

    private String uri;

    private RequestMethod method;

    private String answer;

    private List<String> idParams;

    private boolean generateId;

    private Map<String, GeneratorPattern> generateIdPatterns;

    public ControllerConfig() {
        idParams = new ArrayList<>();
    }

    public List<String> getIdParams() {
        if (idParams.isEmpty()) {
            idParams.add("id");
        }

        return idParams;
    }
}
