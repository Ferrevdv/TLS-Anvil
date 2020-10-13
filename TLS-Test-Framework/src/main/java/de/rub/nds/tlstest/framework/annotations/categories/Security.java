package de.rub.nds.tlstest.framework.annotations.categories;

import de.rub.nds.tlstest.framework.constants.SeverityLevel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Security {
    SeverityLevel value();
}
