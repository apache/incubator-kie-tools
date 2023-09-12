/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.bpmn.client.workitem;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNCategories;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(GwtMockitoTestRunner.class)
public class WorkItemDefinitionClientParserTest {

    private static final String MAIN_WID_FILE = "org/kie/workbench/common/stunner/bpmn/client/workitem/maintest.wid";
    private static final String MISSING_COLON_WID_FILE = "org/kie/workbench/common/stunner/bpmn/client/workitem/missingColon.wid";
    private static final String MISSING_NAME_WID_FILE = "org/kie/workbench/common/stunner/bpmn/client/workitem/missingName.wid";
    private static final String MISSING_DISPLAY_NAME_WID_FILE = "org/kie/workbench/common/stunner/bpmn/client/workitem/missingDisplayName.wid";
    private static final String MISSING_ICON_WID_FILE = "org/kie/workbench/common/stunner/bpmn/client/workitem/missingIcon.wid";
    private static final String MISSING_PARAMETERS_WID_FILE = "org/kie/workbench/common/stunner/bpmn/client/workitem/missingParameters.wid";
    private static final String ALL_PARAMETERS_WID_FILE = "org/kie/workbench/common/stunner/bpmn/client/workitem/allparametersPresent.wid";
    private static final String QUOTAS_WID_FILE = "org/kie/workbench/common/stunner/bpmn/client/workitem/quotas.wid";
    private static final String INVALID_START_WID_FILE = "org/kie/workbench/common/stunner/bpmn/client/workitem/invalidWidStart.wid";
    private static final String SECOND_WID_IS_INCORRECT_WID_FILE = "org/kie/workbench/common/stunner/bpmn/client/workitem/secondWidIsIncorrect.wid";
    private static final String MVEL_LIST_IN_WID_FILE = "org/kie/workbench/common/stunner/bpmn/client/workitem/listParameters.wid";
    private static final String SINGLE_LINE_COMMENTS_FILE = "org/kie/workbench/common/stunner/bpmn/client/workitem/singleLineComments.wid";
    private static final String MULTI_LINE_COMMENTS_FILE = "org/kie/workbench/common/stunner/bpmn/client/workitem/multiLineComments.wid";
    private static final String IMPORT_PART_IGNORED = "org/kie/workbench/common/stunner/bpmn/client/workitem/importPartIgnored.wid";

    final static String EMAIL_WID_EXTRACTED_PARAMETERS = "|Body:String,From:String,Subject:String,To:String|";
    final static String RESULT_WID_RETURN_EXTRACTED_PARAMETERS = "|Result:java.lang.Object|";

    final static String INCIDENT_WID_EXTRACTED_PARAMETERS = "|Incident:java.lang.Object|";

    final static String INCIDENT_WID_EXTRACTED_RETURN_PARAMETERS = "|IncidentPriority:java.lang.Object|";

    final static String REST_WID_EXTRACTED_PARAMETERS = "|ConnectTimeout:String,ContentData:String,Method:String," +
            "Password:String,ReadTimeout:String,Url:String,Username:String|";

    final static String REST_WID_EXTRACTED_NOT_COMMENTED_PARAMETERS = "|ConnectTimeout:String,Method:String,ReadTimeout:String|";

    final static private String ICON_64 = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAgAAAAIACAYAAAD0eNT6AAAYK0lEQVR4nO3de9z1+Vzv8dcMcxCjDMMwjHMOUw5D52ypFMmmaJuaKFTa6YBSm+hgt4lIOmt33nJKg8gWiYrHVDpK2zk1coxhNDOYw93+Y/VoYua+5z5c1/VZa/2ez8fj9b+xfuv9Xfd1rev3KwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA23rWrU6qTqxOr42f/5wAAR+rY6ubV3apvrn6o+uXqFdWbqwuqf7uCLqk+VL21em31gurp1fdUX13dojp67/4zAIBPdUx1p+ph1dOq367+rHpvta8rPuB3oo9Vf1E9s/qW6jbVUbv83woAi3WL6huqn6rObnUQ79Yhf6h9oHpuq582XHeX/vsBYOud1OpH7k+oXt7qx/LTh/zBdmn1J9V35cMAABzQcdW9Wv2e/p3NH+I71cXVi//9v813BwCgukb1gFY";

