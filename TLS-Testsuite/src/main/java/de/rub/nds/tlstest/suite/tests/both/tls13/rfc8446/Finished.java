package de.rub.nds.tlstest.suite.tests.both.tls13.rfc8446;

import de.rub.nds.modifiablevariable.util.Modifiable;
import de.rub.nds.tlsattacker.core.constants.AlertDescription;
import de.rub.nds.tlsattacker.core.constants.HandshakeMessageType;
import de.rub.nds.tlsattacker.core.protocol.message.AlertMessage;
import de.rub.nds.tlsattacker.core.protocol.message.FinishedMessage;
import de.rub.nds.tlsattacker.core.workflow.WorkflowTrace;
import de.rub.nds.tlsattacker.core.workflow.action.ReceiveAction;
import de.rub.nds.tlsattacker.core.workflow.action.SendAction;
import de.rub.nds.tlsattacker.core.workflow.factory.WorkflowTraceType;
import de.rub.nds.tlstest.framework.Validator;
import de.rub.nds.tlstest.framework.annotations.RFC;
import de.rub.nds.tlstest.framework.annotations.TlsTest;
import de.rub.nds.tlstest.framework.constants.SeverityLevel;
import de.rub.nds.tlstest.framework.execution.WorkflowRunner;
import de.rub.nds.tlstest.framework.testClasses.Tls13Test;

@RFC(number = 8446, section = "4.4.4. Finished")
public class Finished extends Tls13Test {

    @TlsTest(description = "Recipients of Finished messages MUST verify " +
            "that the contents are correct and if incorrect MUST terminate " +
            "the connection with a \"decrypt_error\" alert.", securitySeverity = SeverityLevel.CRITICAL)
    public void invalidSignature(WorkflowRunner runner) {
        runner.replaceSelectedCiphersuite = true;
        runner.replaceSupportedCiphersuites = true;

        WorkflowTrace workflowTrace = runner.generateWorkflowTraceUntilSendingMessage(WorkflowTraceType.HANDSHAKE, HandshakeMessageType.FINISHED);
        workflowTrace.addTlsActions(
                new SendAction(new FinishedMessage()),
                new ReceiveAction(new AlertMessage())
        );

        runner.setStateModifier(i -> {
            i.getWorkflowTrace().getFirstSendMessage(FinishedMessage.class)
                    .setVerifyData(Modifiable.xor(new byte[]{0x01}, 0));
            return null;
        });

        runner.execute(workflowTrace).validateFinal(i -> {
            Validator.receivedFatalAlert(i);

            AlertMessage msg = i.getWorkflowTrace().getFirstReceivedMessage(AlertMessage.class);
            if (msg == null) return;
            Validator.testAlertDescription(i, AlertDescription.DECRYPT_ERROR, msg);
        });
    }
}
