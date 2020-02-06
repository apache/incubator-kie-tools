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

package org.kie.workbench.common.stunner.bpmn.client.workitem;

import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNCategories;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(GwtMockitoTestRunner.class)
public class WorkItemDefinitionClientParserTest {

    final static String WID = "  [\n" +
            "    [\n" +
            "      \"name\" : \"Email\",\n" +
            "      \"parameters\" : [\n" +
            "        \"From\" : new StringDataType(),\n" +
            "        \"To\" : new StringDataType(),\n" +
            "        \"Subject\" : new StringDataType(),\n" +
            "        \"Body\" : new StringDataType()\n" +
            "      ],\n" +
            "      \"displayName\" : \"Email\",\n" +
            "      \"icon\" : \"defaultemailicon.gif\"\n" +
            "    ],\n" +
            "    [\n" +
            "      \"name\" : \"Rest\",\n" +
            "      \"parameters\" : [\n" +
            "          \"ContentData\" : new StringDataType(),\n" +
            "          \"Url\" : new StringDataType(),\n" +
            "          \"Method\" : new StringDataType(),\n" +
            "          \"ConnectTimeout\" : new StringDataType(),\n" +
            "          \"ReadTimeout\" : new StringDataType(),\n" +
            "          \"Username\" : new StringDataType(),\n" +
            "          \"Password\" : new StringDataType()\n" +
            "      ],\n" +
            "      \"results\" : [\n" +
            "          \"Result\" : new ObjectDataType(),\n" +
            "      ],\n" +
            "      \"displayName\" : \"REST\",\n" +
            "      \"icon\" : \"defaultservicenodeicon.png\"\n" +
            "    ],\n" +
            "     [\n" +
            "      \"name\" : \"Milestone\",\n" +
            "      \"parameters\" : [\n" +
            "          \"Condition\" : new StringDataType()\n" +
            "      ],\n" +
            "      \"displayName\" : \"Milestone\",\n" +
            "      \"icon\" : \"defaultmilestoneicon.png\",\n" +
            "      \"category\" : \"Milestone\"\n" +
            "      ]\n" +
            "  ]";

    final static String EMAIL_WID_EXTRACTED_PARAMETERS = "|Body:String,From:String,Subject:String,To:String|";

    final static String REST_WID_EXTRACTED_PARAMETERS = "|ConnectTimeout:String,ContentData:String,Method:String," +
            "Password:String,ReadTimeout:String,Url:String,Username:String|";

    @Test
    public void emptyWidsTest() {
        List<WorkItemDefinition> defs = WorkItemDefinitionClientParser.parse("");
        assertTrue(defs.isEmpty());
        defs = WorkItemDefinitionClientParser.parse("[]");
        assertTrue(defs.isEmpty());
        defs = WorkItemDefinitionClientParser.parse("[\n]");
        assertTrue(defs.isEmpty());
        defs = WorkItemDefinitionClientParser.parse(null);
        assertTrue(defs.isEmpty());
    }

    @Test
    public void widParseTest() {
        List<WorkItemDefinition> defs = WorkItemDefinitionClientParser.parse(WID);
        assertEquals(3, defs.size());
        WorkItemDefinition wid1 = defs.get(0);
        assertEquals("Email", wid1.getName());
        assertEquals("Email", wid1.getDisplayName());
        assertEquals("defaultemailicon.gif", wid1.getIconDefinition().getUri());
        assertEquals(BPMNCategories.SERVICE_TASKS, wid1.getCategory());
        assertTrue(wid1.getResults().isEmpty());
        assertEquals(EMAIL_WID_EXTRACTED_PARAMETERS, wid1.getParameters());

        WorkItemDefinition wid2 = defs.get(1);
        assertEquals("Rest", wid2.getName());
        assertEquals("REST", wid2.getDisplayName());
        assertEquals("defaultservicenodeicon.png", wid2.getIconDefinition().getUri());
        assertTrue(wid1.getResults().isEmpty());

        assertEquals(REST_WID_EXTRACTED_PARAMETERS, wid2.getParameters());
        assertEquals("|Result:java.lang.Object|", wid2.getResults());

        WorkItemDefinition wid3 = defs.get(2);

        assertEquals("Milestone", wid3.getName());
        assertEquals("Milestone", wid3.getDisplayName());
        assertEquals("defaultmilestoneicon.png", wid3.getIconDefinition().getUri());
        assertEquals("|Condition:String|", wid3.getParameters());
        assertEquals("Milestone", wid3.getCategory());
    }
}
