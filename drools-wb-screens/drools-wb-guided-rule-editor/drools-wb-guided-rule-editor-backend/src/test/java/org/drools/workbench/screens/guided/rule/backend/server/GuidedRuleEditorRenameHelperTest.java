/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.rule.backend.server;

import javax.enterprise.inject.Instance;

import org.drools.workbench.screens.guided.rule.type.GuidedRuleDRLResourceTypeDefinition;
import org.drools.workbench.screens.guided.rule.type.GuidedRuleDSLRResourceTypeDefinition;
import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.project.categories.Decision;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.datamodel.backend.server.service.DataModelService;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.options.CommentedOption;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GuidedRuleEditorRenameHelperTest {

    @Mock
    private IOService ioService;

    @Mock
    private GuidedRuleEditorServiceUtilities utilities;

    @Mock
    private CommentedOptionFactory commentedOptionFactory;

    @Mock
    private DataModelService dataModelService;

    private GuidedRuleEditorRenameHelper helper;
    private GuidedRuleDRLResourceTypeDefinition drlResourceType = new GuidedRuleDRLResourceTypeDefinition(new Decision());
    private GuidedRuleDSLRResourceTypeDefinition dslrResourceType = new GuidedRuleDSLRResourceTypeDefinition(new Decision());

    private final String drl = "rule \"rule\"\n" +
            "when\n" +
            "$p : Person()\n" +
            "then\n" +
            "modify( $p ) {\n" +
            "}\n" +
            "end";

    private final String dslr = "rule \"rule\"\n" +
            "when\n" +
            ">$p : Person()\n" +
            "then\n" +
            ">modify( $p ) {\n" +
            ">}\n" +
            "end";

    private String[] dsls = new String[]{"There is a person=Person()"};

    @Before
    public void setup() {
        helper = new GuidedRuleEditorRenameHelper(ioService,
                                                  drlResourceType,
                                                  dslrResourceType,
                                                  utilities,
                                                  commentedOptionFactory,
                                                  dataModelService,
                                                  mock(Instance.class));
        when(utilities.loadDslsForPackage(any(Path.class))).thenReturn(dsls);
    }

    @Test
    public void testRDRLFile() {
        final Path pathSource = mock(Path.class);
        final Path pathDestination = mock(Path.class);
        when(pathSource.toURI()).thenReturn("file://p0/src/main/resources/MyFile.rdrl");
        when(pathDestination.toURI()).thenReturn("file://p0/src/main/resources/MyNewFile.rdrl");
        when(pathDestination.getFileName()).thenReturn("MyNewFile.rdrl");
        when(ioService.readAllString(any(org.uberfire.java.nio.file.Path.class))).thenReturn(drl);

        helper.postProcess(pathSource,
                           pathDestination);

        final ArgumentCaptor<String> drlArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(ioService,
               times(1)).write(any(org.uberfire.java.nio.file.Path.class),
                               drlArgumentCaptor.capture(),
                               any(CommentedOption.class));

        final String newDrl = drlArgumentCaptor.getValue();
        assertNotNull(newDrl);
        assertTrue(newDrl.contains("MyNewFile"));
    }

    @Test
    public void testRDSLRFile() {
        final Path pathSource = mock(Path.class);
        final Path pathDestination = mock(Path.class);
        when(pathSource.toURI()).thenReturn("file://p0/src/main/resources/MyFile.rdslr");
        when(pathDestination.toURI()).thenReturn("file://p0/src/main/resources/MyNewFile.rdslr");
        when(pathDestination.getFileName()).thenReturn("MyNewFile.rdslr");
        when(ioService.readAllString(any(org.uberfire.java.nio.file.Path.class))).thenReturn(dslr);

        helper.postProcess(pathSource,
                           pathDestination);

        final ArgumentCaptor<String> drlArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(ioService,
               times(1)).write(any(org.uberfire.java.nio.file.Path.class),
                               drlArgumentCaptor.capture(),
                               any(CommentedOption.class));

        final String newDrl = drlArgumentCaptor.getValue();
        assertNotNull(newDrl);
        assertTrue(newDrl.contains("MyNewFile"));
    }
}
