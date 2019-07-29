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

package org.kie.workbench.common.dmn.client.editors.documentation.links;

import java.util.Optional;
import java.util.function.Consumer;

import org.jboss.errai.common.client.dom.HTMLElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.property.dmn.DMNExternalLink;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NameAndUriPopoverImplTest {

    @Mock
    private NameAndUrlPopoverView view;

    private NameAndUriPopoverImpl popover;

    @Before
    public void setup() {
        popover = new NameAndUriPopoverImpl(view);
    }

    @Test
    public void testInit() {
        popover.init();
        verify(view).init(popover);
    }

    @Test
    public void testGetElement() {

        final HTMLElement element = mock(HTMLElement.class);

        when(view.getElement()).thenReturn(element);

        final HTMLElement actual = popover.getElement();

        assertEquals(element, actual);
    }

    @Test
    public void testShow() {

        final Optional<String> editorTitle = Optional.of("title");

        popover.show(editorTitle);

        verify(view).show(editorTitle);
    }

    @Test
    public void testHide() {

        popover.hide();

        verify(view).hide();
    }

    @Test
    public void testSetOnExternalLinkCreated() {

        final Consumer<DMNExternalLink> consumer = mock(Consumer.class);

        popover.setOnExternalLinkCreated(consumer);

        verify(view).setOnExternalLinkCreated(consumer);
    }
}