    @Test
    public void emptyWidsTest() {
        List<WorkItemDefinition> defs = WorkItemDefinitionClientParser.parse("");
        assertTrue(defs.isEmpty());
        defs = WorkItemDefinitionClientParser.parse("[]");
        assertTrue(defs.isEmpty());
        defs = WorkItemDefinitionClientParser.parse("[\n]");
        assertTrue(defs.isEmpty());
        defs = WorkItemDefinitionClientParser.parse("[\n[\n]\n]");
        assertTrue(defs.isEmpty());
        defs = WorkItemDefinitionClientParser.parse(null);
        assertTrue(defs.isEmpty());
        defs = WorkItemDefinitionClientParser.parse("");
        assertTrue(defs.isEmpty());
    }

    @Test
    public void testWidParseLineFeed() {
        String widFile = loadTestFile(MAIN_WID_FILE);
        testWidParse(widFile);
    }

    @Test
    public void testWidParseCarriageReturn() {
        String widFile = loadTestFile(MAIN_WID_FILE);
        testWidParse(widFile.replace("\n", "\r"));
    }

    @Test
    public void testWidParseCarriageReturnAndLineFeed() {
        String widFile = loadTestFile(MAIN_WID_FILE);
        testWidParse(widFile.replace("\n", "\r\n"));
    }

    @Test
    public void testMissingName() {
        String widFile = loadTestFile(MISSING_NAME_WID_FILE);
        List<WorkItemDefinition> defs = WorkItemDefinitionClientParser.parse(widFile);
        assertEquals(1, defs.size());
        WorkItemDefinition wid = defs.get(0);
        assertEquals("", wid.getName());
        assertEquals("Display Email", wid.getDisplayName());
        assertEquals("defaultemailicon.gif", wid.getIconDefinition().getUri());
        assertEquals(BPMNCategories.CUSTOM_TASKS, wid.getCategory());
        assertEquals("Some documentation", wid.getDocumentation());
        assertTrue(wid.getResults().isEmpty());
        assertEquals(EMAIL_WID_EXTRACTED_PARAMETERS, wid.getParameters());
    }

    @Test
    public void testMissingDisplayName() {
        String widFile = loadTestFile(MISSING_DISPLAY_NAME_WID_FILE);
        List<WorkItemDefinition> defs = WorkItemDefinitionClientParser.parse(widFile);
        assertEquals(1, defs.size());
        WorkItemDefinition wid = defs.get(0);
        assertEquals("Email", wid.getName());
        assertEquals("", wid.getDisplayName());
        assertEquals("defaultemailicon.gif", wid.getIconDefinition().getUri());
        assertEquals(BPMNCategories.CUSTOM_TASKS, wid.getCategory());
        assertEquals("Some documentation", wid.getDocumentation());
        assertTrue(wid.getResults().isEmpty());
        assertEquals(EMAIL_WID_EXTRACTED_PARAMETERS, wid.getParameters());
    }

    @Test
    public void testAllParametersPresent() {
        String widFile = loadTestFile(ALL_PARAMETERS_WID_FILE);
        List<WorkItemDefinition> defs = WorkItemDefinitionClientParser.parse(widFile);
        assertEquals(1, defs.size());
        WorkItemDefinition wid = defs.get(0);
        assertEquals("Email", wid.getName());
        assertEquals("Display Email", wid.getDisplayName());
        assertEquals("defaultemailicon.gif", wid.getIconDefinition().getUri());
        assertEquals("new org.package.DefaultHandler()", wid.getDefaultHandler());
        assertEquals("Some description", wid.getDescription());
        assertEquals(BPMNCategories.CUSTOM_TASKS, wid.getCategory());
        assertEquals("Some documentation", wid.getDocumentation());
        assertEquals(EMAIL_WID_EXTRACTED_PARAMETERS, wid.getParameters());
        assertEquals(RESULT_WID_RETURN_EXTRACTED_PARAMETERS, wid.getResults());
    }

    @Test
    public void testQuotas() {
        String widFile = loadTestFile(QUOTAS_WID_FILE);
        List<WorkItemDefinition> defs = WorkItemDefinitionClientParser.parse(widFile);
        assertEquals(1, defs.size());
        WorkItemDefinition wid = defs.get(0);
        assertEquals("Email", wid.getName());
        assertEquals("Display Email", wid.getDisplayName());
        assertEquals("defaultemailicon.gif", wid.getIconDefinition().getUri());
        assertEquals("new org.package.DefaultHandler()", wid.getDefaultHandler());
        assertEquals("Some \"description\"", wid.getDescription());
        assertEquals(BPMNCategories.CUSTOM_TASKS, wid.getCategory());
        assertEquals("Some \'documentation\'", wid.getDocumentation());
        assertEquals(EMAIL_WID_EXTRACTED_PARAMETERS, wid.getParameters());
        assertEquals(RESULT_WID_RETURN_EXTRACTED_PARAMETERS, wid.getResults());
    }

