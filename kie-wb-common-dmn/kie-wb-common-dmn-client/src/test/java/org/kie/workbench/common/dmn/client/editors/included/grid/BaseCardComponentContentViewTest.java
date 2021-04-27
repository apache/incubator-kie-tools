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

import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLParagraphElement;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public abstract class BaseCardComponentContentViewTest<V extends BaseCardComponent.ContentView> {

    @Mock
    protected HTMLParagraphElement path;

    @Mock
    protected HTMLButtonElement removeButton;

    @Mock
    protected DMNCardComponent presenter;

    protected V view;

    protected abstract V getCardView();

    @Before
    public void setup() {
        view = getCardView();
        view.init(presenter);
    }

    @Test
    public void testOnRemoveButtonClick() {
        view.onRemoveButtonClick(mock(ClickEvent.class));

        verify(presenter).remove();
    }

    @Test
    public void testSetPath() {
        final String path = "path";
        this.path.textContent = "something";

        view.setPath(path);

        assertEquals(path, this.path.textContent);
    }
}
