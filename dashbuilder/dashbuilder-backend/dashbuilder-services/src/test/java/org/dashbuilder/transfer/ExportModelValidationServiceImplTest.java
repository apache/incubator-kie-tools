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

package org.dashbuilder.transfer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.json.DisplayerSettingsJSONMarshaller;
import org.dashbuilder.navigation.service.PerspectivePluginServices;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.layout.editor.api.editor.LayoutColumn;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.api.editor.LayoutRow;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ExportModelValidationServiceImplTest {

    ExportModelValidationServiceImpl dataTransferExportValidator;

    @Mock
    PerspectivePluginServices perspectivePluginServices;

    @Mock
    DisplayerSettingsJSONMarshaller marshaller;

    @Before
    public void init() {
        dataTransferExportValidator = new ExportModelValidationServiceImpl(perspectivePluginServices, marshaller);
    }

    @Test
    public void testTryingToExportPageWithMissingDataSet() {
        String page1 = "page1";
        String ds1 = "ds1";
        String json1 = "json1";

        DataSetDef def = mock(DataSetDef.class);
        when(def.getUUID()).thenReturn(ds1);
        
        DisplayerSettings ds = registerDSMock(ds1);
        when(marshaller.fromJsonString(json1)).thenReturn(ds);
        
        LayoutTemplate ltForPage1 = createTemplate(json1);
        
        when(perspectivePluginServices.getLayoutTemplate(page1)).thenReturn(ltForPage1);
        
        DataTransferExportModel model = new DataTransferExportModel(Collections.emptyList(),
                                                                    Arrays.asList(page1),
                                                                    true);

        Map<String, List<String>> missingDataSets = dataTransferExportValidator.checkMissingDatasets(model);
        
        assertTrue(missingDataSets.containsKey(page1));
        
        assertEquals(ds1, missingDataSets.get(page1).get(0));
    }
    
    @Test
    public void testSuccessExportWithAllDatasets() {
        String page1 = "page1";
        String ds1 = "ds1";
        String json1 = "json1";

        DataSetDef def = mock(DataSetDef.class);
        when(def.getUUID()).thenReturn(ds1);
        
        DisplayerSettings ds = registerDSMock(ds1);
        when(marshaller.fromJsonString(json1)).thenReturn(ds);
        
        LayoutTemplate ltForPage1 = createTemplate(json1);
        
        when(perspectivePluginServices.getLayoutTemplate(page1)).thenReturn(ltForPage1);
        
        DataTransferExportModel model = new DataTransferExportModel(Arrays.asList(def),
                                                                    Arrays.asList(page1),
                                                                    true);

        Map<String, List<String>> missingDataSets = dataTransferExportValidator.checkMissingDatasets(model);
        
        assertTrue(missingDataSets.isEmpty());
        assertFalse(missingDataSets.containsKey(page1));
    }

    private DisplayerSettings registerDSMock(String ds1) {
        DataSetLookup lookup = mock(DataSetLookup.class);
        when(lookup.getDataSetUUID()).thenReturn(ds1);
        
        DisplayerSettings ds = mock(DisplayerSettings.class);
        when(ds.getDataSetLookup()).thenReturn(lookup);
        
        return ds;
    }

    public LayoutTemplate createTemplate(String...jsonValues) {
        LayoutRow r1 = new LayoutRow();

        LayoutColumn lc1r1 = new LayoutColumn("");
        LayoutComponent cplc1r1 = new LayoutComponent();
        lc1r1.add(cplc1r1);

        LayoutColumn lc2r1 = new LayoutColumn("");
        LayoutComponent cplc2r1 = new LayoutComponent();
        lc2r1.add(cplc2r1);

        r1.add(lc1r1);
        r1.add(lc2r1);

        List<LayoutRow> createdRows = new ArrayList<>();

        for (String jsonValue : jsonValues) {
            LayoutRow row = new LayoutRow();

            LayoutColumn lc1r = new LayoutColumn("");
            LayoutComponent cp1lc1r = new LayoutComponent();
            lc1r.add(cp1lc1r);

            LayoutColumn lc2r = new LayoutColumn("");
            LayoutComponent lc2lc2r = new LayoutComponent();
            lc2lc2r.addProperty("json", jsonValue);
            lc2r.add(lc2lc2r);
            row.add(lc1r);
            row.add(lc2r);
            createdRows.add(row);
        }

        LayoutTemplate lt = new LayoutTemplate("");
        lt.addRow(r1);
        createdRows.forEach(lt::addRow);

        return lt;

    }

}