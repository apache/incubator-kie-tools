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

package org.guvnor.ala.ui.openshift.client.pipeline.template.table;

import java.util.List;

import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import org.guvnor.ala.ui.openshift.model.TemplateParam;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TemplateParamsTablePresenterTest {

    private static final String OLD_VALUE = "OLD_VALUE";

    private static final String NEW_VALUE = "NEW_VALUE";

    private static final String PARAM_NAME = "PARAM_NAME";

    @Mock
    private TemplateParamsTablePresenter.View view;

    @Mock
    private ListDataProvider<TemplateParam> dataProvider;

    @Mock
    private List<TemplateParam> dataProviderList;

    @Mock
    private HasData<TemplateParam> dataDisplay;

    private TemplateParamsTablePresenter presenter;

    @Mock
    private TemplateParamsTablePresenter.ParamChangeHandler changeHandler;

    @Before
    public void setUp() {
        when(view.getDisplay()).thenReturn(dataDisplay);
        when(dataProvider.getList()).thenReturn(dataProviderList);

        presenter = new TemplateParamsTablePresenter(view) {
            @Override
            ListDataProvider<TemplateParam> createDataProvider() {
                return TemplateParamsTablePresenterTest.this.dataProvider;
            }
        };
        presenter.init();
        verify(view,
               times(1)).init(presenter);
        verify(dataProvider,
               times(1)).addDataDisplay(dataDisplay);
    }

    @Test
    public void testSetItems() {
        @SuppressWarnings("unchecked")
        List<TemplateParam> items = mock(List.class);

        presenter.setItems(items);

        verify(dataProviderList,
               times(1)).clear();
        verify(dataProviderList,
               times(1)).addAll(items);
        verify(dataProvider,
               times(1)).flush();
    }

    @Test
    public void testClear() {
        presenter.clear();
        verify(dataProviderList,
               times(1)).clear();
        verify(dataProvider,
               times(1)).flush();
    }

    @Test
    public void testGetView() {
        assertEquals(view,
                     presenter.getView());
    }

    @Test
    public void testOnParamChange() {
        presenter.setParamChangeHandler(changeHandler);
        presenter.onParamChange(PARAM_NAME,
                                NEW_VALUE,
                                OLD_VALUE);
        verify(changeHandler,
               times(1)).onParamChange(PARAM_NAME,
                                       NEW_VALUE,
                                       OLD_VALUE);
    }
}
