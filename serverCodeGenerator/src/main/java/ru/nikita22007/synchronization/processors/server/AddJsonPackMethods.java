package ru.nikita22007.synchronization.processors.server;


import org.json.JSONObject;
import ru.nikita22007.synchronization.annotations.SynchronizationField;
import ru.nikita22007.synchronization.annotations.SynchronizedClass;
import ru.nikita22007.synchronization.network.JsonSerializable;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.*;
import spoon.reflect.declaration.*;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtTypeReference;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class AddJsonPackMethods extends AbstractProcessor<CtClass> {
    @Override
    public void process(CtClass element) {
        if (!element.hasAnnotation(SynchronizedClass.class)) {
            return;
        }
        CtFieldReference[] annotatedFields = (CtFieldReference[])Stream.of(element.getAllFields()).filter((s)->((CtFieldReference) s).hasAnnotation(SynchronizationField.class)).toArray();
        //todo methods
        // CtFieldReference[] annotatedFields = (CtFieldReference[])Stream.of(element.getAllFields()).filter((s)->((CtFieldReference) s).hasAnnotation(SynchronizationField.class)).toArray();
        Factory factory = getFactory();
        CtTypeReference<JSONObject> jsonObjectType = factory.createCtTypeReference(JSONObject.class);
        //CtMethod createMethod(CtType<?> target, Set<ModifierKind> modifiers, CtTypeReference returnType, String name, List<CtParameter<?>> parameters, Set<CtTypeReference<? extends Throwable>> thrownTypes);
        CtMethod<JSONObject> toJsonMethod = factory.createMethod(
                element,
                Set.of(ModifierKind.PUBLIC),
                jsonObjectType,
                "toJson",
                List.of(), //params
                Set.of() //trown types
        );
        CtBlock ctBlock = factory.createBlock();
        CtLocalVariable<JSONObject> jsonObjectCtVariable = factory.createLocalVariable(
                jsonObjectType,
                "jsonObject",
                factory.createConstructorCall(jsonObjectType,factory.createCodeSnippetExpression())
        );
        ctBlock.addStatement(jsonObjectCtVariable);
        for (CtFieldReference field : annotatedFields) {
            ctBlock.addStatement(SaveFieldToJsonStatement(field, jsonObjectCtVariable));
        }

        toJsonMethod.setBody(ctBlock);
        element.addMethod(toJsonMethod);
    }
    private CtStatement SaveFieldToJsonStatement(CtFieldReference field, CtLocalVariable<JSONObject> jsonObjectCtVariable) {
        Factory factory = getFactory();
        CtTypeReference fieldType = field.getType();
        CtTypeReference<JSONObject> jsonObjectType = factory.createCtTypeReference(JSONObject.class);
        CtTypeReference<JsonSerializable> jsonSerializableType = factory.createCtTypeReference(JsonSerializable.class);
        CtType dataType = field.getType().getTypeDeclaration();
        CtExpression value;
        CtTypeReference resultDataType = fieldType;
        if (dataType.isSubtypeOf(factory.createCtTypeReference(JsonSerializable.class))) {
            CtMethod<JSONObject> getObjectMethod =  dataType.getMethod(jsonObjectType, "toJsonObject");
            CtVariableAccess<JSONObject> access =  factory.Code().createVariableRead(field, field.isStatic());
            value = factory.createInvocation( access,
                    getObjectMethod.getReference(),
                    List.of()
            );
            resultDataType = jsonObjectType;
        } else {
            value = factory.Code().createVariableRead(field, field.isStatic());
        }
        CtVariableAccess<JSONObject> jsonObjectVariableReadStatment = factory.Code().createVariableRead(jsonObjectCtVariable.getReference(), false);
        CtExecutableReference<JSONObject> putmethod = jsonObjectType.getDeclaration().getMethod(jsonObjectType, "put", resultDataType).getReference();
        CtStatement writedata = factory.createInvocation(
                 jsonObjectVariableReadStatment,
                putmethod,
                value
                );
        return writedata;
    }
}


