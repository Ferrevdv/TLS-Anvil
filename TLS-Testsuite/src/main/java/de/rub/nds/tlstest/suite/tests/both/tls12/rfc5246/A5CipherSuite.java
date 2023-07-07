/**
 * TLS-Testsuite - A testsuite for the TLS protocol
 *
 * <p>Copyright 2022 Ruhr University Bochum
 *
 * <p>Licensed under Apache License 2.0 http://www.apache.org/licenses/LICENSE-2.0
 */
package de.rub.nds.tlstest.suite.tests.both.tls12.rfc5246;

import de.rub.nds.tlsattacker.core.constants.CipherSuite;
import de.rub.nds.tlstest.framework.annotations.RFC;
import de.rub.nds.tlstest.framework.annotations.TestDescription;
import de.rub.nds.tlstest.framework.annotations.categories.ComplianceCategory;
import de.rub.nds.tlstest.framework.annotations.categories.HandshakeCategory;
import de.rub.nds.tlstest.framework.annotations.categories.SecurityCategory;
import de.rub.nds.tlstest.framework.constants.SeverityLevel;
import de.rub.nds.tlstest.framework.testClasses.Tls12Test;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

@RFC(number = 5246, section = "A.5. The Cipher Suite")
public class A5CipherSuite extends Tls12Test {

    @Test
    @SecurityCategory(SeverityLevel.CRITICAL)
    @TestDescription(
            "TLS_NULL_WITH_NULL_NULL is specified and is the initial state of a "
                    + "TLS connection during the first handshake on that channel, but MUST "
                    + "NOT be negotiated, as it provides no more protection than an "
                    + "unsecured connection.")
    @HandshakeCategory(SeverityLevel.MEDIUM)
    @ComplianceCategory(SeverityLevel.HIGH)
    public void negotiateTLS_NULL_WITH_NULL_NULL() {
        List<CipherSuite> suites =
                new ArrayList<>(context.getFeatureExtractionResult().getCipherSuites());
        if (suites.contains(CipherSuite.TLS_NULL_WITH_NULL_NULL)) {
            throw new AssertionError("TLS_NULL_WITH_NULL_NULL ciphersuite is supported");
        }
    }

    /*@AnvilTest(description = "These cipher suites MUST NOT be used by TLS 1.2 implementations unless the application " +
    "layer has specifically requested to allow anonymous key exchange", securitySeverity = SeverityLevel.HIGH)*/
    @Test
    @SecurityCategory(SeverityLevel.CRITICAL)
    @TestDescription(
            "These "
                    + "cipher suites MUST NOT be used by TLS 1.2 implementations unless the "
                    + "application layer has specifically requested to allow anonymous key "
                    + "exchange.")
    @HandshakeCategory(SeverityLevel.MEDIUM)
    @ComplianceCategory(SeverityLevel.HIGH)
    public void anonCipherSuites() {
        List<CipherSuite> suites =
                new ArrayList<>(context.getFeatureExtractionResult().getCipherSuites());
        List<CipherSuite> forbidden = CipherSuite.getImplemented();
        forbidden.removeIf(i -> !i.toString().contains("_anon_"));

        List<String> errors = new ArrayList<>();
        for (CipherSuite i : forbidden) {
            if (suites.contains(i)) {
                errors.add(i.toString());
            }
        }

        if (errors.size() > 0) {
            throw new AssertionError(
                    String.format(
                            "The following ciphersuites should not be supported: %s",
                            String.join(", ", errors)));
        }
    }
}
