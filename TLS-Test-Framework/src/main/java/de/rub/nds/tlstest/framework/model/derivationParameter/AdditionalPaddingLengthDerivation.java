/**
 * TLS-Test-Framework - A framework for modeling TLS tests
 *
 * <p>Copyright 2022 Ruhr University Bochum
 *
 * <p>Licensed under Apache License 2.0 http://www.apache.org/licenses/LICENSE-2.0
 */
package de.rub.nds.tlstest.framework.model.derivationParameter;

import de.rub.nds.anvilcore.model.parameter.DerivationParameter;
import de.rub.nds.tlsattacker.core.config.Config;
import de.rub.nds.tlstest.framework.TestContext;
import de.rub.nds.tlstest.framework.anvil.TlsAnvilConfig;
import de.rub.nds.tlstest.framework.model.LegacyDerivationScope;
import de.rub.nds.tlstest.framework.model.TlsParameterType;
import java.util.LinkedList;
import java.util.List;

/** Defines values for the optiona TLS 1.3 padding lengths */
public class AdditionalPaddingLengthDerivation extends DerivationParameter<TlsAnvilConfig, Integer> {

    public AdditionalPaddingLengthDerivation() {
        super(TlsParameterType.ADDITIONAL_PADDING_LENGTH, Integer.class);
    }

    public AdditionalPaddingLengthDerivation(Integer selectedValue) {
        this();
        setSelectedValue(selectedValue);
    }

    @Override
    public List<DerivationParameter> getParameterValues(
            TestContext context, LegacyDerivationScope scope) {
        List<DerivationParameter> parameterValues = new LinkedList<>();
        parameterValues.add(new AdditionalPaddingLengthDerivation(5));
        parameterValues.add(new AdditionalPaddingLengthDerivation(100));
        parameterValues.add(new AdditionalPaddingLengthDerivation(1000));
        return parameterValues;
    }

    @Override
    public void applyToConfig(TlsAnvilConfig config, de.rub.nds.anvilcore.model.DerivationScope derivationScope) {
        config.setDefaultAdditionalPadding(getSelectedValue());
    }


    @Override
    public List<DerivationParameter<TlsAnvilConfig, Integer>> getParameterValues(de.rub.nds.anvilcore.model.DerivationScope derivationScope) {
        List<DerivationParameter> parameterValues = new LinkedList<>();
        parameterValues.add(new AdditionalPaddingLengthDerivation(5));
        parameterValues.add(new AdditionalPaddingLengthDerivation(100));
        parameterValues.add(new AdditionalPaddingLengthDerivation(1000));
        return parameterValues;
    }

    @Override
    protected DerivationParameter<TlsAnvilConfig, Integer> generateValue(Integer selectedValue) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
