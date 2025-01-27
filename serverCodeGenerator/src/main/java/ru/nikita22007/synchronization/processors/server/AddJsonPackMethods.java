package ru.nikita22007.synchronization.processors.server;


import org.apache.log4j.LogManager;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.json.JSONObject;
import ru.nikita22007.synchronization.annotations.SynchronizationField;
import ru.nikita22007.synchronization.annotations.SynchronizedClass;
import ru.nikita22007.synchronization.annotations.SynchronizedSetter;
import ru.nikita22007.synchronization.network.JsonSerializable;
import ru.nikita22007.synchronization.util.StringUtils;
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
import java.util.Objects;
import java.util.Set;

public class AddJsonPackMethods extends AbstractAnnotationProcessor<SynchronizedClass, CtClass> {
    @Override
    public void init() {
        super.init();
        clearConsumedAnnotationTypes();
    }
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
            ctBlock.addStatement(SaveFieldToJsonStatement((CtField)field, jsonObjectCtVariable, element));
        }

        CtVariableAccess<JSONObject> returnVariable =  factory.Code().createVariableRead(jsonObjectCtVariable.getReference(), false);
        CtReturn<JSONObject> returnStatement = factory.createReturn();
        ctBlock.addStatement(returnStatement.setReturnedExpression(
                 returnVariable
        ));
        toJsonMethod.setBody(ctBlock);
        element.addMethod(toJsonMethod);
    }

    private CtStatement SaveFieldToJsonStatement(CtField field, CtLocalVariable<JSONObject> jsonObjectCtVariable, CtClass baseClass) {
        Factory factory = getFactory();
        CtTypeReference fieldType = field.getType();
        CtTypeReference<JSONObject> jsonObjectType = factory.createCtTypeReference(JSONObject.class);
        CtTypeReference<JsonSerializable> jsonSerializableType = factory.createCtTypeReference(JsonSerializable.class);
        CtType dataType = field.getType().getTypeDeclaration();
        CtTypeReference resultDataType = fieldType;
        CtMethod getter = findGetter(field,baseClass);
        CtExpression value;
        if (getter == null) {
            LogManager.getLogger(this.getClass()).warn("No getter for " + field.getSimpleName() + ". Using without getter" );
            //value = factory.createVariableRead(field.getReference(), false);//todo this line crashes code
            value = factory.createCodeSnippetExpression("this."+field.getSimpleName());
        } else {
            CtInvocation access = factory.createInvocation(factory.createThisAccess(), getter.getReference(), List.of());
            value = access;
        }
        if (dataType.isSubtypeOf(jsonSerializableType)) {
            CtMethod<JSONObject> getObjectMethod = dataType.getMethod(jsonObjectType, "toJsonObject");
            value = factory.createInvocation(value,
                    getObjectMethod.getReference(),
                    List.of()
            );
            resultDataType = jsonObjectType;
        }
        CtVariableAccess<JSONObject> jsonObjectVariableReadStatment = factory.Code().createVariableRead(jsonObjectCtVariable.getReference(), false);
        CtMethod<JSONObject> method = jsonObjectType.getTypeDeclaration().getMethod(jsonObjectType, "put", factory.createCtTypeReference(String.class), factory.createCtTypeReference(Object.class));
        CtExecutableReference<JSONObject> putMethodReference = method.getReference( );
        CtStatement writedata = factory.createInvocation(
                jsonObjectVariableReadStatment,
                putMethodReference,
                factory.createCodeSnippetExpression("\""+field.getSimpleName() + "\""),
                value
        );
        return writedata;
    }


        private CtMethod findGetter(CtField field, CtClass clazz){
        SynchronizationField fieldAnnotation = field.getAnnotation(SynchronizationField.class);
        String getterName = fieldAnnotation.setterName();
        if (Objects.equals(getterName, "")) {
            getterName = "get"+ StringUtils.capitalize(field.getSimpleName());
            CtMethod method =  clazz.getMethod(field.getType(), getterName);
            if (method == null) {
                getterName = "is" + StringUtils.capitalize(field.getSimpleName());
                return clazz.getMethod(field.getType(), getterName);
            }
        }
        return clazz.getMethod(field.getType(), getterName);
    }
}


