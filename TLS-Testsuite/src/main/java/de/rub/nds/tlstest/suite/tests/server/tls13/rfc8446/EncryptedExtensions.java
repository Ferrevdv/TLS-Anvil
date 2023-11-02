/**
 * TLS-Testsuite - A testsuite for the TLS protocol
 *
 * <p>Copyright 2022 Ruhr University Bochum
 *
 * <p>Licensed under Apache License 2.0 http://www.apache.org/licenses/LICENSE-2.0
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.rub.nds.tlstest.suite.tests.server.tls13.rfc8446;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.rub.nds.anvilcore.annotation.AnvilTest;
import de.rub.nds.anvilcore.annotation.ServerTest;
import de.rub.nds.tlsattacker.core.config.Config;
import de.rub.nds.tlsattacker.core.constants.ExtensionType;
import de.rub.nds.tlsattacker.core.protocol.message.EncryptedExtensionsMessage;
import de.rub.nds.tlsattacker.core.protocol.message.extension.ExtensionMessage;
import de.rub.nds.tlsattacker.core.workflow.WorkflowTrace;
import de.rub.nds.tlsattacker.core.workflow.factory.WorkflowTraceType;
import de.rub.nds.tlstest.framework.constants.AssertMsgs;
import de.rub.nds.tlstest.framework.execution.WorkflowRunner;
import de.rub.nds.tlstest.framework.testClasses.Tls13Test;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;

@ServerTest
public class EncryptedExtensions extends Tls13Test {

    public ConditionEvaluationResult sniActive() {
        Config c = this.getConfig();
        if (c.isAddServerNameIndicationExtension()) {
            return ConditionEvaluationResult.enabled("");
        }
        return ConditionEvaluationResult.disabled("SNI is disabled");
    }

    @AnvilTest(id = "8446-ZkFrZYKzbi")
    public void includedInvalidExtensions(
            ArgumentsAccessor argumentAccessor, WorkflowRunner runner) {
        Config c = getPreparedConfig(argumentAccessor, runner);

        WorkflowTrace workflowTrace = runner.generateWorkflowTrace(WorkflowTraceType.HANDSHAKE);

        runner.execute(workflowTrace, c)
                .validateFinal(
                        i -> {
                            WorkflowTrace trace = i.getWorkflowTrace();
                            EncryptedExtensionsMessage encExt =
                                    trace.getFirstReceivedMessage(EncryptedExtensionsMessage.class);
                            assertNotNull(AssertMsgs.ENCRYPTED_EXTENSIONS_NOT_RECEIVED, encExt);

                            for (ExtensionMessage ext : encExt.getExtensions()) {
                                assertTrue(
                                        "EncryptedExtensions contained a forbidden extension: "
                                                + ExtensionType.getExtensionType(
                                                        ext.getExtensionType().getValue()),
                                        ExtensionType.allowedInEncryptedExtensions(
                                                ExtensionType.getExtensionType(
                                                        ext.getExtensionType().getValue())));
                            }
                        });
    }
}
