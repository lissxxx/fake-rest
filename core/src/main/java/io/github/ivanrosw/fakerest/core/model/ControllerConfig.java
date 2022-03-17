package io.github.ivanrosw.fakerest.core.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class ControllerConfig extends BaseUriConfig implements Copyable<ControllerConfig> {

    private String answer;

    private long delayMs;

    private List<String> idParams;

    private boolean generateId;

    private Map<String, GeneratorPattern> generateIdPatterns;

    public ControllerConfig() {
        idParams = new ArrayList<>();
        generateIdPatterns = new HashMap<>();
    }

    @Override
    public ControllerConfig copy() {
        ControllerConfig copy = new ControllerConfig();
        copy.setId(this.getId());
        copy.setUri(this.getUri());
        copy.setMethod(this.getMethod());
        copy.setAnswer(this.answer);
        copy.setDelayMs(this.delayMs);
        copy.setIdParams(new ArrayList<>(this.idParams));
        copy.setGenerateId(this.generateId);
        copy.setGenerateIdPatterns(new HashMap<>(this.generateIdPatterns));
        return copy;
    }
}
