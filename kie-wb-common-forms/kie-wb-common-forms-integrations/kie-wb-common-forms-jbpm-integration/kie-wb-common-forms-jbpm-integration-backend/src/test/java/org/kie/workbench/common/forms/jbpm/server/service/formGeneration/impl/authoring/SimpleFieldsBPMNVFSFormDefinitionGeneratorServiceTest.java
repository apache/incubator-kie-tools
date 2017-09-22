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

package org.kie.workbench.common.forms.jbpm.server.service.formGeneration.impl.authoring;

import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SimpleFieldsBPMNVFSFormDefinitionGeneratorServiceTest extends BPMNVFSFormDefinitionGeneratorServiceTest {

    @Test
    public void testCreateNewProcessFormSimpleVariables() {

        when(ioService.exists(any())).thenReturn(false);

        checkSimpleVariableForms();
    }

    @Test
    public void testCreateExistingProcessFormSimpleVariables() throws IOException {

        when(ioService.exists(any())).thenReturn(true);

        when(ioService.readAllString(any())).thenReturn(IOUtils.toString(new InputStreamReader(this.getClass().getResourceAsStream("/forms/simpleform.frm"))));

        checkSimpleVariableForms();
    }

    @After
    public void afterTest() {

        verify(ioService,
               never()).write(any(),
                              anyString(),
                              any());
        verify(formFinderService,
               never()).findFormsForType(any(),
                                         any());
    }
}
