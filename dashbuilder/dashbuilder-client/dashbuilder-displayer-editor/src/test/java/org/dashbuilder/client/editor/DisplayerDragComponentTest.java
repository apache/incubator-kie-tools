/*
 * Copyright 2018 JBoss, by Red Hat, Inc
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
package org.dashbuilder.client.editor;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.DisplayerType;
import org.dashbuilder.displayer.client.widgets.DisplayerEditorPopup;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.ext.layout.editor.client.api.ModalConfigurationContext;

import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class DisplayerDragComponentTest {

    @InjectMocks
    DisplayerDragComponent displayerDragComponent;

    @Mock
    SyncBeanManager beanManager;

    @Mock
    SyncBeanDef<DisplayerEditorPopup> editorBeanDef;

    @Mock
    DisplayerEditorPopup editorPopup;

    @Mock
    ModalConfigurationContext configurationContext;

    @Mock
    Map<String,String> configurationProperties;

    @InjectMocks
    LineChartDragComponent lineChartDragComponent;

    @Before
    public void setUp(){
        when(configurationContext.getComponentProperties()).thenReturn(configurationProperties);
        when(beanManager.lookupBean(DisplayerEditorPopup.class)).thenReturn(editorBeanDef);
        when(editorBeanDef.newInstance()).thenReturn(editorPopup);
    }

    @Test
    public void testDefaultDisplayerType() {
        assertEquals(lineChartDragComponent.getDisplayerType(), DisplayerType.LINECHART);
        assertEquals(lineChartDragComponent.getDisplayerSubType(), null);

        lineChartDragComponent.getConfigurationModal(configurationContext);
        verify(editorPopup).setDisplayerType(DisplayerType.LINECHART);
        verify(editorPopup, never()).setDisplayerSubType(any());
    }

    @Test
    public void testDoNotSetDisplayerType() {
        when(configurationProperties.get("json")).thenReturn("");
        lineChartDragComponent.getConfigurationModal(configurationContext);
        verify(editorPopup, never()).setDisplayerType(any());
        verify(editorPopup, never()).setDisplayerSubType(any());
    }

    @Test
    public void testAdjustSize(){
        final DisplayerSettings settings = mock(DisplayerSettings.class);
        when(settings.getChartWidth()).thenReturn(0);
        when(settings.getTableWidth()).thenReturn(0);

        displayerDragComponent.adjustSize(settings, 0);

        verify(settings).setTableWidth(0);

        displayerDragComponent.adjustSize(settings, 30);

        verify(settings).setTableWidth(10);
    }

}
