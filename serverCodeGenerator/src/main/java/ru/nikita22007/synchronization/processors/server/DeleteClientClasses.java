package ru.nikita22007.synchronization.processors.server;

import ru.nikita22007.synchronization.annotations.ClientSide;
import ru.nikita22007.synchronization.annotations.ServerSide;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;

public class DeleteClientClasses extends AbstractProcessor<CtClass> {
    @Override
    public void process(CtClass element) {
        if (element.hasAnnotation(ClientSide.class)) {
            element.delete();
        }
    }
}