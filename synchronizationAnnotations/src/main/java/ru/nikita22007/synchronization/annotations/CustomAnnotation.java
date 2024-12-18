package ru.nikita22007.synchronization.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface CustomAnnotation {

    String className();
    String value() default "Hello";
    int type() default 0;
}