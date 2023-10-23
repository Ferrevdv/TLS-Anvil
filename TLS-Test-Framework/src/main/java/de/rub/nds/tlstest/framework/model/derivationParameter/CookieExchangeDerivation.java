/**
 * TLS-Test-Framework - A framework for modeling TLS tests
 *
 * <p>Copyright 2022 Ruhr University Bochum
 *
 * <p>Licensed under Apache License 2.0 http://www.apache.org/licenses/LICENSE-2.0
 */
package de.rub.nds.tlstest.framework.model.derivationParameter;

import de.rub.nds.tlsattacker.core.config.Config;
import de.rub.nds.tlstest.framework.TestContext;
import de.rub.nds.tlstest.framework.model.DerivationScope;
import de.rub.nds.tlstest.framework.model.DerivationType;
import java.util.LinkedList;
import java.util.List;

public class CookieExchangeDerivation extends DerivationParameter<Boolean> {

    public CookieExchangeDerivation() {
        super(DerivationType.COOKIE_EXCHANGE, Boolean.class);
    }

    public CookieExchangeDerivation(Boolean selectedValue) {
        this();
        setSelectedValue(selectedValue);
    }

    @Override
    public List<DerivationParameter> getParameterValues(
            TestContext context, DerivationScope scope) {
        List<DerivationParameter> parameterValues = new LinkedList<>();
        parameterValues.add(new CookieExchangeDerivation(true));
        parameterValues.add(new CookieExchangeDerivation(false));
        return parameterValues;
    }

    @Override
    public void applyToConfig(Config config, TestContext context) {
        config.setDtlsCookieExchange(getSelectedValue());
    }
}