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


package org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.impl;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.mvp.Command;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DateTimePickerPresenterTest {

    @Mock
    private DateTimePickerPresenterView view;

    @Mock
    private Command valueChangeCommand;

    @Mock
    private Command hideCommand;

    private DateTimePickerPresenter presenter;

    @Before
    public void init() {
        presenter = spy(new DateTimePickerPresenter(view));

        presenter.init(valueChangeCommand, hideCommand);
    }

    @Test
    public void testLiveCycle() {
        verify(view).init(any());

        presenter.getElement();

        verify(view).getElement();

        presenter.show();

        verify(view).show();

    }

    @Test
    public void testSetterAndGetter() {
        Date date = new Date();

        presenter.setDate(date);

        assertEquals(date,
                     presenter.getDate());
    }

    @Test
    public void testNotifications() {
        Date date = new Date();

        presenter.notifyDateChange(date);

        verify(presenter).setDate(date);

        assertEquals(date,
                     presenter.getDate());

        verify(valueChangeCommand).execute();

        presenter.notifyHide();

        verify(hideCommand).execute();
    }
}
