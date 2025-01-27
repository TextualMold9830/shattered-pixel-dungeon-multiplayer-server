package ru.nikita22007.synchronization.processors.server;

import ru.nikita22007.synchronization.annotations.SynchronizedSetter;
import spoon.processing.AbstractAnnotationProcessor;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;

import java.util.Objects;

public class processSynchronizedSetters extends AbstractAnnotationProcessor<SynchronizedSetter, CtMethod> {
    @Override
    public void init() {
        super.init();
        clearConsumedAnnotationTypes();
    }
    @Override
    public void process(SynchronizedSetter annotation, CtMethod element) {
        if (annotation.customSynchronization()) {
            return;
        }
        if (element.getParameters().size() != 1) {
            return;
        }
        String fieldName = annotation.field();
        CtParameter parameter = (CtParameter) element.getParameters().get(0);
        CtStatement checkStatement = getFactory().createCodeSnippetStatement("if (Objects.equals(this." + fieldName + ", " + parameter.getSimpleName() + ")) { return;}");
        CtBlock methodBody = element.getBody();
        methodBody.insertBegin(checkStatement);
        CtStatement sendStatement = getFactory().createCodeSnippetStatement("ru.nikita22007.synchronization.network.NetworkIOInstance.INSTANCE.SendObject(this);");
        methodBody.insertEnd(sendStatement);
    }

}
