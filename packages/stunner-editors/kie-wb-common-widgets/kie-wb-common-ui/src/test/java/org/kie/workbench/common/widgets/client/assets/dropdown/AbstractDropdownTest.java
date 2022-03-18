/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.widgets.client.assets.dropdown;

import elemental2.dom.HTMLElement;
import org.mockito.Mock;

import static org.mockito.Mockito.when;

public abstract class AbstractDropdownTest {

    protected final static String KIEASSETSDROPDOWNVIEW_SELECT = "KIEASSETSDROPDOWNVIEW_SELECT";
    protected final static String DEFAULT_VALUE = "DEFAULT_VALUE";

    @Mock
    protected HTMLElement htmlElementMock;

    @Mock
    protected KieAssetsDropdownItem kieAssetsDropdownItemMock;

    protected void commonSetup() {
        when(getViewMock().getElement()).thenReturn(htmlElementMock);
        when(getViewMock().getValue()).thenReturn(DEFAULT_VALUE);
    }

    protected abstract KieAssetsDropdown.View getViewMock();

    protected abstract KieAssetsDropdown getDropdown();
}
