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

package org.kie.workbench.common.dmn.client.editors.common.page;

import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import elemental2.dom.DOMTokenList;
import elemental2.dom.Element;
import elemental2.dom.HTMLDivElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.kie.workbench.common.dmn.client.editors.common.page.DMNPage.DMN_PAGE_CSS_CLASS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub(RootPanel.class)
public class DMNPageTest {

    @Mock
    private HTMLDivElement pageView;

    @Mock
    private TranslationService translationService;

    @Mock
    private Element targetElement;

    @Test
    public void testSetupDMNPage() {

        pageView.parentNode = mock(Element.class);
        pageView.parentNode.parentNode = targetElement;
        targetElement.classList = mock(DOMTokenList.class);

        // Constructor triggers the 'setupPageCSSClass'.
        new DMNPage("Title", pageView, translationService) {
        };

        verify(targetElement.classList).add(DMN_PAGE_CSS_CLASS);
    }
}
