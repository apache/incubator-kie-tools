/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.jboss.errai.ioc.client.IOCClientTestCase;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.tests.EmptyDiagramTest;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.tests.InputDataTest;
import org.kie.workbench.common.stunner.kogito.client.service.KogitoClientDiagramService;

public class DMNMarshallerKogitoUnmarshallerTest extends IOCClientTestCase {

    private static final Logger LOGGER = Logger.getLogger(DMNMarshallerKogitoUnmarshallerTest.class.getName());

    @Override
    public String getModuleName() {
        return "org.kie.workbench.common.dmn.webapp.kogito.marshaller.DMNMarshallerKogitoUnmarshallerTest";
    }

    public void testUnmarshall() {
        LOGGER.info("Entering testUnmarshall()...");

        final DMNUnmarshallerTestBootstrap bootstrap = new DMNUnmarshallerTestBootstrap();
        bootstrap.bootstrap(() -> {
            final KogitoClientDiagramService service = setupService();
            final List<DMNTest> tests = getTests();
            final Map<Class<?>, AssertionError> errors = new HashMap<>();
            for (DMNTest test : tests) {
                try {
                    test.run(service);
                } catch (AssertionError ae) {
                    errors.put(test.getClass(), ae);
                } catch (Exception e) {
                    e.fillInStackTrace();
                    fail(e.getMessage());
                }
            }

            if (!errors.isEmpty()) {
                throw new MultipleAssertionsError(errors);
            }
        });

        LOGGER.info("Exiting testUnmarshall()...");
    }

    private List<DMNTest> getTests() {
        final List<DMNTest> tests = new ArrayList<>();
        tests.add(new EmptyDiagramTest());
        tests.add(new InputDataTest());
        return tests;
    }

    private KogitoClientDiagramService setupService() {
        LOGGER.info("Entering setupService()...");

        try {
            final SyncBeanDef<KogitoClientDiagramService> beanDef = IOC.getBeanManager().lookupBean(KogitoClientDiagramService.class);
            final KogitoClientDiagramService service = beanDef.getInstance();
            return service;
        } catch (Exception e) {
            e.fillInStackTrace();
            LOGGER.severe("Exception in setupService()...[" + e.getMessage() + "]");
        } finally {
            LOGGER.info("Exiting  setupService()...");
        }

        throw new IllegalStateException("Unable to instantiate KogitoClientDiagramService");
    }
}
