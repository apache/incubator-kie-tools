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

package org.kie.workbench.common.dmn.client.widgets.decisionservice.parameters.parametergroup;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.DOMTokenList;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLUListElement;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.widgets.decisionservice.parameters.parametergroup.ParameterGroup.ParameterItem;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.kie.workbench.common.dmn.client.editors.types.common.HiddenHelper.HIDDEN_CSS_CLASS;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ParameterGroupTest {

    @Mock
    private HTMLDivElement groupHeader;

    @Mock
    private HTMLUListElement parameters;

    @Mock
    private ManagedInstance<ParameterItem> parameterItems;

    @Mock
    private Elemental2DomUtil util;

    @Mock
    private HTMLDivElement none;

    @Mock
    private DOMTokenList classList;

    private ParameterGroup parameterGroup;

    @Before
    public void setup() {

        none.classList = classList;
        parameterGroup = spy(new ParameterGroup(groupHeader,
                                                parameters,
                                                parameterItems,
                                                util,
                                                none));
    }

    @Test
    public void testSetHeader() {

        final String headerText = "text";

        parameterGroup.setHeader(headerText);

        assertEquals(groupHeader.textContent, headerText);
    }

    @Test
    public void testAddParameter() {

        final String name = "name";
        final String type = "type";
        final HTMLElement htmlElement = mock(HTMLElement.class);
        final ParameterItem parameterItem = mock(ParameterItem.class);
        final elemental2.dom.HTMLElement element = mock(elemental2.dom.HTMLElement.class);

        doReturn(parameterItem).when(parameterGroup).createParameterItem();
        when(parameterItem.getElement()).thenReturn(htmlElement);
        when(util.asHTMLElement(htmlElement)).thenReturn(element);

        parameterGroup.addParameter(name, type);

        verify(util).asHTMLElement(htmlElement);
        verify(parameters).appendChild(element);
        verify(parameterGroup).refreshNone();
        assertFalse(parameterGroup.isEmpty());
    }

    @Test
    public void testRefreshNone() {

        doReturn(true).when(parameterGroup).isEmpty();

        parameterGroup.refreshNone();

        verify(classList).remove(HIDDEN_CSS_CLASS);

        doReturn(false).when(parameterGroup).isEmpty();

        parameterGroup.refreshNone();

        verify(classList).add(HIDDEN_CSS_CLASS);
    }
}
