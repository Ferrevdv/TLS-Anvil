/**
 * TLS-Test-Framework - A framework for modeling TLS tests
 *
 * <p>Copyright 2022 Ruhr University Bochum
 *
 * <p>Licensed under Apache License 2.0 http://www.apache.org/licenses/LICENSE-2.0
 */
package de.rub.nds.tlstest.framework.model.constraint;

import de.rub.nds.anvilcore.model.DerivationScope;
import de.rub.nds.anvilcore.model.parameter.DerivationParameter;
import de.rub.nds.anvilcore.model.parameter.ParameterFactory;
import de.rub.nds.anvilcore.model.parameter.ParameterIdentifier;
import de.rub.nds.tlsattacker.core.certificate.CertificateKeyPair;
import de.rub.nds.tlsattacker.core.constants.AlgorithmResolver;
import de.rub.nds.tlsattacker.core.constants.CertificateKeyType;
import de.rub.nds.tlsattacker.core.constants.CipherSuite;
import de.rub.nds.tlsattacker.core.constants.CipherType;
import de.rub.nds.tlsattacker.core.constants.HKDFAlgorithm;
import de.rub.nds.tlsattacker.core.constants.HashAlgorithm;
import de.rub.nds.tlsattacker.core.constants.ProtocolVersion;
import de.rub.nds.tlsattacker.core.constants.SignatureAlgorithm;
import de.rub.nds.tlsattacker.core.constants.SignatureAndHashAlgorithm;
import de.rub.nds.tlsattacker.core.crypto.keys.CustomDSAPrivateKey;
import de.rub.nds.tlstest.framework.anvil.TlsAnvilConfig;
import de.rub.nds.tlstest.framework.constants.KeyExchangeType;
import de.rub.nds.tlstest.framework.constants.KeyX;
import de.rub.nds.tlstest.framework.model.TlsParameterType;
import de.rub.nds.tlstest.framework.model.derivationParameter.CertificateDerivation;
import de.rub.nds.tlstest.framework.model.derivationParameter.CipherSuiteDerivation;
import de.rub.nds.tlstest.framework.model.derivationParameter.SigAndHashDerivation;
import de.rub.nds.tlstest.framework.model.derivationParameter.SignatureBitmaskDerivation;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Analyzes modeled parameter values for the Coffee4J model.
 *
 * <p>Each Coffee4J constraint requires at least one parameter combination that is forbidden under
 * the constraint. This class provides methods used to add constraints only if this condition is
 * met.
 */
public class ConstraintHelper {

    private static final Logger LOGGER = LogManager.getLogger();

    public static boolean staticEcdhCipherSuiteModeled(DerivationScope scope) {
        CipherSuiteDerivation cipherSuiteDeriv =
                (CipherSuiteDerivation) getParameterInstance(TlsParameterType.CIPHER_SUITE);
        List<DerivationParameter<TlsAnvilConfig, CipherSuite>> values =
                cipherSuiteDeriv.getConstrainedParameterValues(scope);
        for (DerivationParameter param : values) {
            CipherSuite cipherSuite = (CipherSuite) param.getSelectedValue();
            if (!cipherSuite.isEphemeral()
                    && AlgorithmResolver.getKeyExchangeAlgorithm(cipherSuite).isKeyExchangeEcdh()) {
                return true;
            }
        }
        return false;
    }

    public static boolean staticCipherSuiteModeled(DerivationScope scope) {
        CipherSuiteDerivation cipherSuiteDeriv =
                (CipherSuiteDerivation) getParameterInstance(TlsParameterType.CIPHER_SUITE);
        List<DerivationParameter<TlsAnvilConfig, CipherSuite>> values =
                cipherSuiteDeriv.getConstrainedParameterValues(scope);
        for (DerivationParameter param : values) {
            CipherSuite cipherSuite = (CipherSuite) param.getSelectedValue();
            if (!cipherSuite.isEphemeral()) {
                return true;
            }
        }
        return false;
    }

    public static boolean ephemeralCipherSuiteModeled(DerivationScope scope) {
        CipherSuiteDerivation cipherSuiteDeriv =
                (CipherSuiteDerivation) getParameterInstance(TlsParameterType.CIPHER_SUITE);
        List<DerivationParameter<TlsAnvilConfig, CipherSuite>> values =
                cipherSuiteDeriv.getConstrainedParameterValues(scope);
        for (DerivationParameter param : values) {
            CipherSuite cipherSuite = (CipherSuite) param.getSelectedValue();
            if (cipherSuite.isEphemeral()) {
                return true;
            }
        }
        return false;
    }

