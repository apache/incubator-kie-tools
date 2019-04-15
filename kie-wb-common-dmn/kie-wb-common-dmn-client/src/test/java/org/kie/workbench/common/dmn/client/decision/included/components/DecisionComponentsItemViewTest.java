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

package org.kie.workbench.common.dmn.client.decision.included.components;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.HTMLHeadingElement;
import elemental2.dom.HTMLImageElement;
import elemental2.dom.HTMLParagraphElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;

@RunWith(GwtMockitoTestRunner.class)
public class DecisionComponentsItemViewTest {

    @Mock
    private HTMLImageElement icon;

    @Mock
    private HTMLHeadingElement name;

    @Mock
    private HTMLParagraphElement file;

    @Mock
    private DecisionComponentsItem presenter;

    private DecisionComponentsItemView view;

    @Before
    public void setup() {
        view = new DecisionComponentsItemView(icon, name, file);
        view.init(presenter);
    }

    @Test
    public void testSetIcon() {
        final String iconURI = "http://src.icon.url";
        icon.src = "something";

        view.setIcon(iconURI);

        assertEquals(iconURI, icon.src);
    }

    @Test
    public void testSetName() {
        final String name = "name";
        this.name.textContent = "something";

        view.setName(name);

        assertEquals(name, this.name.textContent);
    }

    @Test
    public void testSetFile() {
        final String file = "file";
        this.file.textContent = "something";

        view.setFile(file);

        assertEquals(file, this.file.textContent);
    }
}
