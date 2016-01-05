/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.client.views.pfly.listbar;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.client.workbench.PanelManager;
import org.uberfire.commons.data.Pair;
import org.uberfire.workbench.model.PartDefinition;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith( GwtMockitoTestRunner.class )
public class ListBarWidgetImplTest {

    ListBarWidgetImpl listBar;

    @Before
    public void setUp() throws Exception {
        listBar = new ListBarWidgetImpl() {
            @Override
            void setupContextMenu() {
            }
        };
    }

    @Test
    public void onSelectPartOnPartHiddenEventIsFired() {

        final PartDefinition selectedPart = mock( PartDefinition.class );
        final PartDefinition currentPart = mock( PartDefinition.class );

        listBar.panelManager = mock( PanelManager.class );
        listBar.partContentView.put( selectedPart, new FlowPanel() );
        listBar.parts.add( selectedPart );
        listBar.currentPart = Pair.newPair( currentPart, new FlowPanel() );
        listBar.partContentView.put( currentPart, new FlowPanel() );
        listBar.titleDropDown = mock( PartListDropdown.class );

        listBar.selectPart( selectedPart );

        verify( listBar.panelManager ).onPartHidden( currentPart );

    }


}