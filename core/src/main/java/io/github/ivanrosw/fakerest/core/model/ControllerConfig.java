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
