/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.backend.kmodule;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.Test;
import org.kie.workbench.common.services.shared.kmodule.ClockTypeOption;
import org.kie.workbench.common.services.shared.kmodule.ConsoleLogger;
import org.kie.workbench.common.services.shared.kmodule.FileLogger;
import org.kie.workbench.common.services.shared.kmodule.KBaseModel;
import org.kie.workbench.common.services.shared.kmodule.KModuleModel;
import org.kie.workbench.common.services.shared.kmodule.KSessionModel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class KModuleContentHandlerTest {

    @Test
    public void testBasic() throws Exception {
        final KModuleContentHandler kModuleContentHandler = new KModuleContentHandler();
        final KModuleModel model = kModuleContentHandler.toModel(readResource("simpleKModule.xml"));

        assertNotNull(model);

        assertEquals(1, model.getKBases().size());
        final KBaseModel kBaseModel = model.getKBases().get("org.kie.example1");
        assertNotNull(kBaseModel);

        assertEquals(1, kBaseModel.getKSessions().size());
        final KSessionModel kSessionModel = kBaseModel.getKSessions().get(0);

        assertEquals("ksession1", kSessionModel.getName());
        assertEquals("stateful", kSessionModel.getType());
        assertTrue(kSessionModel.isDefault());
        assertEquals(ClockTypeOption.REALTIME, kSessionModel.getClockType());
        assertTrue(kSessionModel.getLogger() instanceof FileLogger);

        final FileLogger logger = (FileLogger) kSessionModel.getLogger();
        assertEquals("response-builder_filelogger", logger.getName());
        assertEquals("/tmp/response-builder_filelogger.log", logger.getFile());
        assertTrue(logger.isThreaded());
        assertEquals(1500, logger.getInterval());

        final String xml = kModuleContentHandler.toString(model);

        assertTrue(xml.contains("<kbase name=\"org.kie.example1\" default=\"false\" eventProcessingMode=\"stream\" equalsBehavior=\"identity\">"));
        assertTrue(xml.contains("<ksession name=\"ksession1\" type=\"stateful\" default=\"true\" clockType=\"realtime\">"));
        assertTrue(xml.contains("<fileLogger name=\"response-builder_filelogger\" file=\"/tmp/response-builder_filelogger.log\" threaded=\"true\" interval=\"1500\"/>"));
    }

    @Test
    public void testConsoleLogger() throws Exception {
        final KModuleContentHandler kModuleContentHandler = new KModuleContentHandler();
        final KModuleModel model = kModuleContentHandler.toModel(readResource("consoleLoggerKModule.xml"));

        assertNotNull(model);

        assertEquals(1, model.getKBases().size());
        final KBaseModel kBaseModel = model.getKBases().get("org.kie.example2");
        assertNotNull(kBaseModel);

        assertEquals(1, kBaseModel.getKSessions().size());
        final KSessionModel kSessionModel = kBaseModel.getKSessions().get(0);

        assertEquals("ksession2", kSessionModel.getName());
        assertEquals("stateless", kSessionModel.getType());
        assertFalse(kSessionModel.isDefault());
        assertEquals(ClockTypeOption.PSEUDO, kSessionModel.getClockType());
        assertTrue(kSessionModel.getLogger() instanceof ConsoleLogger);

        final String xml = kModuleContentHandler.toString(model);

        assertTrue(xml.contains("<kbase name=\"org.kie.example2\" default=\"false\" eventProcessingMode=\"stream\" equalsBehavior=\"identity\">"));
        assertTrue(xml.contains("<ksession name=\"ksession2\" type=\"stateless\" default=\"false\" clockType=\"pseudo\">"));
        assertTrue(xml.contains("<consoleLogger/>"));
    }

    @Test
    public void testMarshallingOfDefaultDroolsNameSpace() throws Exception {
        final KModuleContentHandler kModuleContentHandler = new KModuleContentHandler();
        final String kmodule = kModuleContentHandler.toString(new KModuleModel());

        assertNotNull(kmodule);
        assertTrue(kmodule.contains("xmlns=\"http://www.drools.org/xsd/kmodule\""));
    }

    private String readResource(String name) {
        StringBuffer contents = new StringBuffer();
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(name)));
            String text = null;

            while ((text = reader.readLine()) != null) {
                contents.append(text);
            }
        } catch (Exception e) {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            throw new IllegalStateException("Error while reading file.", e);
        }

        return contents.toString();
    }
}
