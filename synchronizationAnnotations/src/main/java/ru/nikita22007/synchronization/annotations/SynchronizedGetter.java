package ru.nikita22007.synchronization.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})

@Retention(RetentionPolicy.SOURCE)
public @interface SynchronizedGetter {
    boolean customSynchronization() default false;
}
