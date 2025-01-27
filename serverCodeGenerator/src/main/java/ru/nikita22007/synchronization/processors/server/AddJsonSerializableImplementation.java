package ru.nikita22007.synchronization.processors.server;

import ru.nikita22007.synchronization.annotations.SynchronizedClass;
import ru.nikita22007.synchronization.network.JsonSerializable;
import spoon.processing.AbstractAnnotationProcessor;
import spoon.reflect.declaration.CtClass;

public class AddJsonSerializableImplementation extends AbstractAnnotationProcessor<SynchronizedClass, CtClass> {
    @Override
    public void init() {
        super.init();
        clearConsumedAnnotationTypes();
    }
    @Override
    public void process(SynchronizedClass annotation, CtClass element) {
        element.addSuperInterface(getFactory().createCtTypeReference(JsonSerializable.class));
    }
}
