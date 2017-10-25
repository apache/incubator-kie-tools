/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.guvnor.test;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple JUnit test runner which automatically takes care of starting Weld container before the test runs and stopping
 * the Weld after the test finishes.
 * <p>
 * The test class can also use all CDI constructs (like @Inject). For example the test can inject BeanManager:
 * ...
 * @Inject private BeanManager beanManager;
 * ...
 * <p>
 * Use @RunWith annotation to specify the runner for the test class: {@code @RunWith(WeldJUnitRunner.class)
 */
public class WeldJUnitRunner extends BlockJUnit4ClassRunner {

    private static final Logger logger = LoggerFactory.getLogger(WeldJUnitRunner.class);

    private final Class<?> testClass;
    private Weld weld;
    private WeldContainer weldContainer;

    /**
     * Creates a WeldJUnitRunner to run {@code testClass}
     * @param testClass
     * @throws InitializationError if the test class is malformed.
     */
    public WeldJUnitRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
        this.testClass = testClass;
    }

    @Override
    protected Object createTest() throws Exception {
        return weldContainer.instance().select(testClass).get();
    }

    @Override
    public void runChild(final FrameworkMethod method,
                         RunNotifier notifier) {
        startWeld();
        try {
            super.runChild(method,
                           notifier);
        } finally {
            stopWeld();
        }
    }

    private void startWeld() {
        logger.debug("Starting Weld for test class " + testClass.getCanonicalName());
        weld = new Weld(testClass.getCanonicalName());
        weldContainer = weld.initialize();
    }

    private void stopWeld() {
        logger.debug("Stopping Weld for test class " + testClass.getCanonicalName());
        if (weld != null) {
            weld.shutdown();
        }
    }
}
