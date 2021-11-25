package io.github.ivanrosw.fakerest.core.model;

public enum ControllerMode {
    COLLECTION,                 // Tech mode for identify _ALL or _ONE
    COLLECTION_ALL,             // Needs for work with all collection
    COLLECTION_ONE,             // Needs for work with collection by keys
    STATIC                      // No work with collection. Only bound response
}