    public static boolean nullSigHashModeled(DerivationScope scope) {
        SigAndHashDerivation sigHashDeriv =
                (SigAndHashDerivation) getParameterInstance(TlsParameterType.SIG_HASH_ALGORIHTM);
        List<DerivationParameter<TlsAnvilConfig, SignatureAndHashAlgorithm>> values =
                sigHashDeriv.getConstrainedParameterValues(scope);
        return values.stream().anyMatch(parameter -> parameter.getSelectedValue() == null);
    }

    public static boolean multipleBlocksizesModeled(DerivationScope scope) {
        CipherSuiteDerivation cipherSuiteDeriv =
                (CipherSuiteDerivation) getParameterInstance(TlsParameterType.CIPHER_SUITE);
        List<DerivationParameter<TlsAnvilConfig, CipherSuite>> values =
                cipherSuiteDeriv.getConstrainedParameterValues(scope);
        Set<Integer> blockLengths = new HashSet<>();
        for (DerivationParameter<TlsAnvilConfig, CipherSuite> param : values) {
            CipherSuite selectableCipherSuite = (CipherSuite) param.getSelectedValue();
            if (AlgorithmResolver.getCipherType(selectableCipherSuite) == CipherType.BLOCK) {
                blockLengths.add(AlgorithmResolver.getCipher(selectableCipherSuite).getBlocksize());
            }
        }

        return blockLengths.size() > 1;
    }

    public static boolean unpaddedCipherSuitesModeled(DerivationScope scope) {
        CipherSuiteDerivation cipherSuiteDeriv =
                (CipherSuiteDerivation) getParameterInstance(TlsParameterType.CIPHER_SUITE);
        List<DerivationParameter<TlsAnvilConfig, CipherSuite>> values =
                cipherSuiteDeriv.getConstrainedParameterValues(scope);
        for (DerivationParameter param : values) {
            CipherSuite cipherSuite = (CipherSuite) param.getSelectedValue();
            if (!cipherSuite.isUsingPadding(getTargetVersion(scope))
                    || AlgorithmResolver.getCipherType(cipherSuite) == CipherType.AEAD) {
                return true;
            }
        }

        return false;
    }

    public static boolean multipleMacSizesModeled(DerivationScope scope) {
        CipherSuiteDerivation cipherSuiteDeriv =
                (CipherSuiteDerivation) getParameterInstance(TlsParameterType.CIPHER_SUITE);
        List<DerivationParameter<TlsAnvilConfig, CipherSuite>> values =
                cipherSuiteDeriv.getConstrainedParameterValues(scope);
        Set<Integer> macLengths = new HashSet<>();
        for (DerivationParameter param : values) {
            int macLen =
                    AlgorithmResolver.getMacAlgorithm(
                                    getTargetVersion(scope), (CipherSuite) param.getSelectedValue())
                            .getSize();
            if (macLen > 0) {
                macLengths.add(macLen);
            }
        }

        return macLengths.size() > 1;
    }

    public static boolean ecdhCipherSuiteModeled(DerivationScope scope) {
        CipherSuiteDerivation cipherSuiteDeriv =
                (CipherSuiteDerivation) getParameterInstance(TlsParameterType.CIPHER_SUITE);
        List<DerivationParameter<TlsAnvilConfig, CipherSuite>> values =
                cipherSuiteDeriv.getConstrainedParameterValues(scope);
        for (DerivationParameter param : values) {
            if (isTls13Test(scope)) {
                return true;
            }
            if (AlgorithmResolver.getKeyExchangeAlgorithm((CipherSuite) param.getSelectedValue())
                    .isKeyExchangeEcdh()) {
                return true;
            }
        }

        return false;
    }

    public static boolean nonEcdhCipherSuiteModeled(DerivationScope scope) {
        CipherSuiteDerivation cipherSuiteDeriv =
                (CipherSuiteDerivation) getParameterInstance(TlsParameterType.CIPHER_SUITE);
        List<DerivationParameter<TlsAnvilConfig, CipherSuite>> values =
                cipherSuiteDeriv.getConstrainedParameterValues(scope);
        for (DerivationParameter param : values) {
            if (!AlgorithmResolver.getKeyExchangeAlgorithm((CipherSuite) param.getSelectedValue())
                    .isKeyExchangeEcdh()) {
                return true;
            }
        }

        return false;
    }

