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

package org.drools.workbench.screens.scenariosimulation.client.rightpanel;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.FACT_NAME;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.FIELD_NAME;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.FULL_PROPERTY_PATH;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class FieldItemViewTest extends AbstractTestToolsTest {

    private FieldItemViewImpl fieldItemView;

    private String INNER_HTML;
    private String ID_ATTRIBUTE;

    @Before
    public void setup() {
        super.setup();
        INNER_HTML = "<a>" + FIELD_NAME + "</a> [" + FACT_MODEL_TREE.getFactName() + "]";
        ID_ATTRIBUTE = "fieldElement-" + FACT_NAME + "-" + FIELD_NAME;
        this.fieldItemView = spy(new FieldItemViewImpl() {
            {
                this.fieldElement = lIElementMock;
            }
        });
    }

    @Test
    public void setFieldData() {
        fieldItemView.setFieldData(FULL_PROPERTY_PATH, FACT_NAME, FIELD_NAME, FACT_MODEL_TREE.getFactName());
        verify(lIElementMock, times(1)).setInnerHTML(eq(INNER_HTML));
        verify(lIElementMock, times(1)).setAttribute(eq("id"), eq(ID_ATTRIBUTE));
        verify(lIElementMock, times(1)).setAttribute(eq("fieldName"), eq(FIELD_NAME));
        verify(lIElementMock, times(1)).setAttribute(eq("className"), eq(FACT_MODEL_TREE.getFactName()));
        verify(lIElementMock, times(1)).setAttribute(eq("fullPath"), eq(FULL_PROPERTY_PATH));
    }
}