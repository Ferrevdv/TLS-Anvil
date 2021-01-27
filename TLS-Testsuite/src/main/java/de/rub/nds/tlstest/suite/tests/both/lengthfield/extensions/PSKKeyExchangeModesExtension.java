package de.rub.nds.tlstest.suite.tests.both.lengthfield.extensions;

import de.rub.nds.modifiablevariable.util.Modifiable;
import de.rub.nds.tlsattacker.core.config.Config;
import de.rub.nds.tlsattacker.core.constants.ProtocolVersion;
import de.rub.nds.tlsattacker.core.protocol.message.extension.PSKKeyExchangeModesExtensionMessage;
import de.rub.nds.tlsattacker.core.workflow.WorkflowTrace;
import de.rub.nds.tlstest.framework.annotations.KeyExchange;
import de.rub.nds.tlstest.framework.annotations.MethodCondition;
import de.rub.nds.tlstest.framework.annotations.ScopeLimitations;
import de.rub.nds.tlstest.framework.annotations.ServerTest;
import de.rub.nds.tlstest.framework.annotations.TlsTest;
import de.rub.nds.tlstest.framework.annotations.TlsVersion;
import de.rub.nds.tlstest.framework.annotations.categories.HandshakeCategory;
import de.rub.nds.tlstest.framework.annotations.categories.MessageStructureCategory;
import de.rub.nds.tlstest.framework.coffee4j.model.ModelFromScope;
import de.rub.nds.tlstest.framework.constants.KeyExchangeType;
import de.rub.nds.tlstest.framework.constants.SeverityLevel;
import de.rub.nds.tlstest.framework.execution.WorkflowRunner;
import de.rub.nds.tlstest.framework.model.DerivationType;
import de.rub.nds.tlstest.framework.model.ModelType;
import de.rub.nds.tlstest.framework.testClasses.TlsGenericTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;

@Tag("tls13")
@ServerTest
@TlsVersion(supported = ProtocolVersion.TLS13)
@KeyExchange(supported = KeyExchangeType.ALL13)
public class PSKKeyExchangeModesExtension extends TlsGenericTest {
    
    @TlsTest(description = "Send a Pre Shared Key Exchange Modes Extension in the Hello Message with a modified length value")
    @ModelFromScope(baseModel = ModelType.LENGTHFIELD)
    @ScopeLimitations(DerivationType.INCLUDE_PSK_EXCHANGE_MODES_EXTENSION)
    @MethodCondition(method = "targetCanBeTested")
    @MessageStructureCategory(SeverityLevel.MEDIUM)
    @HandshakeCategory(SeverityLevel.MEDIUM)
    public void pskKeyExchangeModesExtensionLength(ArgumentsAccessor argumentAccessor, WorkflowRunner runner) {
        Config config = context.getConfig().createTls13Config();
        config.setAddPSKKeyExchangeModesExtension(true);
        genericExtensionLengthTest(runner, argumentAccessor, config, PSKKeyExchangeModesExtensionMessage.class);
    }
    
    @TlsTest(description = "Send a Pre Shared Key Exchange Modes Extension in the Hello Message with a modified length value")
    @ModelFromScope(baseModel = ModelType.LENGTHFIELD)
    @ScopeLimitations(DerivationType.INCLUDE_PSK_EXCHANGE_MODES_EXTENSION)
    @MethodCondition(method = "targetCanBeTested")
    @MessageStructureCategory(SeverityLevel.MEDIUM)
    @HandshakeCategory(SeverityLevel.MEDIUM)
    public void pskKeyExchangeModesExtensionListLength(ArgumentsAccessor argumentAccessor, WorkflowRunner runner) {
        Config config = context.getConfig().createTls13Config();
        config.setAddPSKKeyExchangeModesExtension(true);
        WorkflowTrace workflowTrace = setupLengthFieldTestForConfig(config, runner, argumentAccessor);
        PSKKeyExchangeModesExtensionMessage keyExchangeModes = getTargetedExtension(PSKKeyExchangeModesExtensionMessage.class, workflowTrace);
        keyExchangeModes.setKeyExchangeModesListLength(Modifiable.add(10));
        runner.execute(workflowTrace, runner.getPreparedConfig()).validateFinal(super::validateLengthTest);        
    }
}
