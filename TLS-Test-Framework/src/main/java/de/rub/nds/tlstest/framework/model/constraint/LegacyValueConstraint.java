/**
 * TLS-Test-Framework - A framework for modeling TLS tests
 *
 * <p>Copyright 2022 Ruhr University Bochum
 *
 * <p>Licensed under Apache License 2.0 http://www.apache.org/licenses/LICENSE-2.0
 */
package de.rub.nds.tlstest.framework.model.constraint;

import de.rub.nds.tlstest.framework.model.TlsParameterType;

public class LegacyValueConstraint {

    private final TlsParameterType affectedType;

    private final String evaluationMethod;

    private final Class<?> clazz;

    private final boolean dynamic;

    public LegacyValueConstraint(
            TlsParameterType affectedType,
            String evaluationMethod,
            Class<?> clazz,
            boolean dynamic) {
        this.affectedType = affectedType;
        this.evaluationMethod = evaluationMethod;
        this.clazz = clazz;
        this.dynamic = dynamic;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public TlsParameterType getAffectedType() {
        return affectedType;
    }

    public String getEvaluationMethod() {
        return evaluationMethod;
    }

    public boolean isDynamic() {
        return dynamic;
    }
}