    @Test
    public void testMissingParameters() {
        String widFile = loadTestFile(MISSING_PARAMETERS_WID_FILE);
        List<WorkItemDefinition> defs = WorkItemDefinitionClientParser.parse(widFile);
        assertEquals(1, defs.size());
        WorkItemDefinition wid1 = defs.get(0);
        assertEquals("Email", wid1.getName());
        assertEquals("Display Email", wid1.getDisplayName());
        assertEquals("defaultemailicon.gif", wid1.getIconDefinition().getUri());
        assertEquals(BPMNCategories.CUSTOM_TASKS, wid1.getCategory());
        assertEquals("Some documentation", wid1.getDocumentation());
        assertTrue(wid1.getResults().isEmpty());
        assertTrue(wid1.getParameters().isEmpty());
    }

    @Test
    public void testMissingIcon() {
        String widFile = loadTestFile(MISSING_ICON_WID_FILE);
        List<WorkItemDefinition> defs = WorkItemDefinitionClientParser.parse(widFile);
        assertEquals(1, defs.size());
        WorkItemDefinition wid1 = defs.get(0);
        assertEquals("Email", wid1.getName());
        assertEquals("Display Email", wid1.getDisplayName());
        assertEquals("", wid1.getIconDefinition().getUri());
        assertEquals(BPMNCategories.CUSTOM_TASKS, wid1.getCategory());
        assertEquals("Some documentation", wid1.getDocumentation());
        assertTrue(wid1.getResults().isEmpty());
        assertEquals(EMAIL_WID_EXTRACTED_PARAMETERS, wid1.getParameters());
    }

    @Test
    public void testInvalidWidMissingColon() {
        String widFile = loadTestFile(MISSING_COLON_WID_FILE);
        List<WorkItemDefinition> defs = WorkItemDefinitionClientParser.parse(widFile);
        assertEquals(0, defs.size());
        // We can add some logging invocation test when KOGITO-3846 will be done
    }

    @Test
    public void testInvalidWidStart() {
        String widFile = loadTestFile(INVALID_START_WID_FILE);
        List<WorkItemDefinition> defs = WorkItemDefinitionClientParser.parse(widFile);
        assertEquals(0, defs.size());
        // We can add some logging invocation test when KOGITO-3846 will be done
    }

    @Test
    public void testSecondWidIsIncorrect() {
        String widFile = loadTestFile(SECOND_WID_IS_INCORRECT_WID_FILE);
        List<WorkItemDefinition> defs = WorkItemDefinitionClientParser.parse(widFile);
        // Despite of third WID is correct, parser failed on the second one so can't proceed and only returns first one
        assertEquals(1, defs.size());
        WorkItemDefinition wid1 = defs.get(0);
        assertEquals("Email", wid1.getName());
        assertEquals("Display Email", wid1.getDisplayName());
        assertEquals("Some documentation", wid1.getDocumentation());
    }

    @Test
    public void testSingleLineComments() {
        String widFile = loadTestFile(SINGLE_LINE_COMMENTS_FILE);
        List<WorkItemDefinition> defs = WorkItemDefinitionClientParser.parse(widFile);
        assertEquals(1, defs.size());

        WorkItemDefinition wid3 = defs.get(0);
        assertEquals("Rest", wid3.getName());
        assertEquals("REST", wid3.getDisplayName());
        assertEquals("", wid3.getIconDefinition().getUri());
        assertEquals(RESULT_WID_RETURN_EXTRACTED_PARAMETERS, wid3.getResults());
        assertEquals(REST_WID_EXTRACTED_NOT_COMMENTED_PARAMETERS, wid3.getParameters());
    }