    public static boolean multipleHkdfSizesModeled(DerivationScope scope) {
        CipherSuiteDerivation cipherSuiteDeriv =
                (CipherSuiteDerivation) getParameterInstance(TlsParameterType.CIPHER_SUITE);
        List<DerivationParameter<TlsAnvilConfig, CipherSuite>> values =
                cipherSuiteDeriv.getConstrainedParameterValues(scope);
        Set<HKDFAlgorithm> hkdfAlgos = new HashSet<>();
        for (DerivationParameter param : values) {
            CipherSuite selectedCipher = (CipherSuite) param.getSelectedValue();
            hkdfAlgos.add(AlgorithmResolver.getHKDFAlgorithm(selectedCipher));
        }

        return hkdfAlgos.size() > 1;
    }

    public static boolean multipleTagSizesModeled(DerivationScope scope) {
        CipherSuiteDerivation cipherSuiteDeriv =
                (CipherSuiteDerivation) getParameterInstance(TlsParameterType.CIPHER_SUITE);
        List<DerivationParameter<TlsAnvilConfig, CipherSuite>> values =
                cipherSuiteDeriv.getConstrainedParameterValues(scope);
        Set<Integer> tagLengths = new HashSet<>();
        for (DerivationParameter param : values) {
            CipherSuite selectedCipher = (CipherSuite) param.getSelectedValue();
            tagLengths.add(getAuthTagLen(selectedCipher));
        }

        return tagLengths.size() > 1;
    }

    public static boolean nullModeled(DerivationScope scope, TlsParameterType type) {
        return getParameterInstance(type).getConstrainedParameterValues(scope).stream()
                .anyMatch(
                        parameterValue ->
                                ((DerivationParameter) parameterValue).getSelectedValue() == null);
    }

    public static boolean multipleSigAlgorithmRequiredKeyTypesModeled(DerivationScope scope) {
        SigAndHashDerivation sigHashDeriv =
                (SigAndHashDerivation) getParameterInstance(TlsParameterType.SIG_HASH_ALGORIHTM);
        List<DerivationParameter<TlsAnvilConfig, SignatureAndHashAlgorithm>> values =
                sigHashDeriv.getConstrainedParameterValues(scope);
        values = removeNull(values);
        Set<CertificateKeyType> keyTypes = new HashSet<>();
        for (DerivationParameter param : values) {
            SignatureAlgorithm sigAlgorithm =
                    ((SigAndHashDerivation) param).getSelectedValue().getSignatureAlgorithm();
            if (sigAlgorithm.name().contains("RSA")) {
                keyTypes.add(CertificateKeyType.RSA);
            } else if (sigAlgorithm == SignatureAlgorithm.ECDSA) {
                keyTypes.add(CertificateKeyType.ECDSA);
            } else if (sigAlgorithm == SignatureAlgorithm.GOSTR34102012_256
                    || sigAlgorithm == SignatureAlgorithm.GOSTR34102012_256) {
                keyTypes.add(CertificateKeyType.GOST12);
            } else if (sigAlgorithm == SignatureAlgorithm.GOSTR34102001) {
                keyTypes.add(CertificateKeyType.GOST01);
            } else if (sigAlgorithm == SignatureAlgorithm.DSA) {
                keyTypes.add(CertificateKeyType.DSS);
            } else if (sigAlgorithm == SignatureAlgorithm.ANONYMOUS) {
                // does not require a certificate
            } else {
                LOGGER.warn(
                        "SignatureAlgorithm "
                                + sigAlgorithm
                                + " was selected but should not be supported by TLS-Attacker");
            }
        }
        return keyTypes.size() > 1;
    }

    public static boolean multipleCertPublicKeyTypesModeled(DerivationScope scope) {
        CertificateDerivation certDeriv =
                (CertificateDerivation) getParameterInstance(TlsParameterType.CERTIFICATE);
        List<DerivationParameter<TlsAnvilConfig, CertificateKeyPair>> values =
                certDeriv.getConstrainedParameterValues(scope);
        Set<CertificateKeyType> certKeyTypes = new HashSet<>();
        for (DerivationParameter param : values) {
            certKeyTypes.add(
                    ((CertificateDerivation) param).getSelectedValue().getCertPublicKeyType());
        }
        return certKeyTypes.size() > 1;
    }

    public static boolean cipherSuitesWithDifferentCertPublicKeyRequirementsModeled(
            DerivationScope scope) {
        CipherSuiteDerivation cipherSuiteDeriv =
                (CipherSuiteDerivation) getParameterInstance(TlsParameterType.CIPHER_SUITE);
        List<DerivationParameter<TlsAnvilConfig, CipherSuite>> values =
                cipherSuiteDeriv.getConstrainedParameterValues(scope);
        Set<CertificateKeyType> certKeyTypes = new HashSet<>();
        for (DerivationParameter param : values) {
            try {
                certKeyTypes.add(
                        AlgorithmResolver.getCertificateKeyType(
                                ((CipherSuiteDerivation) param).getSelectedValue()));
            } catch (Exception ignored) {
                // resolver may fail for GREASE cipher suites
            }
        }
        return certKeyTypes.size() > 1;
    }

