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

package org.kie.workbench.common.dmn.client.editors.included.grid;

import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.CSSStyleDeclaration;
import elemental2.dom.HTMLAnchorElement;
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
    protected HTMLAnchorElement pathLink;

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
        ((BaseCardComponentContentView) view).onRemoveButtonClick(mock(ClickEvent.class));

        verify(presenter).remove();
    }

    @Test
    public void testOnPathLinkAnchorClick() {
        ((BaseCardComponentContentView) view).onPathLinkClick(mock(ClickEvent.class));

        verify(presenter).openPathLink();
    }

    @Test
    public void testSetPath() {
        final String path = "path";
        this.path.textContent = "something";
        this.pathLink.style = new CSSStyleDeclaration();

        view.setPath(path);

        assertEquals(path, this.path.textContent);
        assertEquals("none", this.pathLink.style.display);
    }

    @Test
    public void testSetPathLink() {
        final String path = "path";
        this.pathLink.textContent = "something";
        this.path.style = new CSSStyleDeclaration();

        view.setPathLink(path);

        assertEquals(path, this.pathLink.textContent);
        assertEquals("none", this.path.style.display);
    }
}
