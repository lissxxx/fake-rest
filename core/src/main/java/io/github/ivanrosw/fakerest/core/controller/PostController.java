package io.github.ivanrosw.fakerest.core.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.ivanrosw.fakerest.core.model.GeneratorPattern;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PostController extends FakeModifyController {

    @Override
    protected ResponseEntity<String> handleOne(HttpServletRequest request, String body) {
        ResponseEntity<String> result;
        if (body != null && !body.isEmpty()) {
            result = saveOne(body);
        } else {
            result = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return result;
    }

    private ResponseEntity<String> saveOne(String body) {
        ResponseEntity<String> result;
        ObjectNode bodyJson = jsonUtils.toObjectNode(body);

        if (bodyJson != null && !bodyJson.isNull()) {
            if (controllerConfig.isGenerateId()) {
                addId(bodyJson);
            }
            String key = controllerData.buildKey(bodyJson, controllerConfig.getIdParams());

            if (!controllerData.containsKey(controllerConfig.getUri(), key)) {
                controllerData.putData(controllerConfig.getUri(), key, bodyJson);
                result = new ResponseEntity<>(bodyJson.toString(), HttpStatus.OK);
            } else {
                ObjectNode error = jsonUtils.createJson();
                jsonUtils.putString(error, DESCRIPTION_PARAM, String.format(KEY_ALREADY_EXIST, key));
                result = new ResponseEntity<>(error.toString(), HttpStatus.BAD_REQUEST);
            }
        } else {
            ObjectNode error = jsonUtils.createJson();
            jsonUtils.putString(error, DESCRIPTION_PARAM, String.format(DATA_NOT_JSON, body));
            result = new ResponseEntity<>(error.toString(), HttpStatus.BAD_REQUEST);
        }
        return result;
    }

    private void addId(ObjectNode data) {
        Map<String, GeneratorPattern> generatorPatterns = controllerConfig.getGenerateIdPatterns();
        controllerConfig.getIdParams().forEach(idParam -> {
            GeneratorPattern pattern = generatorPatterns == null ? null : generatorPatterns.get(idParam);
            jsonUtils.putString(data, idParam, idGenerator.generateId(pattern));
        });
    }

}
