/**
 * TLS-Testsuite - A testsuite for the TLS protocol
 *
 * <p>Copyright 2022 Ruhr University Bochum
 *
 * <p>Licensed under Apache License 2.0 http://www.apache.org/licenses/LICENSE-2.0
 */
package de.rub.nds.tlstest.suite.tests.client.tls13.rfc8701;

import de.rub.nds.anvilcore.annotation.AnvilTest;
import de.rub.nds.anvilcore.annotation.ClientTest;
import de.rub.nds.anvilcore.annotation.ExcludeParameter;
import de.rub.nds.anvilcore.annotation.IncludeParameter;
import de.rub.nds.anvilcore.coffee4j.model.ModelFromScope;
import de.rub.nds.modifiablevariable.util.Modifiable;
import de.rub.nds.tlsattacker.core.config.Config;
import de.rub.nds.tlsattacker.core.constants.CipherSuite;
import de.rub.nds.tlsattacker.core.constants.ExtensionType;
import de.rub.nds.tlsattacker.core.constants.ProtocolVersion;
import de.rub.nds.tlsattacker.core.constants.SignatureAndHashAlgorithm;
import de.rub.nds.tlsattacker.core.protocol.message.*;
import de.rub.nds.tlsattacker.core.protocol.message.extension.GreaseExtensionMessage;
import de.rub.nds.tlsattacker.core.protocol.message.extension.SupportedVersionsExtensionMessage;
import de.rub.nds.tlsattacker.core.workflow.WorkflowTrace;
import de.rub.nds.tlsattacker.core.workflow.action.ReceiveAction;
import de.rub.nds.tlsattacker.core.workflow.action.SendAction;
import de.rub.nds.tlsattacker.core.workflow.factory.WorkflowTraceType;
import de.rub.nds.tlstest.framework.Validator;
import de.rub.nds.tlstest.framework.anvil.TlsParameterCombination;
import de.rub.nds.tlstest.framework.execution.WorkflowRunner;
import de.rub.nds.tlstest.framework.model.derivationParameter.GreaseCipherSuiteDerivation;
import de.rub.nds.tlstest.framework.model.derivationParameter.GreaseExtensionDerivation;
import de.rub.nds.tlstest.framework.model.derivationParameter.GreaseProtocolVersionDerivation;
import de.rub.nds.tlstest.framework.model.derivationParameter.GreaseSigHashDerivation;
import de.rub.nds.tlstest.framework.testClasses.Tls13Test;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;

@ClientTest
public class ServerInitiatedExtensionPoints extends Tls13Test {

    @AnvilTest(id = "8701-91tcbyhyNk")
    @ModelFromScope(modelType = "CERTIFICATE")
    @IncludeParameter("GREASE_EXTENSION")
    public void advertiseGreaseExtensionsInSessionTicket(
            ArgumentsAccessor argumentAccessor, WorkflowRunner runner) {
        Config c = getPreparedConfig(argumentAccessor, runner);
        ExtensionType selectedGreaseExt =
                parameterCombination
                        .getParameter(GreaseExtensionDerivation.class)
                        .getSelectedValue();
        WorkflowTrace workflowTrace = runner.generateWorkflowTrace(WorkflowTraceType.HANDSHAKE);
        workflowTrace.addTlsActions(new SendAction(new NewSessionTicketMessage()));

        NewSessionTicketMessage msg =
                workflowTrace.getFirstSendMessage(NewSessionTicketMessage.class);
        msg.addExtension(new GreaseExtensionMessage(selectedGreaseExt, 25));

        runner.execute(workflowTrace, c).validateFinal(Validator::executedAsPlanned);
    }

    @AnvilTest(id = "8701-q8vvYUsUCu")
    @ModelFromScope(modelType = "CERTIFICATE")
    @IncludeParameter("GREASE_PROTOCOL_VERSION")
    public void selectGreaseVersion(ArgumentsAccessor argumentAccessor, WorkflowRunner runner) {
        Config c = getPreparedConfig(argumentAccessor, runner);
        WorkflowTrace workflowTrace = runner.generateWorkflowTrace(WorkflowTraceType.HELLO);
        workflowTrace.addTlsActions(new ReceiveAction(new AlertMessage()));
        sharedGreaseVersionTest(workflowTrace, runner, parameterCombination);
    }

    public static void sharedGreaseVersionTest(
            WorkflowTrace workflowTrace,
            WorkflowRunner runner,
            TlsParameterCombination externalTlsParameterCombination) {
        ProtocolVersion selectedGreaseVersion =
                externalTlsParameterCombination
                        .getParameter(GreaseProtocolVersionDerivation.class)
                        .getSelectedValue();

        ServerHelloMessage sh = workflowTrace.getFirstSendMessage(ServerHelloMessage.class);
        SupportedVersionsExtensionMessage ext =
                sh.getExtension(SupportedVersionsExtensionMessage.class);
        ext.setSupportedVersions(Modifiable.explicit(selectedGreaseVersion.getValue()));

        runner.execute(workflowTrace, runner.getPreparedConfig())
                .validateFinal(Validator::receivedFatalAlert);
    }

