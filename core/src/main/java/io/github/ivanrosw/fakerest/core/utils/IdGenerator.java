package io.github.ivanrosw.fakerest.core.utils;

import io.github.ivanrosw.fakerest.core.model.GeneratorPattern;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class IdGenerator {

    private AtomicInteger atomicInteger;

    public IdGenerator() {
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
