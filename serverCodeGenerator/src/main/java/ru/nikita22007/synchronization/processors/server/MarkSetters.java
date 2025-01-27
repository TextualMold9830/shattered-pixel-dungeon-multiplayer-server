package ru.nikita22007.synchronization.processors.server;

import ru.nikita22007.synchronization.annotations.SynchronizationField;
import ru.nikita22007.synchronization.annotations.SynchronizedSetter;
import ru.nikita22007.synchronization.util.StringUtils;
import spoon.processing.AbstractAnnotationProcessor;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;

import java.util.List;
import java.util.Objects;

public class MarkSetters extends AbstractAnnotationProcessor<SynchronizationField, CtField<?>> {
    @Override
    public void init() {
        super.init();
        clearConsumedAnnotationTypes();
    }
    @Override
    public void process(SynchronizationField annotation, CtField<?> element) {
        markSetters(element, (CtClass) element.getParent());
    }

    private void markSetters(CtField field, CtClass clazz){
        SynchronizationField fieldAnnotation = field.getAnnotation(SynchronizationField.class);
        String setterName = fieldAnnotation.getterName();
        if (Objects.equals(setterName, "")) {
            setterName = "set"+ StringUtils.capitalize(field.getSimpleName());
        }
        Factory factory = getFactory();
        List<CtMethod> methods =  clazz.getMethodsByName(setterName);
        for (CtMethod method : methods) {
            CtAnnotation<SynchronizedSetter> annotation =  factory.createAnnotation(factory.createCtTypeReference(SynchronizedSetter.class));
            annotation.addValue("field", field.getSimpleName());
            method.addAnnotation(annotation);
        }
    }
}
