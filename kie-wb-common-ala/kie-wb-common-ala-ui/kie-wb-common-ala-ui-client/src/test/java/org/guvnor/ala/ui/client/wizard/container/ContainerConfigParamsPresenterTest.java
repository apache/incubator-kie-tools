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

package org.guvnor.ala.ui.client.wizard.container;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import org.guvnor.ala.ui.client.util.ContentChangeHandler;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;

import static org.guvnor.ala.AlaSPITestCommons.mockList;
import static org.guvnor.ala.ui.client.resources.i18n.GuvnorAlaUIConstants.ContainerConfigParamsPresenter_AddContainerPopupTitle;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ContainerConfigParamsPresenterTest {

    private static final int CONTAINER_CONFIG_COUNT = 10;

    private static final String TITLE = "TITLE";

    private static final String CONTAINER_NAME_VALUE = "CONTAINER_NAME_VALUE";

    private static final String GROUP_ID_VALUE = "GROUP_ID_VALUE";

    private static final String ARTIFACT_ID_VALUE = "ARTIFACT_ID_VALUE";

    private static final String VERSION_VALUE = "VERSION_VALUE";

    @Mock
    private ContainerConfigParamsPresenter.View view;

    @Mock
    private ContainerConfigPopup containerConfigPopup;

    @Mock
    private EventSourceMock<ContainerConfigParamsChangeEvent> configParamsChangeEvent;

    @Mock
    private TranslationService translationService;

    private ContainerConfigParamsPresenter presenter;

    @Mock
    private ListDataProvider<ContainerConfig> dataProvider;

    @Mock
    private List<ContainerConfig> dataProviderList;

    @Mock
    private HasData<ContainerConfig> dataGrid;

    private ArgumentCaptor<ParameterizedCommand> okCommandCaptor;

    private ArgumentCaptor<Command> cancelCommandCaptor;

    @Mock
    private ContentChangeHandler contentChangeHandler;

    @Before
    public void setUp() {
        okCommandCaptor = ArgumentCaptor.forClass(ParameterizedCommand.class);
        cancelCommandCaptor = ArgumentCaptor.forClass(Command.class);
        presenter = new ContainerConfigParamsPresenter(view,
                                                       containerConfigPopup,
                                                       configParamsChangeEvent,
                                                       translationService) {
            @Override
            ListDataProvider<ContainerConfig> createDataProvider() {
                return ContainerConfigParamsPresenterTest.this.dataProvider;
            }
        };
        presenter.addContentChangeHandler(contentChangeHandler);
        when(dataProvider.getList()).thenReturn(dataProviderList);
        when(view.getDisplay()).thenReturn(dataGrid);
        presenter.init();
        verify(view,
               times(1)).init(presenter);
        verify(view,
               times(1)).getDisplay();
        verify(dataProvider,
               times(1)).addDataDisplay(dataGrid);
    }

    @Test
    public void testGetView() {
        assertEquals(view,
                     presenter.getView());
    }

    @Test
    public void testPrepareView() {
        presenter.prepareView();
        verify(dataProvider,
               times(1)).refresh();
    }

    @Test
    public void testClear() {
        presenter.clear();
        verify(dataProviderList,
               times(1)).clear();
    }

    @Test
    public void testIsComplete() {
        when(dataProviderList.isEmpty()).thenReturn(true);
        presenter.isComplete(Assert::assertFalse);

        when(dataProviderList.isEmpty()).thenReturn(false);
        presenter.isComplete(Assert::assertTrue);
    }

    @Test
    public void testOnGetWizardTitle() {
        when(view.getWizardTitle()).thenReturn(TITLE);
        assertEquals(TITLE,
                     presenter.getWizardTitle());
    }

    @Test
    public void testOnAddContainerPopupConfirmed() {
        List<ContainerConfig> currentElements = new ArrayList<>();
        when(dataProvider.getList()).thenReturn(currentElements);
        //the add container popup is called
        prepareAndCallPopup();

        ContainerConfig returnedConfig = new ContainerConfig(CONTAINER_NAME_VALUE,
                                                             GROUP_ID_VALUE,
                                                             ARTIFACT_ID_VALUE,
                                                             VERSION_VALUE);
        //the popup was properly completed with a ContainerConfig.
        okCommandCaptor.getValue().execute(returnedConfig);

        //the container config was added to the list.
        assertTrue(currentElements.contains(returnedConfig));
        verify(configParamsChangeEvent,
               times(1)).fire(new ContainerConfigParamsChangeEvent(currentElements));
        verify(contentChangeHandler,
               times(1)).onContentChange();
    }

    @Test
    public void testOnAddContainerPopupCanceled() {
        List<ContainerConfig> currentElements = new ArrayList<>();
        when(dataProvider.getList()).thenReturn(currentElements);
        int originalSize = currentElements.size();

        //the add container popup is called
        prepareAndCallPopup();

        //the popup was canceled.
        cancelCommandCaptor.getValue().execute();

        //no elements were added to the list.
        assertEquals(originalSize,
                     currentElements.size());
        verify(configParamsChangeEvent,
               never()).fire(any());
        verify(contentChangeHandler,
               never()).onContentChange();
    }

    private void prepareAndCallPopup() {
        when(translationService.getTranslation(ContainerConfigParamsPresenter_AddContainerPopupTitle)).thenReturn(TITLE);
        presenter.onAddContainer();
        verify(containerConfigPopup,
               times(1)).show(eq(TITLE),
                              okCommandCaptor.capture(),
                              cancelCommandCaptor.capture(),
                              anyList());
    }

    @Test
    public void testOnDeleteContainer() {
        //mock an arbitrary set of configs.
        List<ContainerConfig> currentElements = mockList(ContainerConfig.class,
                                                         CONTAINER_CONFIG_COUNT);
        when(dataProvider.getList()).thenReturn(currentElements);

        //pick an arbitrary element for deletion.
        int originalSize = currentElements.size();
        int index = 4;
        ContainerConfig arbitraryElement = currentElements.get(index);
        presenter.onDeleteContainer(arbitraryElement);

        assertEquals(originalSize - 1,
                     currentElements.size());
        assertFalse(currentElements.contains(arbitraryElement));
        verify(configParamsChangeEvent,
               times(1)).fire(new ContainerConfigParamsChangeEvent(currentElements));
        verify(contentChangeHandler,
               times(1)).onContentChange();
    }
}
