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

package org.dashbuilder.displayer.client;

import org.dashbuilder.displayer.DisplayerSettingsFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.Silent.class)
public class DisplayerRefreshTest extends AbstractDisplayerTest {

    @Test
    public void testRefreshEnabled() {
        AbstractDisplayer displayer = (AbstractDisplayer) displayerLocator.lookupDisplayer(
                DisplayerSettingsFactory.newTableSettings()
                        .dataset(EXPENSES)
                        .refreshOn(10, false)
                        .buildSettings()
        );
        displayer.draw();
        assertEquals(displayer.isRefreshOn(), true);
        verify(displayer.getView()).enableRefreshTimer(10);
        verify(displayer.getView(), never()).cancelRefreshTimer();
    }

    @Test
    public void testRefreshDisabled() {
        AbstractDisplayer displayer = (AbstractDisplayer) displayerLocator.lookupDisplayer(
                DisplayerSettingsFactory.newTableSettings()
                        .dataset(EXPENSES)
                        .refreshOff()
                        .buildSettings()
        );
        displayer.draw();
        assertEquals(displayer.isRefreshOn(), true);
        verify(displayer.getView()).cancelRefreshTimer();
        verify(displayer.getView(), never()).enableRefreshTimer(anyInt());
    }

    @Test
    public void testSwitchRefreshOff() {
        AbstractDisplayer displayer = (AbstractDisplayer) displayerLocator.lookupDisplayer(
                DisplayerSettingsFactory.newTableSettings()
                        .dataset(EXPENSES)
                        .refreshOn(10, false)
                        .buildSettings()
        );
        // Draw is always required ion order to switch refresh on
        displayer.draw();

        // Switch off
        reset(displayer.getView());
        displayer.setRefreshOn(false);
        assertEquals(displayer.isRefreshOn(), false);
        verify(displayer.getView()).cancelRefreshTimer();
    }

    @Test
    public void testSwitchRefreshOn() {
        AbstractDisplayer displayer = (AbstractDisplayer) displayerLocator.lookupDisplayer(
                DisplayerSettingsFactory.newTableSettings()
                        .dataset(EXPENSES)
                        .refreshOn(10, false)
                        .buildSettings()
        );
        // Refresh enabled on draw
        AbstractDisplayer.View view = displayer.getView();
        displayer.draw();
        verify(view).enableRefreshTimer(10);

        // Already on, nothing happens
        reset(view);
        displayer.setRefreshOn(true);
        verify(view, never()).enableRefreshTimer(anyInt());
        verify(view, never()).cancelRefreshTimer();
   }
}