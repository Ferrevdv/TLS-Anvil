/**
 * TLS-Test-Framework - A framework for modeling TLS tests
 *
 * <p>Copyright 2022 Ruhr University Bochum
 *
 * <p>Licensed under Apache License 2.0 http://www.apache.org/licenses/LICENSE-2.0
 */
package de.rub.nds.tlstest.framework.model.derivationParameter;

import de.rub.nds.tlsattacker.core.config.Config;
import de.rub.nds.tlsattacker.core.constants.ExtensionType;
import de.rub.nds.tlstest.framework.TestContext;
import de.rub.nds.tlstest.framework.model.LegacyDerivationScope;
import de.rub.nds.tlstest.framework.model.TlsParameterType;
import java.util.LinkedList;
import java.util.List;

public class GreaseExtensionDerivation extends DerivationParameter<ExtensionType> {

    public GreaseExtensionDerivation() {
        super(TlsParameterType.GREASE_EXTENSION, ExtensionType.class);
    }

    public GreaseExtensionDerivation(ExtensionType selectedValue) {
        this();
        setSelectedValue(selectedValue);
    }

    @Override
    public List<DerivationParameter> getParameterValues(
            TestContext context, LegacyDerivationScope scope) {
        List<DerivationParameter> parameterValues = new LinkedList<>();
        for (ExtensionType extType : ExtensionType.values()) {
            if (extType.isGrease()) {
                parameterValues.add(new GreaseExtensionDerivation(extType));
            }
        }
        return parameterValues;
    }

    @Override
    public void applyToConfig(Config config, TestContext context) {}
}
