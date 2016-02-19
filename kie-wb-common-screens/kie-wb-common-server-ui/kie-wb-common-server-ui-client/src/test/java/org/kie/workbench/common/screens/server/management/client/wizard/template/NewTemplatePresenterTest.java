/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.server.management.client.wizard.template;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class NewTemplatePresenterTest {

    @Mock
    NewTemplatePresenter.View view;

    @InjectMocks
    NewTemplatePresenter presenter;

    @Test
    public void testInit() {
        presenter.init();

        verify(view).init(presenter);
        assertEquals(view.asWidget(), presenter.asWidget());
    }

    @Test
    public void testIsTemplateNameValid() {
        when(view.getTemplateName())
                .thenReturn(null)
                .thenReturn("")
                .thenReturn("test");

        assertFalse(presenter.isTemplateNameValid());
        assertFalse(presenter.isTemplateNameValid());
        assertTrue(presenter.isTemplateNameValid());
    }

    @Test
    public void testIsCapabilityValid() {
        when(view.isPlanningCapabilityChecked()).thenReturn(true);
        when(view.isRuleCapabilityChecked()).thenReturn(true);
        when(view.isProcessCapabilityChecked()).thenReturn(true);

        assertTrue(presenter.isCapabilityValid());

        when(view.isPlanningCapabilityChecked()).thenReturn(false);
        when(view.isRuleCapabilityChecked()).thenReturn(true);
        when(view.isProcessCapabilityChecked()).thenReturn(true);

        assertTrue(presenter.isCapabilityValid());

        when(view.isPlanningCapabilityChecked()).thenReturn(true);
        when(view.isRuleCapabilityChecked()).thenReturn(false);
        when(view.isProcessCapabilityChecked()).thenReturn(true);

        assertTrue(presenter.isCapabilityValid());

        when(view.isPlanningCapabilityChecked()).thenReturn(false);
        when(view.isRuleCapabilityChecked()).thenReturn(false);
        when(view.isProcessCapabilityChecked()).thenReturn(false);

        assertFalse(presenter.isCapabilityValid());
    }

    @Test
    public void testClear() {
        presenter.clear();
        verify(view).clear();
    }

}