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
package org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.common;

import com.google.gwt.dom.client.Element;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(GwtMockitoTestRunner.class)
public class DecisionTablePopoverUtilsTest {

    @Mock
    private Element element;

    @Mock
    private HTMLElement htmlElement;

    private DecisionTablePopoverUtils popoverUtils;

    @Before
    public void setup() {
        this.popoverUtils = new DecisionTablePopoverUtils();
    }

    @Test
    public void checkElementRegistration() {
        popoverUtils.setupPopover(element,
                                  "hello");

        assertTrue(popoverUtils.getPopoverElementRegistrations().isEmpty());

        popoverUtils.setupAndRegisterPopover(element,
                                             "hello");

        assertFalse(popoverUtils.getPopoverElementRegistrations().isEmpty());
        assertEquals(1,
                     popoverUtils.getPopoverElementRegistrations().size());

        popoverUtils.destroyPopovers();

        assertTrue(popoverUtils.getPopoverElementRegistrations().isEmpty());
    }

    @Test
    public void checkHTMLElementRegistration() {
        popoverUtils.setupPopover(htmlElement,
                                  "hello");

        assertTrue(popoverUtils.getPopoverHTMLElementRegistrations().isEmpty());

        popoverUtils.setupAndRegisterPopover(htmlElement,
                                             "hello");

        assertFalse(popoverUtils.getPopoverHTMLElementRegistrations().isEmpty());
        assertEquals(1,
                     popoverUtils.getPopoverHTMLElementRegistrations().size());

        popoverUtils.destroyPopovers();

        assertTrue(popoverUtils.getPopoverHTMLElementRegistrations().isEmpty());
    }
}
