/**
 * TLS-Test-Framework - A framework for modeling TLS tests
 *
 * <p>Copyright 2022 Ruhr University Bochum
 *
 * <p>Licensed under Apache License 2.0 http://www.apache.org/licenses/LICENSE-2.0
 */
package de.rub.nds.tlstest.framework.annotations;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.rub.nds.anvilcore.coffee4j.model.ModelFromScope;
import de.rub.nds.tlstest.framework.coffee4j.reporter.TlsReporter;
import de.rub.nds.tlstest.framework.coffee4j.reporter.TlsTestsuiteReporter;
import de.rwth.swc.coffee4j.engine.characterization.ben.Ben;
import de.rwth.swc.coffee4j.junit.provider.configuration.characterization.EnableFaultCharacterization;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@TestChooser
@EnableFaultCharacterization(Ben.class)
@ModelFromScope(modelType = "GENERIC")
@TlsReporter(TlsTestsuiteReporter.class)
public @interface TlsTest {
    @JsonProperty("Description")
    String description() default "";
}
