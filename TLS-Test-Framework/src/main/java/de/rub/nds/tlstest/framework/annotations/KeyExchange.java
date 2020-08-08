package de.rub.nds.tlstest.framework.annotations;

import de.rub.nds.tlstest.framework.constants.KeyExchangeType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.TYPE})
public @interface KeyExchange {
    KeyExchangeType[] supported();
    boolean mergeSupportedWithClassSupported() default false;
    boolean requiresServerKeyExchMsg() default false;
}
