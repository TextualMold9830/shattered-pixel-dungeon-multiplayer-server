package ru.nikita22007.synchronization.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

@Target({ElementType.FIELD})

@Retention(RetentionPolicy.SOURCE)
public @interface SynchronizationField {
    public String name() default "";
    public String setterName() default "";
    public String getterName() default "";
}
