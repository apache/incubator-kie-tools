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

package org.dashbuilder.client.editor;

import java.util.Map;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.GlobalDisplayerSettings;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DisplayerDragComponentTest {

    @InjectMocks
    DisplayerDragComponent displayerDragComponent;

    @Mock
    SyncBeanManager beanManager;

    @Mock
    Map<String,String> configurationProperties;
    
    @Mock
    LayoutComponent layoutComponent;
    
    @Mock
    GlobalDisplayerSettings globalDisplayerSettings;

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
