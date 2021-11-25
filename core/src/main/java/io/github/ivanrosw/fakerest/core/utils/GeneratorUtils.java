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
            result = generateNumber();
        }
        return result;
    }

    private String generateUUID() {
        return UUID.randomUUID().toString();
    }

    private String generateNumber() {
        return String.valueOf(atomicInteger.incrementAndGet());
    }
}
