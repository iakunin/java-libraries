package dev.iakunin.library.tests.parametrized;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.params.provider.ArgumentsSource;

@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD, })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ArgumentsSource(JsonFileSourceProvider.class)
public @interface JsonFileSource {
    String file() default "";

    String expectFile() default "";
}
