/**
 * TLS-Testsuite - A testsuite for the TLS protocol
 *
 * Copyright 2020 Ruhr University Bochum and
 * TÜV Informationstechnik GmbH
 *
 * Licensed under Apache License 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package de.rub.nds.tlstest.suite.tests.server.tls12.rfc6066;

import de.rub.nds.tlsattacker.core.config.Config;
import de.rub.nds.tlsattacker.core.protocol.message.AlertMessage;
import de.rub.nds.tlsattacker.core.protocol.message.ClientHelloMessage;
import de.rub.nds.tlsattacker.core.protocol.message.extension.ServerNameIndicationExtensionMessage;
import de.rub.nds.tlsattacker.core.protocol.message.extension.sni.SNIEntry;
import de.rub.nds.tlsattacker.core.protocol.message.extension.sni.ServerNamePair;
import de.rub.nds.tlsattacker.core.workflow.WorkflowTrace;
import de.rub.nds.tlsattacker.core.workflow.action.ReceiveAction;
import de.rub.nds.tlsattacker.core.workflow.action.SendAction;
import de.rub.nds.tlstest.framework.Validator;
import de.rub.nds.tlstest.framework.annotations.MethodCondition;
import de.rub.nds.tlstest.framework.annotations.RFC;
import de.rub.nds.tlstest.framework.annotations.ServerTest;
import de.rub.nds.tlstest.framework.annotations.TlsTest;
import de.rub.nds.tlstest.framework.execution.WorkflowRunner;
import de.rub.nds.tlstest.framework.testClasses.Tls12Test;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

@RFC(number = 6066, section = "3. Server Name Indication")
@ServerTest
public class ServerNameIndication extends Tls12Test {

    public ConditionEvaluationResult sniActive() {
        Config c = this.getConfig();
        if (c.isAddServerNameIndicationExtension()) {
            return ConditionEvaluationResult.enabled("");
        }
        return ConditionEvaluationResult.disabled("SNI is disabled");
    }

    @TlsTest(description = "The ServerNameList MUST NOT contain more than one name of the same " +
            "name_type.")
    @MethodCondition(method = "sniActive")
    public void moreThanOneNameOfTheSameType(WorkflowRunner runner) {
        Config c = this.getConfig();
        c.setAddServerNameIndicationExtension(true);

        runner.replaceSupportedCiphersuites = true;

        ServerNamePair pair = new ServerNamePair();
        pair.setServerNameConfig(c.getDefaultClientConnection().getHostname()
                .getBytes(Charset.forName("ASCII")));
        pair.setServerNameTypeConfig(c.getSniType().getValue());

        ClientHelloMessage clientHello = new ClientHelloMessage(c);
        clientHello.getExtension(ServerNameIndicationExtensionMessage.class).setServerNameList(new ArrayList<ServerNamePair>(){{
            add(pair);
            add(pair);
        }});

        WorkflowTrace workflowTrace = new WorkflowTrace();
        workflowTrace.addTlsActions(
                new SendAction(clientHello),
                new ReceiveAction(new AlertMessage())
        );

        runner.execute(workflowTrace, c).validateFinal(Validator::receivedFatalAlert);
    }

}