    @AnvilTest(id = "8701-xwVd59Y3Fq")
    @ModelFromScope(modelType = "CERTIFICATE")
    @IncludeParameter("GREASE_CIPHERSUITE")
    @ExcludeParameter("CIPHER_SUITE")
    public void selectGreaseCipherSuite(ArgumentsAccessor argumentAccessor, WorkflowRunner runner) {
        Config c = getPreparedConfig(argumentAccessor, runner);
        WorkflowTrace workflowTrace = runner.generateWorkflowTrace(WorkflowTraceType.HELLO);
        workflowTrace.addTlsActions(new ReceiveAction(new AlertMessage()));
        sharedGreaseCipherSuiteTest(workflowTrace, runner, parameterCombination);
    }

    public static void sharedGreaseCipherSuiteTest(
            WorkflowTrace workflowTrace,
            WorkflowRunner runner,
            TlsParameterCombination externalTlsParameterCombination) {
        CipherSuite selectedGreaseCipherSuite =
                externalTlsParameterCombination
                        .getParameter(GreaseCipherSuiteDerivation.class)
                        .getSelectedValue();

        ServerHelloMessage sh = workflowTrace.getFirstSendMessage(ServerHelloMessage.class);
        sh.setSelectedCipherSuite(Modifiable.explicit(selectedGreaseCipherSuite.getByteValue()));

        runner.execute(workflowTrace, runner.getPreparedConfig())
                .validateFinal(Validator::receivedFatalAlert);
    }

    @AnvilTest(id = "8701-NczJT3TSj4")
    @ModelFromScope(modelType = "CERTIFICATE")
    @IncludeParameter("GREASE_EXTENSION")
    public void sendServerHelloGreaseExtension(
            ArgumentsAccessor argumentAccessor, WorkflowRunner runner) {
        Config c = getPreparedConfig(argumentAccessor, runner);
        WorkflowTrace workflowTrace = runner.generateWorkflowTrace(WorkflowTraceType.HELLO);
        workflowTrace.addTlsActions(new ReceiveAction(new AlertMessage()));
        sharedServerHelloGreaseExtensionTest(workflowTrace, runner, parameterCombination);
    }

    public static void sharedServerHelloGreaseExtensionTest(
            WorkflowTrace workflowTrace,
            WorkflowRunner runner,
            TlsParameterCombination externalTlsParameterCombination) {
        ExtensionType selectedGreaseExt =
                externalTlsParameterCombination
                        .getParameter(GreaseExtensionDerivation.class)
                        .getSelectedValue();

        ServerHelloMessage sh = workflowTrace.getFirstSendMessage(ServerHelloMessage.class);
        sh.addExtension(new GreaseExtensionMessage(selectedGreaseExt, 25));

        runner.execute(workflowTrace, runner.getPreparedConfig())
                .validateFinal(Validator::receivedFatalAlert);
    }

    @AnvilTest(id = "8701-pVCWxJraM8")
    @ModelFromScope(modelType = "CERTIFICATE")
    @IncludeParameter("GREASE_EXTENSION")
    public void sendEncryptedExtensionsGreaseExtension(
            ArgumentsAccessor argumentAccessor, WorkflowRunner runner) {
        Config c = getPreparedConfig(argumentAccessor, runner);
        WorkflowTrace workflowTrace = runner.generateWorkflowTrace(WorkflowTraceType.HELLO);
        workflowTrace.addTlsActions(new ReceiveAction(new AlertMessage()));
        ExtensionType selectedGreaseExt =
                parameterCombination
                        .getParameter(GreaseExtensionDerivation.class)
                        .getSelectedValue();

        EncryptedExtensionsMessage sh =
                workflowTrace.getFirstSendMessage(EncryptedExtensionsMessage.class);
        sh.addExtension(new GreaseExtensionMessage(selectedGreaseExt, 25));

        runner.execute(workflowTrace, c).validateFinal(Validator::receivedFatalAlert);
    }

    @AnvilTest(id = "8701-9F3St2di12")
    @IncludeParameter("GREASE_SIG_HASH")
    public void sendCertificateVerifyGreaseSignatureAlgorithm(
            ArgumentsAccessor argumentAccessor, WorkflowRunner runner) {
        Config c = getPreparedConfig(argumentAccessor, runner);
        WorkflowTrace workflowTrace = runner.generateWorkflowTrace(WorkflowTraceType.HELLO);
        workflowTrace.addTlsActions(new ReceiveAction(new AlertMessage()));
        SignatureAndHashAlgorithm selectedGreaseSigHash =
                parameterCombination.getParameter(GreaseSigHashDerivation.class).getSelectedValue();

        CertificateVerifyMessage sh =
                workflowTrace.getFirstSendMessage(CertificateVerifyMessage.class);
        sh.setSignatureHashAlgorithm(Modifiable.explicit(selectedGreaseSigHash.getByteValue()));

        runner.execute(workflowTrace, c).validateFinal(Validator::receivedFatalAlert);
    }
}
