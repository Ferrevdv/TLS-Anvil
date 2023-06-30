/**
 * TLS-Test-Framework - A framework for modeling TLS tests
 *
 * <p>Copyright 2022 Ruhr University Bochum
 *
 * <p>Licensed under Apache License 2.0 http://www.apache.org/licenses/LICENSE-2.0
 */
package de.rub.nds.tlstest.framework.model.derivationParameter;

import de.rub.nds.tlsattacker.core.config.Config;
import de.rub.nds.tlsattacker.core.constants.NamedGroup;
import de.rub.nds.tlstest.framework.TestContext;
import de.rub.nds.tlstest.framework.model.LegacyDerivationScope;
import de.rub.nds.tlstest.framework.model.TlsParameterType;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/** */
public class IncludeGreaseNamedGroupsDerivation extends DerivationParameter<Boolean> {

    public IncludeGreaseNamedGroupsDerivation() {
        super(TlsParameterType.INCLUDE_GREASE_NAMED_GROUPS, Boolean.class);
    }

    public IncludeGreaseNamedGroupsDerivation(Boolean selectedValue) {
        this();
        setSelectedValue(selectedValue);
    }

    @Override
    public List<DerivationParameter> getParameterValues(
            TestContext context, LegacyDerivationScope scope) {
        List<DerivationParameter> parameterValues = new LinkedList<>();
        parameterValues.add(new IncludeGreaseNamedGroupsDerivation(true));
        parameterValues.add(new IncludeGreaseNamedGroupsDerivation(false));
        return parameterValues;
    }

    @Override
    public void applyToConfig(Config config, TestContext context) {}

    @Override
    public void postProcessConfig(Config config, TestContext context) {
        if (getSelectedValue()) {
            Arrays.asList(NamedGroup.values()).stream()
                    .filter(group -> group.isGrease())
                    .forEach(greaseGroup -> config.getDefaultClientNamedGroups().add(greaseGroup));
        }
    }
}
