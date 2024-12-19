package ru.nikita22007.synchronization.processors.server;


import org.json.JSONObject;
import ru.nikita22007.synchronization.annotations.SynchronizationField;
import ru.nikita22007.synchronization.annotations.SynchronizedClass;
import ru.nikita22007.synchronization.network.JsonSerializable;
import spoon.processing.AbstractAnnotationProcessor;
import spoon.processing.AbstractProcessor;
import spoon.processing.AnnotationProcessor;
import spoon.reflect.code.*;
import spoon.reflect.declaration.*;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtTypeReference;

import java.sql.Statement;
import java.util.List;
import java.util.Set;

public class AddJsonPackMethods extends AbstractAnnotationProcessor<SynchronizedClass, CtClass> {
    @Override
    public void process(SynchronizedClass annotation, CtClass element) {
        Object[] annotatedFields =  (
                (element.getFields().stream().filter(
                (s) -> ((CtField)s).hasAnnotation(SynchronizationField.class)
        ).toArray()));
        //todo methods
        // CtFieldReference[] annotatedFields = (CtFieldReference[])Stream.of(element.getAllFields()).filter((s)->((CtFieldReference) s).hasAnnotation(SynchronizationField.class)).toArray();
        Factory factory = getFactory();
        CtTypeReference<JSONObject> jsonObjectType = factory.createCtTypeReference(JSONObject.class);
        //CtMethod createMethod(CtType<?> target, Set<ModifierKind> modifiers, CtTypeReference returnType, String name, List<CtParameter<?>> parameters, Set<CtTypeReference<? extends Throwable>> thrownTypes);
        CtMethod<JSONObject> toJsonMethod = factory.createMethod(
                element,
                Set.of(ModifierKind.PUBLIC),
                jsonObjectType,
                "toJsonObject",
                List.of(), //params
                Set.of() //trown types
        );
        CtBlock ctBlock = factory.createBlock();
        CtLocalVariable<JSONObject> jsonObjectCtVariable = factory.createLocalVariable(
                jsonObjectType,
                "jsonObject",
                factory.createConstructorCall(jsonObjectType, factory.createCodeSnippetExpression())
        );
        ctBlock.addStatement(jsonObjectCtVariable);
        for (Object field : annotatedFields) {
            ctBlock.addStatement(SaveFieldToJsonStatement((CtField)field, jsonObjectCtVariable));
        }

        CtVariableAccess<JSONObject> returnVariable =  factory.Code().createVariableRead(jsonObjectCtVariable.getReference(), false);
        CtReturn<JSONObject> returnStatement = factory.createReturn();
        ctBlock.addStatement(returnStatement.setReturnedExpression(
                 returnVariable
        ));
        toJsonMethod.setBody(ctBlock);
        element.addMethod(toJsonMethod);
    }

    private CtStatement SaveFieldToJsonStatement(CtField field, CtLocalVariable<JSONObject> jsonObjectCtVariable) {
        Factory factory = getFactory();
        CtTypeReference fieldType = field.getType();
        CtTypeReference<JSONObject> jsonObjectType = factory.createCtTypeReference(JSONObject.class);
        CtTypeReference<JsonSerializable> jsonSerializableType = factory.createCtTypeReference(JsonSerializable.class);
        CtType dataType = field.getType().getTypeDeclaration();
        CtExpression value;
        CtTypeReference resultDataType = fieldType;
        if (dataType.isSubtypeOf(factory.createCtTypeReference(JsonSerializable.class))) {
            CtMethod<JSONObject> getObjectMethod = dataType.getMethod(jsonObjectType, "toJsonObject");
            CtVariableAccess<JSONObject> access = factory.Code().createVariableRead(field.getReference(), field.isStatic());
            value = factory.createInvocation(access,
                    getObjectMethod.getReference(),
                    List.of()
            );
            resultDataType = jsonObjectType;
        } else {
            value = factory.Code().createVariableRead(field.getReference(), field.isStatic());
        }
        CtVariableAccess<JSONObject> jsonObjectVariableReadStatment = factory.Code().createVariableRead(jsonObjectCtVariable.getReference(), false);
        CtMethod<JSONObject> method = jsonObjectType.getTypeDeclaration().getMethod(jsonObjectType, "put", factory.createCtTypeReference(String.class), factory.createCtTypeReference(Object.class));
        CtExecutableReference<JSONObject> putmethodReference = method.getReference( );
        CtStatement writedata = factory.createInvocation(
                jsonObjectVariableReadStatment,
                putmethodReference,
                factory.createCodeSnippetExpression("\""+field.getSimpleName() + "\""),
                value
        );
        return writedata;
    }
}


