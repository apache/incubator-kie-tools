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

package org.dashbuilder.displayer.client.widgets;

import java.util.Arrays;
import java.util.List;

import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.DisplayerSettingsFactory;
import org.dashbuilder.displayer.DisplayerSubType;
import org.dashbuilder.displayer.DisplayerType;
import org.dashbuilder.displayer.client.AbstractRendererLibrary;
import org.dashbuilder.displayer.client.Displayer;
import org.dashbuilder.displayer.client.RendererLibrary;
import org.dashbuilder.displayer.client.RendererManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.mvp.Command;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RendererSelectorTest {

    @Mock
    RendererSelector.RadioListView radioListView;

    @Mock
    RendererSelector.TabListView tabListView;

    @Mock
    RendererSelector.ListBoxView listBoxView;

    @Mock
    RendererManager rendererManager;

    @Mock
    Command selectCommand;

    RendererLibMock gwtLib = new RendererLibMock("gwt");
    RendererLibMock d3Lib = new RendererLibMock("d3");
    RendererLibMock lienzoLib = new RendererLibMock("lienzo");

    DisplayerSettings settings = DisplayerSettingsFactory
            .newPieChartSettings()
            .renderer("d3")
            .buildSettings();

    RendererSelector presenter;

    class RendererLibMock extends AbstractRendererLibrary {

        String name;

        public RendererLibMock(String name) {
            this.name = name;
        }

        @Override
        public String getUUID() {
            return null;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public List<DisplayerType> getSupportedTypes() {
            return null;
        }

        @Override
        public List<DisplayerSubType> getSupportedSubtypes(DisplayerType displayerType) {
            return null;
        }

        @Override
        public Displayer lookupDisplayer(DisplayerSettings displayer) {
            return null;
        }
    }

    @Before
    public void init() {
        presenter = new RendererSelector(tabListView, listBoxView, radioListView, rendererManager);
    }

    @Test
    public void testSingleRenderer() {
        List<RendererLibrary> rendererLibs = Arrays.asList((RendererLibrary) d3Lib);
        when(rendererManager.getRendererForDisplayer(settings)).thenReturn(d3Lib);

        presenter.init(settings, RendererSelector.SelectorType.TAB, 300, selectCommand);

        verify(tabListView).setVisible(false);
        verify(tabListView, never()).setWidth(anyInt());
        verify(tabListView, never()).clearRendererSelector();
        verify(tabListView, never()).addRendererItem(anyString());
        verify(tabListView, never()).setSelectedRendererIndex(anyInt());

        verifyZeroInteractions(listBoxView, radioListView);
    }

    @Test
    public void testMultipleRenderers() {
        List<RendererLibrary> rendererLibs = Arrays.asList((RendererLibrary) gwtLib, d3Lib, lienzoLib);
        when(rendererManager.getRenderersForType(any(), any())).thenReturn(rendererLibs);
        when(rendererManager.getRendererForDisplayer(settings)).thenReturn(d3Lib);

        presenter.init(settings, RendererSelector.SelectorType.LIST, 300, selectCommand);

        verify(listBoxView).setVisible(true);
        verify(listBoxView).setWidth(300);
        verify(listBoxView).clearRendererSelector();
        verify(listBoxView).addRendererItem("gwt");
        verify(listBoxView).addRendererItem("d3");
        verify(listBoxView).addRendererItem("lienzo");
        verify(listBoxView).setSelectedRendererIndex(1);

        verifyZeroInteractions(tabListView, radioListView);
    }

    @Test
    public void testOnSelect() {
        List<RendererLibrary> rendererLibs = Arrays.asList((RendererLibrary) gwtLib, d3Lib);
        when(rendererManager.getRendererForDisplayer(settings)).thenReturn(d3Lib);
        when(rendererManager.getRendererByName(gwtLib.getName())).thenReturn(gwtLib);
        when(tabListView.getRendererSelected()).thenReturn(gwtLib.getName());

        presenter.init(settings, RendererSelector.SelectorType.TAB, 300, selectCommand);
        presenter.onRendererSelected();

        verify(selectCommand).execute();
        assertEquals(presenter.getRendererLibrary(), gwtLib);
    }
}