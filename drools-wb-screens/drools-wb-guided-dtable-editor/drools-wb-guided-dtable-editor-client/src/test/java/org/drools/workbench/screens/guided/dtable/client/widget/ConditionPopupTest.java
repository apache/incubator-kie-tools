/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.widget;

import java.util.HashMap;

import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.dtable.client.resources.images.GuidedDecisionTableImageResources508;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.drools.workbench.screens.guided.rule.client.editor.CEPWindowOperatorsDropdown;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.html.Text;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub({ Text.class, GuidedDecisionTableImageResources508.class, CEPWindowOperatorsDropdown.class })
public class ConditionPopupTest {

    @Mock
    private AsyncPackageDataModelOracle oracle;

    @Mock
    private GuidedDecisionTableView.Presenter presenter;

    @Mock
    private ConditionColumnCommand refreshGrid;

    private ConditionPopup popup;

    @BeforeClass
    public static void setupApplicationPreferences() {
        ApplicationPreferences.setUp( new HashMap<String, String>() {{
            put( ApplicationPreferences.DATE_FORMAT,
                 "dd-MM-yyyy" );
        }} );
    }

    private void setup( final GuidedDecisionTable52 model,
                        final Pattern52 pattern,
                        final ConditionCol52 column,
                        final boolean isNew,
                        final boolean isReadOnly ) {
        this.popup = spy( new ConditionPopup( model,
                                              oracle,
                                              presenter,
                                              refreshGrid,
                                              pattern,
                                              column,
                                              isNew,
                                              isReadOnly ) );
    }

    @Test
    public void patternIsPreSelectedWhenBeingEdited() {
        final GuidedDecisionTable52 model = new GuidedDecisionTable52();
        final Pattern52 pattern = new Pattern52();
        final ConditionCol52 column = new ConditionCol52();

        final Pattern52 p1 = new Pattern52();
        p1.setBoundName( "$p1" );
        final Pattern52 p2 = new Pattern52();
        p2.setBoundName( "$p2" );
        model.getConditions().add( p1 );
        model.getConditions().add( p2 );

        pattern.setBoundName( "$p2" );

        setup( model,
               pattern,
               column,
               false,
               false );

        popup.loadPatterns();

        final ArgumentCaptor<Integer> indexArgumentCaptor = ArgumentCaptor.forClass( Integer.class );

        verify( popup,
                times( 1 ) ).selectPattern( any( ListBox.class ),
                                            indexArgumentCaptor.capture() );

        assertEquals( 1,
                      indexArgumentCaptor.getValue().intValue() );

    }

}
