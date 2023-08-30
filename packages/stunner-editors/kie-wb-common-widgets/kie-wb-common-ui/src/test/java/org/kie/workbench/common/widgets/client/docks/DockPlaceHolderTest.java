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

package org.kie.workbench.common.widgets.client.docks;

import com.google.gwt.user.client.ui.IsWidget;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DockPlaceHolderTest {

    @Mock
    private DockPlaceHolderBaseView view;

    @InjectMocks
    private DockPlaceHolder dockPlaceHolder = new DockPlaceHolder();

    @Test
    public void presenterIsSet() {
        dockPlaceHolder.init(view);
        verify(view).setPresenter(dockPlaceHolder);
    }

    @Test
    public void viewIsNotNull() {
        assertEquals(view, dockPlaceHolder.getView());
    }

    @Test
    public void setContent() {
        final IsWidget widget = mock(IsWidget.class);

        dockPlaceHolder.setView(widget);

        verify(view).clear();
        verify(view).setWidget(widget);
    }
}