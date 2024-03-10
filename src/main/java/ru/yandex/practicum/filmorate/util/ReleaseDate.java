package ru.yandex.practicum.filmorate.util;

import javax.validation.Constraint;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Payload;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
@Constraint(validatedBy = ReleaseDateValidator.class)
@Target({ TYPE, FIELD, ANNOTATION_TYPE })
@Retention(RUNTIME)
public @interface ReleaseDate {
    String message() default
            "{ru.yandex.practicum.filmorate.util.ReleaseDate.message}";
    Class <?> [] groups() default {};
    Class <? extends Payload> [] payload() default {};
}
