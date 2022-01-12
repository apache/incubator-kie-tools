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

package org.kie.workbench.common.dmn.client.editors.included.grid;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.HTMLElement;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;

@RunWith(GwtMockitoTestRunner.class)
public class DMNCardComponentContentViewTest extends BaseCardComponentContentViewTest<DMNCardComponentContentView> {

    @Mock
    private HTMLElement dataTypesCount;

    @Mock
    private HTMLElement drgElementsCount;

    @Override
    protected DMNCardComponentContentView getCardView() {
        return new DMNCardComponentContentView(path, pathLink, dataTypesCount, drgElementsCount, removeButton);
    }

    @Test
    public void testSetDataTypesCount() {
        dataTypesCount.textContent = "something";

        view.setDataTypesCount(123);

        assertEquals("123", dataTypesCount.textContent);
    }

    @Test
    public void testSetDrgElementsCount() {
        drgElementsCount.textContent = "something";

        view.setDrgElementsCount(456);

        assertEquals("456", drgElementsCount.textContent);
    }
}
