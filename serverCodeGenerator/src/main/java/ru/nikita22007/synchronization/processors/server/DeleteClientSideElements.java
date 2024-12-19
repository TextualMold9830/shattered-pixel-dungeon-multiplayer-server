package ru.nikita22007.synchronization.processors.server;

import ru.nikita22007.synchronization.annotations.ClientSide;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtElement;

public class DeleteClientSideElements extends AbstractProcessor<CtElement> {
    @Override
    public void process(CtElement element) {
        if (element.hasAnnotation(ClientSide.class)) {
            element.delete();
        }
    }
}