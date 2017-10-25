/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.ala.ui.client.wizard.pipeline.select.item;

import java.util.ArrayList;
import java.util.Collection;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.guvnor.ala.ui.ProvisioningManagementTestCommons.PIPELINE1;
import static org.guvnor.ala.ui.ProvisioningManagementTestCommons.PIPELINE1_KEY;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class PipelineItemPresenterTest {

    @Mock
    private PipelineItemPresenter.View view;

    private PipelineItemPresenter presenter;

    private Collection<PipelineItemPresenter> others;

    private Collection<PipelineItemPresenter> all;

    private static final int TOTAL_ITEMS = 5;

    @Before
    public void setUp() {
        presenter = new PipelineItemPresenter(view);
        presenter.init();
        verify(view,
               times(1)).init(presenter);

        others = new ArrayList<>();
        for (int i = 0; i < (TOTAL_ITEMS - 1); i++) {
            PipelineItemPresenter itemPresenter;
            itemPresenter = new PipelineItemPresenter(mock(PipelineItemPresenter.View.class));
            others.add(itemPresenter);
        }
        all = new ArrayList<>();
        all.addAll(others);
        all.add(presenter);
    }

    @Test
    public void testSetup() {
        presenter.setup(PIPELINE1_KEY);
        verify(view,
               times(1)).setPipelineName(PIPELINE1);
    }

    @Test
    public void testGetPipeline() {
        presenter.setup(PIPELINE1_KEY);
        assertEquals(PIPELINE1_KEY,
                     presenter.getPipeline());
    }

    @Test
    public void testIsSelected() {
        when(view.isSelected()).thenReturn(true);
        assertTrue(presenter.isSelected());

        when(view.isSelected()).thenReturn(false);
        assertFalse(presenter.isSelected());
    }

    @Test
    public void testPipelineSelected() {
        testSelection(true);
    }

    @Test
    public void testPipelineUnSelected() {
        testSelection(false);
    }

    private void testSelection(boolean selected) {
        presenter.setup(PIPELINE1_KEY);
        presenter.addOthers(all);

        //this item was selected or unselected form the UI
        when(view.isSelected()).thenReturn(selected);
        presenter.onItemClick();

        //all the other elements must have been properly updated.
        others.forEach(other -> {
            if (selected) {
                verify((PipelineItemPresenter.View) other.getView(),
                       times(1)).setSelected(false);
            } else {
                verify((PipelineItemPresenter.View) other.getView(),
                       never()).setSelected(anyBoolean());
            }
        });
        verify((PipelineItemPresenter.View) presenter.getView(),
               never()).setSelected(selected);
    }
}
