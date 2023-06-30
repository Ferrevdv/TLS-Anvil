/**
 * TLS-Test-Framework - A framework for modeling TLS tests
 *
 * <p>Copyright 2022 Ruhr University Bochum
 *
 * <p>Licensed under Apache License 2.0 http://www.apache.org/licenses/LICENSE-2.0
 */
package de.rub.nds.tlstest.framework.model.derivationParameter;

import de.rub.nds.tlsattacker.core.config.Config;
import de.rub.nds.tlsattacker.core.constants.CipherSuite;
import de.rub.nds.tlstest.framework.TestContext;
import de.rub.nds.tlstest.framework.constants.TestEndpointType;
import de.rub.nds.tlstest.framework.model.LegacyDerivationScope;
import de.rub.nds.tlstest.framework.model.TlsParameterType;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class CipherSuiteDerivation extends DerivationParameter<CipherSuite> {

    public CipherSuiteDerivation() {
        super(TlsParameterType.CIPHER_SUITE, CipherSuite.class);
    }

    public CipherSuiteDerivation(CipherSuite selectedValue) {
        this();
        setSelectedValue(selectedValue);
    }

    @Override
    public void applyToConfig(Config config, TestContext context) {
        if (context.getConfig().getTestEndpointMode() == TestEndpointType.SERVER) {
            config.setDefaultClientSupportedCipherSuites(getSelectedValue());
        } else {
            config.setDefaultServerSupportedCipherSuites(getSelectedValue());
        }
        config.setDefaultSelectedCipherSuite(getSelectedValue());
    }

    @Override
    public List<DerivationParameter> getParameterValues(
            TestContext context, LegacyDerivationScope scope) {
        List<DerivationParameter> parameterValues = new LinkedList<>();
        Set<CipherSuite> cipherSuiteList = context.getFeatureExtractionResult().getCipherSuites();
        cipherSuiteList.addAll(
                context.getFeatureExtractionResult().getSupportedTls13CipherSuites());
        for (CipherSuite cipherSuite : cipherSuiteList) {
            if (scope.getKeyExchangeRequirements().compatibleWithCiphersuite(cipherSuite)) {
                parameterValues.add(new CipherSuiteDerivation(cipherSuite));
            }
        }

        return parameterValues;
    }
}
