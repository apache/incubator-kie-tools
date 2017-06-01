/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.client.views.pfly.widgets;

import org.jboss.errai.common.client.dom.Anchor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PopoverTest {

    @Mock
    Anchor anchor;

    @InjectMocks
    Popover popover;

    @Test
    public void testContent() {
        final String content = "some text";
        popover.setContent(content);

        verify(anchor).setAttribute("data-content",
                                    content);
    }

    @Test
    public void testTitle() {
        final String title = "content";
        popover.setTitle(title);

        verify(anchor).setTitle(title);
    }

    @Test
    public void testContainer() {
        final String container = "body";
        popover.setContainer(container);

        verify(anchor).setAttribute("data-container",
                                    container);
    }

    @Test
    public void testHtml() {
        popover.setHtml(true);

        verify(anchor).setAttribute("data-html",
                                    "true");
    }

    @Test
    public void testPlacement() {
        final String placement = "top";
        popover.setPlacement(placement);

        verify(anchor).setAttribute("data-placement",
                                    placement);
    }

    @Test
    public void testTrigger() {
        final String trigger = "focus";
        popover.setTrigger(trigger);

        verify(anchor).setAttribute("data-trigger",
                                    trigger);
    }

    @Test
    public void testTemplate() {
        final String template = "<div></div>";
        popover.setTemplate(template);

        verify(anchor).setAttribute("data-template",
                                    template);
    }

}
