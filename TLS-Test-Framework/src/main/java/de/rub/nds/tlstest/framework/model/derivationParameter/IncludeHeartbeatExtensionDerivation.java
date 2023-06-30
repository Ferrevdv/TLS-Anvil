/**
 * TLS-Test-Framework - A framework for modeling TLS tests
 *
 * <p>Copyright 2022 Ruhr University Bochum
 *
 * <p>Licensed under Apache License 2.0 http://www.apache.org/licenses/LICENSE-2.0
 */
package de.rub.nds.tlstest.framework.model.derivationParameter;

import de.rub.nds.tlsattacker.core.config.Config;
import de.rub.nds.tlsattacker.core.constants.HeartbeatMode;
import de.rub.nds.tlstest.framework.TestContext;
import de.rub.nds.tlstest.framework.model.LegacyDerivationScope;
import de.rub.nds.tlstest.framework.model.TlsParameterType;
import java.util.LinkedList;
import java.util.List;

public class IncludeHeartbeatExtensionDerivation extends DerivationParameter<Boolean> {

    public IncludeHeartbeatExtensionDerivation() {
        super(TlsParameterType.INCLUDE_HEARTBEAT_EXTENSION, Boolean.class);
    }

    public IncludeHeartbeatExtensionDerivation(Boolean selectedValue) {
        this();
        setSelectedValue(selectedValue);
    }

    @Override
    public List<DerivationParameter> getParameterValues(
            TestContext context, LegacyDerivationScope scope) {
        List<DerivationParameter> parameterValues = new LinkedList<>();
        parameterValues.add(new IncludeHeartbeatExtensionDerivation(true));
        parameterValues.add(new IncludeHeartbeatExtensionDerivation(false));
        return parameterValues;
    }

    @Override
    public void applyToConfig(Config config, TestContext context) {
        config.setHeartbeatMode(HeartbeatMode.PEER_NOT_ALLOWED_TO_SEND);
        config.setAddHeartbeatExtension(getSelectedValue());
    }
}
