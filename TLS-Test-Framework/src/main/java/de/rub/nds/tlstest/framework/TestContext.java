package de.rub.nds.tlstest.framework;

import de.rub.nds.tlstest.framework.config.TestConfig;
import de.rub.nds.tlstest.framework.execution.TestRunner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TestContext {
    private static final Logger LOGGER = LogManager.getLogger();
    private TestConfig config;

    private static TestContext instance = null;
    private TestRunner testRunner = null;

    synchronized public static TestContext getInstance() {
        if (TestContext.instance == null) {
            TestContext.instance = new TestContext();
            TestContext.instance.config.parse(null);
            TestContext.instance.getTestRunner().prepareTestExecution();
        }
        return TestContext.instance;
    }

    public TestContext() {
        super();
        this.config = new TestConfig();
        this.testRunner = new TestRunner(this.config);
        TestContext.instance = this;
    }

    public TestContext(TestConfig config) {
        super();
        this.config = config;
    }





    public TestConfig getConfig() {
        return config;
    }

    public void setConfig(TestConfig config) {
        this.config = config;
    }

    public TestRunner getTestRunner() {
        return testRunner;
    }

    public void setTestRunner(TestRunner testRunner) {
        this.testRunner = testRunner;
    }
}
