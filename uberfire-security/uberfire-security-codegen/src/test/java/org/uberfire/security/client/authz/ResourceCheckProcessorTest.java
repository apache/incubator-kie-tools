/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uberfire.security.client.authz;

import org.jboss.errai.codegen.Context;
import org.jboss.errai.codegen.Statement;
import org.jboss.errai.ioc.rebind.ioc.injector.api.Decorable;
import org.jboss.errai.ioc.rebind.ioc.injector.api.FactoryController;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.security.annotations.ResourceCheck;
import org.uberfire.security.processors.ResourceCheckProcessor;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class ResourceCheckProcessorTest {

    @Mock
    Decorable decorable;

    @Mock
    FactoryController controller;

    Context context = Context.create();
    ResourceCheckProcessor processor;

    @Before
    public void setUp() {
        processor = new ResourceCheckProcessor(ResourceCheck.class);
        context.addVariable("project1",
                            String.class);
        context.addVariable("this",
                            FunctionTest.class);
    }

    @Test
    public void testGlobalActionStatement() {
        Statement stmt = processor.createGlobalActionCheck("project",
                                                           "create",
                                                           null,
                                                           null);
        String ifStr = stmt.generate(context);
        assertEquals(ifStr,
                     "if (!(org.uberfire.security.client.authz.AuthorizationManagerHelper.authorize(\"project\", \"create\"))) {\n" +
                             "  return;\n" +
                             "}");
    }

    @Test
    public void testResourceActionStatement() {
        Statement stmt = processor.createResourceActionCheck("project1",
                                                             "create",
                                                             null,
                                                             null);
        String ifStr = stmt.generate(context);
        assertEquals(ifStr,
                     "if (!(org.uberfire.security.client.authz.AuthorizationManagerHelper.authorize(project1, \"create\"))) {\n" +
                             "  return;\n" +
                             "}");
    }

    @Test
    public void testCallbacks() {
        Statement stmt = processor.createResourceActionCheck("project1",
                                                             "read",
                                                             "granted",
                                                             "denied");
        String ifStr = stmt.generate(context);
        assertEquals(ifStr,
                     "if (!(org.uberfire.security.client.authz.AuthorizationManagerHelper.authorize(project1, \"read\"))) {\n" +
                             "  denied();\n" +
                             "  return;\n" +
                             "} else {\n" +
                             "  granted();\n" +
                             "}");
    }

    interface FunctionTest {

        void granted();

        void denied();
    }
}
