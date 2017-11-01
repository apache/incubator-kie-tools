/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.workitems.backend.server;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jbpm.process.core.WorkDefinition;
import org.jbpm.process.workitem.WorkDefinitionImpl;
import org.junit.Test;
import org.mvel2.CompileException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class WorkDefinitionsParserTest {

    @Test
    public void testValidWorkitemDefinition() throws Exception {
        List<String> defStrings = new ArrayList<>();
        defStrings.add(loadFile("validWorkItemDefinition.wid"));

        Map<String, WorkDefinition> defs = WorkDefinitionsParser.parse(defStrings);

        assertNotNull(defs);
        assertEquals(1,
                     defs.size());

        WorkDefinitionImpl myTaskDef = (WorkDefinitionImpl) defs.get("MyTask");

        assertNotNull(myTaskDef);
        assertEquals("MyTask",
                     myTaskDef.getName());
        assertNotNull(myTaskDef.getParameter("MyFirstParam"));
        assertNotNull(myTaskDef.getParameter("MySecondParam"));
        assertNotNull(myTaskDef.getParameter("MyThirdParam"));

        assertNotNull(myTaskDef.getParameterValues());
        assertEquals(2,
                     myTaskDef.getParameterValues().size());
        Map<String, Object> paramValues = myTaskDef.getParameterValues();
        assertTrue(paramValues.containsKey("MyFirstParam"));
        assertTrue(paramValues.containsKey("MySecondParam"));

        assertNotNull(myTaskDef.getVersion());
        assertEquals("1.0",
                     myTaskDef.getVersion());

        assertNotNull(myTaskDef.getDocumentation());
        assertEquals("documentation for sample workitem",
                     myTaskDef.getDocumentation());

        assertNotNull(myTaskDef.getDescription());
        assertEquals("this is a sample workitem",
                     myTaskDef.getDescription());

        assertNotNull(myTaskDef.getMavenDependencies());
        assertEquals(1,
                     myTaskDef.getMavenDependencies().length);
        assertEquals("org.jboss:somemodule:3.2",
                     myTaskDef.getMavenDependencies()[0]);
    }

    @Test(expected = CompileException.class)
    public void testMissingCustomDataTypeDefinition() {
        List<String> defStrings = new ArrayList<>();
        defStrings.add(loadFile("missingCustomDataTypeDefinition.wid"));

        WorkDefinitionsParser.parse(defStrings);
    }

    private String loadFile(String fileName) {
        try {
            URL fileURL = getClass().getResource(fileName);
            return new String(Files.readAllBytes(Paths.get(fileURL.toURI())));
        } catch (Exception e) {
            return null;
        }
    }
}
