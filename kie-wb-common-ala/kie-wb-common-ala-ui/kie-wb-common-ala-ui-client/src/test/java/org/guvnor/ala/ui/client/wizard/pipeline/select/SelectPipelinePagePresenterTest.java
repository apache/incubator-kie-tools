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

package org.guvnor.ala.ui.client.wizard.pipeline.select;

import java.util.ArrayList;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.ala.ui.client.util.ContentChangeHandler;
import org.guvnor.ala.ui.client.wizard.pipeline.select.item.PipelineItemPresenter;
import org.guvnor.ala.ui.model.PipelineKey;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageStatusChangeEvent;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class SelectPipelinePagePresenterTest {

    @Mock
    private SelectPipelinePagePresenter.View view;

    @Mock
    private EventSourceMock<WizardPageStatusChangeEvent> wizardPageStatusChangeEvent;

    @Mock
    private ManagedInstance<PipelineItemPresenter> itemPresenterInstance;

    private SelectPipelinePagePresenter presenter;

    private List<PipelineKey> pipelines;

    private static int PIPELINES_COUNT = 3;

    @Before
    public void setUp() {
        pipelines = new ArrayList<>();
        for (int i = 0; i < PIPELINES_COUNT; i++) {
            pipelines.add(new PipelineKey("Pipeline." + i));
        }
        presenter = new SelectPipelinePagePresenter(view,
                                                    wizardPageStatusChangeEvent,

                                                    itemPresenterInstance) {

            protected int currentPipeline = 0;

            @Override
            protected PipelineItemPresenter newItemPresenter() {
                PipelineItemPresenter itemPresenter = mock(PipelineItemPresenter.class);
                when(itemPresenter.getView()).thenReturn(mock(IsElement.class));
                when(itemPresenter.getPipeline()).thenReturn(pipelines.get(currentPipeline));
                when(itemPresenterInstance.get()).thenReturn(itemPresenter);
                currentPipeline++;
                return super.newItemPresenter();
            }
        };
        presenter.init();
        verify(view,
               times(1)).init(presenter);
    }

    @Test
    public void testSetup() {
        presenter.setup(pipelines);
        verify(view,
               times(1)).clear();
        assertEquals(pipelines.size(),
                     presenter.getItemPresenters().size());
        verify(itemPresenterInstance,
               times(pipelines.size())).get();
        for (int i = 0; i < presenter.getItemPresenters().size(); i++) {
            PipelineItemPresenter itemPresenter = presenter.getItemPresenters().get(i);
            verify(itemPresenter,
                   times(1)).setup(pipelines.get(i));
            verify(itemPresenter,
                   times(1)).addContentChangeHandler(any(ContentChangeHandler.class));
            verify(view,
                   times(1)).addPipelineItem(itemPresenter.getView());
            verify(itemPresenter,
                   times(1)).addOthers(presenter.getItemPresenters());
        }
    }

    @Test
    public void testPageNotCompleted() {
        presenter.setup(pipelines);
        presenter.getItemPresenters().forEach(itemPresenter -> when(itemPresenter.isSelected()).thenReturn(false));
        //no item is selected.
        presenter.isComplete(Assert::assertFalse);
    }

    @Test
    public void testPageCompleted() {
        presenter.setup(pipelines);
        presenter.getItemPresenters().forEach(itemPresenter -> when(itemPresenter.isSelected()).thenReturn(false));
        //let a pipeline be selected
        int selectedIndex = 1;
        when(presenter.getItemPresenters().get(selectedIndex).isSelected()).thenReturn(true);

        //a pipeline is selected, so the page must be completed.
        presenter.isComplete(Assert::assertTrue);
        assertEquals(pipelines.get(selectedIndex),
                     presenter.getPipeline());
    }
}
