/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.widget;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.guided.dtable.shared.model.DTColumnConfig52;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.mockito.Mock;

import java.util.HashMap;

import static org.junit.Assert.assertTrue;

@RunWith(GwtMockitoTestRunner.class)
public class DTCellValueWidgetFactoryTest {

    @Mock
    DTColumnConfig52 column;

    @Before
    public void setUp() throws Exception {
        ApplicationPreferences.setUp(new HashMap<String, String>() {{
            put(ApplicationPreferences.DATE_FORMAT,
                "dd-MM-yyyy");
        }});
    }

    @Test
    public void testGetHideColumnIndicatorChecked() throws Exception {
        assertTrue(DTCellValueWidgetFactory.getHideColumnIndicator(column) instanceof HideColumnCheckBox);
    }
}
