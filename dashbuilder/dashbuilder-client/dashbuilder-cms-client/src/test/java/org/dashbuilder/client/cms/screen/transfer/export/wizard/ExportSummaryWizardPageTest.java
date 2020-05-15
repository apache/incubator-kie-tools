/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.dashbuilder.client.cms.screen.transfer.export.wizard;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.transfer.DataTransferAssets;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(GwtMockitoTestRunner.class)
public class ExportSummaryWizardPageTest {

    ExportSummaryWizardPage exportWizardSummaryPage;

    @Before
    public void init() {
        exportWizardSummaryPage = new ExportSummaryWizardPage();
    }

    @Test
    public void testRemapMissingDependencies() {
        String UUID = "uuid";
        String NAME = "name";
        String PAGE = "page";

        DataSetDef def1 = new DataSetDef();
        def1.setUUID(UUID);
        def1.setName(NAME);

        HashMap<String, List<String>> validation = new HashMap<>();
        validation.put(PAGE, Collections.singletonList(UUID));

        DataTransferAssets assets = new DataTransferAssets(Collections.singletonList(def1), Collections.emptyList());

        exportWizardSummaryPage.setAssets(assets);
        exportWizardSummaryPage.remapMissingDependencies(validation);
        
        assertEquals(NAME, validation.get(PAGE).get(0));
    }
    
    @Test
    public void testRemapMissingDependenciesWithoutDatasetDef() {
        String UUID = "uuid";
        String PAGE = "page";

        HashMap<String, List<String>> validation = new HashMap<>();
        validation.put(PAGE, Collections.singletonList(UUID));
        exportWizardSummaryPage.remapMissingDependencies(validation);
        
        assertEquals(UUID, validation.get(PAGE).get(0));
    }

}
