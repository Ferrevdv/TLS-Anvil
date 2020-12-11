package de.rub.nds.tlstest.suite.tests.both.lengthField.extensions;

import de.rub.nds.tlsattacker.core.config.Config;
import de.rub.nds.tlsattacker.core.constants.ProtocolVersion;
import de.rub.nds.tlsattacker.core.protocol.message.extension.HeartbeatExtensionMessage;
import de.rub.nds.tlsattacker.core.protocol.message.extension.PaddingExtensionMessage;
import de.rub.nds.tlstest.framework.annotations.KeyExchange;
import de.rub.nds.tlstest.framework.annotations.ScopeLimitations;
import de.rub.nds.tlstest.framework.annotations.ServerTest;
import de.rub.nds.tlstest.framework.annotations.TlsTest;
import de.rub.nds.tlstest.framework.annotations.TlsVersion;
import de.rub.nds.tlstest.framework.coffee4j.model.ModelFromScope;
import de.rub.nds.tlstest.framework.constants.KeyExchangeType;
import de.rub.nds.tlstest.framework.execution.WorkflowRunner;
import de.rub.nds.tlstest.framework.model.DerivationType;
import de.rub.nds.tlstest.framework.model.ModelType;
import de.rub.nds.tlstest.framework.testClasses.TlsGenericTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;

@ServerTest
public class PaddingExtension extends TlsGenericTest {
    
    @Tag("tls12")
    @TlsVersion(supported = ProtocolVersion.TLS12)
    @KeyExchange(supported = KeyExchangeType.ALL12)
    @TlsTest(description = "Send a Padding Extension in the Hello Message with a modified length value")
    @ScopeLimitations(DerivationType.INCLUDE_PADDING_EXTENSION)
    @ModelFromScope(baseModel = ModelType.LENGTHFIELD)
    public void paddingExtensionLengthTLS12(ArgumentsAccessor argumentAccessor, WorkflowRunner runner) {
        Config config = context.getConfig().createConfig();
        config.setAddPaddingExtension(true);
        genericExtensionLengthTest(runner, argumentAccessor, config, PaddingExtensionMessage.class);
    }
    
    @Tag("tls13")
    @TlsVersion(supported = ProtocolVersion.TLS13)
    @KeyExchange(supported = KeyExchangeType.ALL13)
    @TlsTest(description = "Send a Padding Extension in the Hello Message with a modified length value")
    @ScopeLimitations(DerivationType.INCLUDE_PADDING_EXTENSION)
    @ModelFromScope(baseModel = ModelType.LENGTHFIELD)
    public void paddingExtensionLengthTLS13(ArgumentsAccessor argumentAccessor, WorkflowRunner runner) {
        Config config = context.getConfig().createTls13Config();
        config.setAddPaddingExtension(true);
        genericExtensionLengthTest(runner, argumentAccessor, config, PaddingExtensionMessage.class);
    }
}