    @Test
    public void testMultiLineComments() {
        String widFile = loadTestFile(MULTI_LINE_COMMENTS_FILE);
        List<WorkItemDefinition> defs = WorkItemDefinitionClientParser.parse(widFile);
        assertEquals(1, defs.size());

        WorkItemDefinition wid3 = defs.get(0);
        assertEquals("Rest", wid3.getName());
        assertEquals("REST", wid3.getDisplayName());
        assertEquals("", wid3.getIconDefinition().getUri());
        assertEquals(RESULT_WID_RETURN_EXTRACTED_PARAMETERS, wid3.getResults());
        assertEquals(REST_WID_EXTRACTED_NOT_COMMENTED_PARAMETERS, wid3.getParameters());
    }

    @Test
    public void testIgnoreImportPart() {
        String widFile = loadTestFile(IMPORT_PART_IGNORED);
        List<WorkItemDefinition> defs = WorkItemDefinitionClientParser.parse(widFile);

        assertEquals(1, defs.size());
        WorkItemDefinition wid1 = defs.get(0);

        assertEquals("Rest", wid1.getName());
        assertEquals("REST", wid1.getDisplayName());
        assertEquals("defaultservicenodeicon.png", wid1.getIconDefinition().getUri());
        assertEquals(RESULT_WID_RETURN_EXTRACTED_PARAMETERS, wid1.getResults());
        assertEquals(REST_WID_EXTRACTED_PARAMETERS, wid1.getParameters());
    }

    @Test
    public void testMvelLists() {
        String widFile = loadTestFile(MVEL_LIST_IN_WID_FILE);
        List<WorkItemDefinition> defs = WorkItemDefinitionClientParser.parse(widFile);
        assertEquals(3, defs.size());

        WorkItemDefinition wid1 = defs.get(0);
        assertEquals("firstWID", wid1.getName());
        assertEquals(INCIDENT_WID_EXTRACTED_PARAMETERS, wid1.getParameters());

        WorkItemDefinition wid2 = defs.get(1);
        assertEquals("secondWID", wid2.getName());
        assertEquals(RESULT_WID_RETURN_EXTRACTED_PARAMETERS, wid2.getResults());

        WorkItemDefinition wid3 = defs.get(2);
        assertEquals("thirdWID", wid3.getName());
    }

    private void testWidParse(final String wid) {
        List<WorkItemDefinition> defs = WorkItemDefinitionClientParser.parse(wid);
        assertEquals(4, defs.size());
        WorkItemDefinition wid1 = defs.get(0);
        assertEquals("Email", wid1.getName());
        assertEquals("Display Email", wid1.getDisplayName());
        assertEquals("defaultemailicon.gif", wid1.getIconDefinition().getUri());
        assertEquals(BPMNCategories.CUSTOM_TASKS, wid1.getCategory());
        assertEquals("Some documentation", wid1.getDocumentation());
        assertTrue(wid1.getResults().isEmpty());
        assertEquals(EMAIL_WID_EXTRACTED_PARAMETERS, wid1.getParameters());

        WorkItemDefinition wid2 = defs.get(1);
        assertEquals("IncidentPriorityService", wid2.getName());
        assertEquals("Incident Priority Service", wid2.getDisplayName());
        assertEquals("incidentpriorityicon.png", wid2.getIconDefinition().getUri());
        assertEquals(BPMNCategories.CUSTOM_TASKS, wid2.getCategory());
        assertEquals(INCIDENT_WID_EXTRACTED_RETURN_PARAMETERS, wid2.getResults());
        assertEquals(INCIDENT_WID_EXTRACTED_PARAMETERS, wid2.getParameters());

        WorkItemDefinition wid3 = defs.get(2);
        assertEquals("Rest", wid3.getName());
        assertEquals("REST", wid3.getDisplayName());
        assertEquals("defaultservicenodeicon.png", wid3.getIconDefinition().getUri());
        assertEquals(RESULT_WID_RETURN_EXTRACTED_PARAMETERS, wid3.getResults());
        assertEquals(REST_WID_EXTRACTED_PARAMETERS, wid3.getParameters());

        WorkItemDefinition wid4 = defs.get(3);

        assertEquals("Milestone", wid4.getName());
        assertEquals("Milestone", wid4.getDisplayName());
        assertEquals(ICON_64, wid4.getIconDefinition().getUri());
        assertEquals(ICON_64, wid4.getIconDefinition().getIconData());
        assertEquals("|Condition:String|", wid4.getParameters());
        assertEquals("Milestone", wid4.getCategory());
    }

    public static String loadTestFile(String path) {
        return new BufferedReader(
                new InputStreamReader(
                        Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResourceAsStream(path))
                )
        )
                .lines()
                .collect(Collectors.joining("\n"));
    }
}
