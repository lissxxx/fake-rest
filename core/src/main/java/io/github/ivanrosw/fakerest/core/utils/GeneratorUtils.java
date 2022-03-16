/*
 * Copyright (C) 2022 Ivan Rosinskii
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

import io.github.ivanrosw.fakerest.core.model.GeneratorPattern;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class GeneratorUtils {

    private AtomicInteger atomicInteger;

    public GeneratorUtils() {
        atomicInteger = new AtomicInteger();
    }

    public String generateId(GeneratorPattern pattern) {
        String result;
        if (pattern == GeneratorPattern.UUID) {
            result = generateUUID();
        } else {
            result = generateSequence();
        }
        return result;
    }

    private String generateUUID() {
        return UUID.randomUUID().toString();
    }

    private String generateSequence() {
        return String.valueOf(atomicInteger.incrementAndGet());
    }
}