    public static boolean pssSigAlgoModeled(DerivationScope scope) {
        SigAndHashDerivation sigHashDeriv =
                (SigAndHashDerivation) getParameterInstance(TlsParameterType.SIG_HASH_ALGORIHTM);
        List<DerivationParameter<TlsAnvilConfig, SignatureAndHashAlgorithm>> algorithms =
                sigHashDeriv.getConstrainedParameterValues(scope);
        algorithms = removeNull(algorithms);
        return algorithms.stream()
                .anyMatch(
                        param ->
                                ((SigAndHashDerivation) param)
                                        .getSelectedValue()
                                        .name()
                                        .contains("PSS"));
    }

    public static boolean rsaPkMightNotSufficeForPss(DerivationScope scope) {
        SigAndHashDerivation sigHashDeriv =
                (SigAndHashDerivation) getParameterInstance(TlsParameterType.SIG_HASH_ALGORIHTM);
        List<DerivationParameter<TlsAnvilConfig, SignatureAndHashAlgorithm>> algorithms =
                sigHashDeriv.getConstrainedParameterValues(scope);
        algorithms = removeNull(algorithms);
        CertificateDerivation certDerivation =
                (CertificateDerivation) getParameterInstance(TlsParameterType.CERTIFICATE);
        List<DerivationParameter<TlsAnvilConfig, CertificateKeyPair>> certificates =
                certDerivation.getConstrainedParameterValues(scope);
        boolean pssWithSha512modeled =
                algorithms.stream()
                        .anyMatch(
                                algorithm ->
                                        (((SigAndHashDerivation) algorithm)
                                                                        .getSelectedValue()
                                                                        .getSignatureAlgorithm()
                                                                == SignatureAlgorithm.RSA_PSS_PSS
                                                        || ((SigAndHashDerivation) algorithm)
                                                                        .getSelectedValue()
                                                                        .getSignatureAlgorithm()
                                                                == SignatureAlgorithm.RSA_PSS_RSAE)
                                                && ((SigAndHashDerivation) algorithm)
                                                                .getSelectedValue()
                                                                .getHashAlgorithm()
                                                        == HashAlgorithm.SHA512);

        boolean rsaCertWithKeyBelow1024bitModeled =
                certificates.stream()
                        .anyMatch(
                                certificate ->
                                        ((CertificateDerivation) certificate)
                                                                .getSelectedValue()
                                                                .getCertPublicKeyType()
                                                        == CertificateKeyType.RSA
                                                && ((CertificateDerivation) certificate)
                                                                .getSelectedValue()
                                                                .getPublicKey()
                                                                .keySize()
                                                        < 1024);
        boolean rsaCertWithKeyBelow2048bitModeled =
                certificates.stream()
                        .anyMatch(
                                certificate ->
                                        ((CertificateDerivation) certificate)
                                                                .getSelectedValue()
                                                                .getCertPublicKeyType()
                                                        == CertificateKeyType.RSA
                                                && ((CertificateDerivation) certificate)
                                                                .getSelectedValue()
                                                                .getPublicKey()
                                                                .keySize()
                                                        < 2048);
        if (rsaCertWithKeyBelow1024bitModeled
                || (rsaCertWithKeyBelow2048bitModeled && pssWithSha512modeled)) {
            return true;
        }
        return false;
    }

    public static boolean rsaPkBelow1024BitsModeled(DerivationScope scope) {
        CertificateDerivation certDerivation =
                (CertificateDerivation) getParameterInstance(TlsParameterType.CERTIFICATE);
        List<DerivationParameter<TlsAnvilConfig, CertificateKeyPair>> certificates =
                certDerivation.getConstrainedParameterValues(scope);
        return certificates.stream()
                .anyMatch(
                        selectedCert -> {
                            return ((CertificateDerivation) selectedCert)
                                                    .getSelectedValue()
                                                    .getCertPublicKeyType()
                                            == CertificateKeyType.RSA
                                    && ((CertificateDerivation) selectedCert)
                                                    .getSelectedValue()
                                                    .getPublicKey()
                                                    .keySize()
                                            < 1024;
                        });
    }

