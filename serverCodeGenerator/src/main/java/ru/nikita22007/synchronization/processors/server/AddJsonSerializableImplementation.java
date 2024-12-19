package ru.nikita22007.synchronization.processors.server;

import org.json.JSONObject;
import ru.nikita22007.synchronization.annotations.SynchronizationField;
import ru.nikita22007.synchronization.annotations.SynchronizedClass;
import ru.nikita22007.synchronization.network.JsonSerializable;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class AddJsonSerializableImplementation extends AbstractProcessor<CtClass> {
    public void process(CtClass element) {
        if (!element.hasAnnotation(SynchronizedClass.class)) {
            return;
        }
        element.addSuperInterface(getFactory().createCtTypeReference(JsonSerializable.class));
    }
}
