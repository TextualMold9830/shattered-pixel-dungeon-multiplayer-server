package ru.nikita22007.synchronization.processors.server;

import ru.nikita22007.synchronization.annotations.SynchronizedSetter;
import spoon.processing.AbstractAnnotationProcessor;
import spoon.reflect.declaration.CtMethod;

public class ProcessSynchtonizedSetters extends AbstractAnnotationProcessor<SynchronizedSetter, CtMethod> {

    @Override
    public void process(SynchronizedSetter annotation, CtMethod element) {
        Statment checkStatment =
        element.getBody().insertBefore(

        )
    }

}
