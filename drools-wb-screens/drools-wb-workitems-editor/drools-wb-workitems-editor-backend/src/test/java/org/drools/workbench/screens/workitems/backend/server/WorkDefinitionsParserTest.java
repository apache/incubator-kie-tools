/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.screens.workitems.backend.server;


import org.drools.core.process.core.WorkDefinition;
import org.junit.Test;
import org.mvel2.CompileException;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class WorkDefinitionsParserTest {

    @Test
    public void testValidWorkitemDefinition() throws Exception {
        List<String> defStrings = new ArrayList<>();
        defStrings.add(loadFile( "validWorkItemDefinition.wid" ));

        Map<String, WorkDefinition> defs = WorkDefinitionsParser.parse( defStrings );

        assertNotNull( defs );
        assertEquals(1, defs.size() );

        WorkDefinition myTaskDef = defs.get( "MyTask" );

        assertNotNull( myTaskDef );
        assertEquals("MyTask", myTaskDef.getName());
        assertNotNull(myTaskDef.getParameter("MyFirstParam" ));
        assertNotNull(myTaskDef.getParameter("MySecondParam" ));
        assertNotNull(myTaskDef.getParameter( "MyThirdParam" ));
    }

    @Test(expected = CompileException.class)
    public void testInvalidWorkitemDefinition() {
        List<String> defStrings = new ArrayList<>();
        defStrings.add(loadFile( "missingImportWorkItemDefinition.wid" ));

        WorkDefinitionsParser.parse( defStrings );
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
