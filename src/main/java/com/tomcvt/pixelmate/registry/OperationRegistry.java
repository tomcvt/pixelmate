package com.tomcvt.pixelmate.registry;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.tomcvt.pixelmate.model.operations.EdgeDetectionOperation;
import com.tomcvt.pixelmate.model.operations.ThickenEdgesOperation;

@Service
public class OperationRegistry {
    private final Map<String, Class<?>> operations = new HashMap<>();
    public OperationRegistry() {
        registerOperation(EdgeDetectionOperation.NAME, EdgeDetectionOperation.class);
        registerOperation(ThickenEdgesOperation.NAME, ThickenEdgesOperation.class);
    }

    private void registerOperation(String name, Class<?> operationClass) {
        operations.put(name, operationClass);
    }

    public Map<String, Class<?>> getOperations() {
        return operations;
    }

    public Class<?> getOperationByName(String name) {
        return operations.get(name);
    }
}
