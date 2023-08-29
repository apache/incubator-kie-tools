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

package org.kie.workbench.common.stunner.client.widgets.explorer.navigator.item;

import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.gwtbootstrap3.client.ui.Image;
import org.gwtbootstrap3.client.ui.PanelBody;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.widgets.explorer.navigator.NavigatorItem;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class NavigatorThumbnailItemViewTest {

    @GwtMock
    private Image thumbImage;

    @GwtMock
    private PanelBody body;

    @Mock
    private NavigatorItem presenter;

    private NavigatorThumbnailItemView navigatorThumbnailItemView;

    @Before
    public void setup() {
        this.navigatorThumbnailItemView = new NavigatorThumbnailItemView();
        this.navigatorThumbnailItemView.init(presenter);
    }

    @Test
    public void checkSetSizeAttachesLoadHandler() {
        navigatorThumbnailItemView.setItemPxSize(100,
                                                 200);

        final ArgumentCaptor<LoadHandler> loadHandlerArgumentCaptor = ArgumentCaptor.forClass(LoadHandler.class);
        when(thumbImage.getWidth()).thenReturn(100);
        when(thumbImage.getHeight()).thenReturn(200);

        verify(thumbImage).addLoadHandler(loadHandlerArgumentCaptor.capture());

        final LoadHandler loadHandler = loadHandlerArgumentCaptor.getValue();
        assertNotNull(loadHandler);

        loadHandler.onLoad(mock(LoadEvent.class));

        verify(body).setPixelSize(eq(100),
                                  eq(200));
    }
}
