/**
 * TLS-Testsuite - A testsuite for the TLS protocol
 *
 * <p>Copyright 2022 Ruhr University Bochum
 *
 * <p>Licensed under Apache License 2.0 http://www.apache.org/licenses/LICENSE-2.0
 */
package de.rub.nds.tlstest.suite;

import com.beust.jcommander.ParameterException;
import de.rub.nds.tlstest.framework.TestContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
    private static final Logger LOGGER = LogManager.getLogger();
    private static boolean finished = false;

    static {
        System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager");
    }

    public static void main(String[] args) {

        TestContext testContext = TestContext.getInstance();

        new Thread(
                        () -> {
                            while (!finished) {
                                LOGGER.debug(
                                        "RAM: {}/{}",
                                        (Runtime.getRuntime().totalMemory()
                                                        - Runtime.getRuntime().freeMemory())
                                                / 1000000,
                                        Runtime.getRuntime().totalMemory() / 1000000);
                                try {
                                    Thread.sleep(2000);
                                } catch (Exception ignored) {
                                }
                            }
                        })
                .start();

        try {
            testContext.getConfig().parse(args);

            String packageName = Main.class.getPackageName();
            if (testContext.getConfig().getAnvilTestConfig().getTestPackage() != null) {
                packageName = testContext.getConfig().getAnvilTestConfig().getTestPackage();
                LOGGER.info("Limiting test to those of package {}", packageName);
            }
            testContext.getTestRunner().runTests(packageName);
        } catch (ParameterException E) {
            LOGGER.error("Could not parse provided parameters", E);
            LOGGER.error(String.join(" ", args));
            System.exit(2);
        } catch (Exception e) {
            LOGGER.error("Something went wrong", e);
            System.exit(1);
        }

        finished = true;
    }
}
