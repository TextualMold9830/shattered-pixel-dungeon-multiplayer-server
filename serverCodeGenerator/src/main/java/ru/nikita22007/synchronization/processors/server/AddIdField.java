package ru.nikita22007.synchronization.processors.server;

import ru.nikita22007.synchronization.annotations.UniqueId;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;

import java.lang.annotation.Annotation;
import java.util.*;

public class AddIdField extends AbstractProcessor<CtClass> {
    @Override
    public void process(CtClass element) {
        Factory factory = getFactory();
        if (element.hasAnnotation(UniqueId.class)){
            UniqueId annotation = element.getAnnotation(UniqueId.class);
            String idName = annotation.idFieldName();
            if (element.getField(idName) != null) {
                return;
            }
            element.addField(factory.createField(
                    element,
                    new HashSet<>(Collections.singletonList(ModifierKind.PRIVATE)),
                    factory.createCtTypeReference(long.class),
                    annotation.idFieldName()
            ));

        }
    }
}
