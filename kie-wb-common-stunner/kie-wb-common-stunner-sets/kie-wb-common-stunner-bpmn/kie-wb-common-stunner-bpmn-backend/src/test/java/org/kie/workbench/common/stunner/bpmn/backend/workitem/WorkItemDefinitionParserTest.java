/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.backend.workitem;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import org.apache.commons.io.IOUtils;
import org.jbpm.process.core.ParameterDefinition;
import org.jbpm.process.core.datatype.impl.type.StringDataType;
import org.jbpm.process.core.impl.ParameterDefinitionImpl;
import org.jbpm.process.workitem.WorkDefinitionImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinition;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WorkItemDefinitionParserTest {

    private static final String WID_EMAIL = "org/kie/workbench/common/stunner/bpmn/backend/workitem/Email.wid";
    private static final String WID_FTP = "org/kie/workbench/common/stunner/bpmn/backend/workitem/FTP.wid";

    private static final String NAME = "name1";
    private static final String CATWGORY = "cat1";
    private static final String DESC = "desc1";
    private static final String DISPLAY_NAME = "dn1";
    private static final String DOC = "doc1";
    private static final String HANDLER = "org.hander1";
    private static final String PATH = "path1";
    private static final String ICON = "icon1";
    private static final String ICON_PATH = PATH + "/" + ICON;
    private static final String ICON_DATA = "iconData1";

    @Mock
    private WorkDefinitionImpl jbpmWorkDefinition;

    @Mock
    private ParameterDefinitionImpl param1;

    @Mock
    private ParameterDefinitionImpl param2;

    @Mock
    private Function<String, String> dataUriProvider;

    @Before
    public void init() {
        when(jbpmWorkDefinition.getName()).thenReturn(NAME);
        when(jbpmWorkDefinition.getCategory()).thenReturn(CATWGORY);
        when(jbpmWorkDefinition.getDescription()).thenReturn(DESC);
        when(jbpmWorkDefinition.getDisplayName()).thenReturn(DISPLAY_NAME);
        when(jbpmWorkDefinition.getDocumentation()).thenReturn(DOC);
        when(jbpmWorkDefinition.getDefaultHandler()).thenReturn(HANDLER);
        when(jbpmWorkDefinition.getPath()).thenReturn(PATH);
        when(jbpmWorkDefinition.getIcon()).thenReturn(ICON);
        when(dataUriProvider.apply(eq(ICON_PATH))).thenReturn(ICON_DATA);
        when(param1.getName()).thenReturn("param1");
        when(param1.getType()).thenReturn(new StringDataType());
        when(param2.getName()).thenReturn("param2");
        when(param2.getType()).thenReturn(new StringDataType());
        Set<ParameterDefinition> parameters = new HashSet<ParameterDefinition>(2) {{
            add(param1);
            add(param2);
        }};
        when(jbpmWorkDefinition.getParameters()).thenReturn(parameters);
    }

    @Test
    public void testParseJBPMWorkDefinition() {
        WorkItemDefinition workItemDefinition =
                WorkItemDefinitionParser.parse(jbpmWorkDefinition,
                                               dataUriProvider);
        assertNotNull(workItemDefinition);
        assertEquals(NAME, workItemDefinition.getName());
        assertEquals(CATWGORY, workItemDefinition.getCategory());
        assertEquals(DESC, workItemDefinition.getDescription());
        assertEquals(DISPLAY_NAME, workItemDefinition.getDisplayName());
        assertEquals(DOC, workItemDefinition.getDocumentation());
        assertEquals(HANDLER, workItemDefinition.getDefaultHandler());
        assertEquals(ICON_DATA, workItemDefinition.getIconData());
        assertEquals("|param1:String,param2:String|", workItemDefinition.getParameters());
        assertEquals("||", workItemDefinition.getResults());
    }

    @Test
    public void testEmailWorkItemDefinition() throws Exception {
        when(dataUriProvider.apply(eq("email.gif"))).thenReturn(ICON_DATA);
        String raw = loadStream(WID_EMAIL);
        Collection<WorkItemDefinition> workItemDefinitions =
                WorkItemDefinitionParser.parse(raw,
                                               dataUriProvider);
        assertNotNull(workItemDefinitions);
        assertEquals(1, workItemDefinitions.size());
        WorkItemDefinition workItemDefinition = workItemDefinitions.iterator().next();
        assertNotNull(workItemDefinition);
        assertEquals("Email", workItemDefinition.getName());
        assertEquals("Communication", workItemDefinition.getCategory());
        assertEquals("Sending emails", workItemDefinition.getDescription());
        assertEquals("Email", workItemDefinition.getDisplayName());
        assertEquals("index.html", workItemDefinition.getDocumentation());
        assertEquals("org.jbpm.process.workitem.email.EmailWorkItemHandler", workItemDefinition.getDefaultHandler());
        assertEquals(ICON_DATA, workItemDefinition.getIconData());
        assertEquals("|Body:String,From:String,Subject:String,To:String|", workItemDefinition.getParameters());
        assertEquals("||", workItemDefinition.getResults());
    }

    @Test
    public void testFTPWorkItemDefinition() throws Exception {
        when(dataUriProvider.apply(eq("ftp.gif"))).thenReturn(ICON_DATA);
        String raw = loadStream(WID_FTP);
        Collection<WorkItemDefinition> workItemDefinitions =
                WorkItemDefinitionParser.parse(raw,
                                               dataUriProvider);
        assertNotNull(workItemDefinitions);
        assertEquals(1, workItemDefinitions.size());
        WorkItemDefinition workItemDefinition = workItemDefinitions.iterator().next();
        assertNotNull(workItemDefinition);
        assertEquals("FTP", workItemDefinition.getName());
        assertEquals("File System", workItemDefinition.getCategory());
        assertEquals("Sending files using FTP", workItemDefinition.getDescription());
        assertEquals("FTP", workItemDefinition.getDisplayName());
        assertEquals("", workItemDefinition.getDocumentation());
        assertEquals("org.jbpm.process.workitem.ftp.FTPUploadWorkItemHandler", workItemDefinition.getDefaultHandler());
        assertEquals(ICON_DATA, workItemDefinition.getIconData());
        assertEquals("|Body:String,FilePath:String,Password:String,User:String|", workItemDefinition.getParameters());
        assertEquals("||", workItemDefinition.getResults());
    }

    private static String loadStream(String path) throws IOException {
        final StringWriter writer = new StringWriter();
        IOUtils.copy(Thread.currentThread()
                             .getContextClassLoader()
                             .getResourceAsStream(path),
                     writer,
                     WorkItemDefinitionParser.ENCODING);
        return writer.toString();
    }

    public static void main(String[] args) {
        WorkItemDefinitionParserTest.testServiceRepository();
    }

    private static final String JBOSS_REPO = "https://docs.jboss.org/jbpm/v6.0/repository";

    private static void testServiceRepository() {
        System.out.println("Starting...");
        Collection<WorkItemDefinition> workItems =
                WorkItemDefinitionParser.parse(JBOSS_REPO,
                                               new String[]{"Email"});

        System.out.println("Completed!");
        WorkItemDefinition workItemDefinition = workItems.iterator().next();
        assertNotNull(workItemDefinition);
        assertEquals("Email", workItemDefinition.getName());
        assertEquals("Communication", workItemDefinition.getCategory());
        assertEquals("Sending emails", workItemDefinition.getDescription());
        assertEquals("Email", workItemDefinition.getDisplayName());
        assertEquals("index.html", workItemDefinition.getDocumentation());
        assertEquals("org.jbpm.process.workitem.email.EmailWorkItemHandler", workItemDefinition.getDefaultHandler());
        assertEquals("|Body:String,From:String,Subject:String,To:String|", workItemDefinition.getParameters());
        assertEquals("||", workItemDefinition.getResults());
        assertNotNull(workItemDefinition.getIconData());
    }
}