    public static boolean rsaShaAlgLongerThan256BitsModeled(DerivationScope scope) {
        SigAndHashDerivation sigHashDeriv =
                (SigAndHashDerivation) getParameterInstance(TlsParameterType.SIG_HASH_ALGORIHTM);
        List<DerivationParameter<TlsAnvilConfig, SignatureAndHashAlgorithm>> algorithms =
                sigHashDeriv.getConstrainedParameterValues(scope);
        return algorithms.stream()
                .anyMatch(
                        selectedAlgo -> {
                            if (((SigAndHashDerivation) selectedAlgo)
                                    .getSelectedValue()
                                    .name()
                                    .contains("RSA")) {
                                switch (((SigAndHashDerivation) selectedAlgo)
                                        .getSelectedValue()
                                        .getHashAlgorithm()) {
                                    case SHA384:
                                    case SHA512:
                                        return true;
                                    default:
                                        return false;
                                }
                            }

                            return false;
                        });
    }

    public static boolean signatureLengthConstraintApplicable(DerivationScope scope) {
        CertificateDerivation certDeriv =
                (CertificateDerivation) getParameterInstance(TlsParameterType.CERTIFICATE);
        SignatureBitmaskDerivation sigBitmaskDeriv =
                (SignatureBitmaskDerivation)
                        getParameterInstance(TlsParameterType.SIGNATURE_BITMASK);
        List<DerivationParameter<TlsAnvilConfig, Integer>> bytePositions =
                sigBitmaskDeriv.getConstrainedParameterValues(scope);
        int maxBytePosition = 0;
        for (DerivationParameter selectablePosition : bytePositions) {
            if (((SignatureBitmaskDerivation) selectablePosition).getSelectedValue()
                    > maxBytePosition) {
                maxBytePosition =
                        ((SignatureBitmaskDerivation) selectablePosition).getSelectedValue();
            }
        }
        for (DerivationParameter certDerivationValue :
                certDeriv.getConstrainedParameterValues(scope)) {
            CertificateKeyPair certKeyPair =
                    (CertificateKeyPair) certDerivationValue.getSelectedValue();
            int signatureLength;
            switch (certKeyPair.getCertPublicKeyType()) {
                case RSA:
                    signatureLength =
                            sigBitmaskDeriv.computeEstimatedSignatureSize(
                                    SignatureAlgorithm.RSA, certKeyPair.getPublicKey().keySize());
                    break;
                case ECDH:
                case ECDSA:
                    signatureLength =
                            sigBitmaskDeriv.computeEstimatedSignatureSize(
                                    SignatureAlgorithm.ECDSA, certKeyPair.getPublicKey().keySize());
                    break;
                case DSS:
                    signatureLength =
                            sigBitmaskDeriv.computeEstimatedSignatureSize(
                                    SignatureAlgorithm.DSA,
                                    ((CustomDSAPrivateKey) certKeyPair.getPrivateKey())
                                            .getParams()
                                            .getQ()
                                            .bitLength());
                    break;
                default:
                    throw new RuntimeException(
                            "Can not compute signature size for CertPublicKeyType "
                                    + certKeyPair.getCertPublicKeyType());
            }

            if (maxBytePosition
                    >= SignatureBitmaskDerivation.computeSignatureSizeForCertKeyPair(certKeyPair)) {
                return true;
            }
        }

        return false;
    }

    // TODO remove these functions
    public static boolean isTls13Test(DerivationScope scope) {
        KeyX keyExchangeRequirements = getKeyExchangeRequirements(scope);
        return keyExchangeRequirements.supports(KeyExchangeType.ALL13);
    }

    public static KeyX getKeyExchangeRequirements(DerivationScope scope) {
        return (KeyX) KeyX.resolveKexAnnotation(scope.getExtensionContext());
    }

    public static ProtocolVersion getTargetVersion(DerivationScope scope) {
        if (isTls13Test(scope)) {
            return ProtocolVersion.TLS13;
        } else {
            return ProtocolVersion.TLS12;
        }
    }

    // TODO: integrate into AlgorithmResolver?
    private static int getAuthTagLen(CipherSuite cipherSuite) {
        if (cipherSuite.name().contains("CCM_8")) {
            return 8;
        }
        return 16;
    }

    private static List<DerivationParameter<TlsAnvilConfig, SignatureAndHashAlgorithm>> removeNull(
            List<DerivationParameter<TlsAnvilConfig, SignatureAndHashAlgorithm>> parameterValues) {
        return parameterValues.stream()
                .filter(value -> value.getSelectedValue() != null)
                .collect(Collectors.toList());
    }

    private static DerivationParameter getParameterInstance(TlsParameterType parameterType) {
        return ParameterFactory.getInstanceFromIdentifier(new ParameterIdentifier(parameterType));
    }
}
