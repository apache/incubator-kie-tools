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
import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.guided.dtable.shared.model.ActionSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableConstants;
import org.drools.workbench.screens.guided.dtable.client.resources.images.GuidedDecisionTableImageResources508;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.gwtbootstrap3.client.ui.html.Text;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.mockito.Mock;
import org.uberfire.ext.widgets.common.client.common.ImageButton;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub({ Text.class, GuidedDecisionTableImageResources508.class })
public class ActionSetFieldPopupTest {

    @Mock
    private AsyncPackageDataModelOracle oracle;

    @Mock
    private GuidedDecisionTableView.Presenter presenter;

    @Mock
    private ActionColumnCommand refreshGrid;

    private ActionSetFieldPopup popup;

    @BeforeClass
    public static void setupApplicationPreferences() {
        ApplicationPreferences.setUp( new HashMap<String, String>() {{
            put( ApplicationPreferences.DATE_FORMAT,
                 "dd-MM-yyyy" );
        }} );
    }

    private void setup( final GuidedDecisionTable52 model,
                        final ActionSetFieldCol52 column,
                        final boolean isNew,
                        final boolean isReadOnly ) {
        this.popup = spy( new ActionSetFieldPopup( model,
                                                   oracle,
                                                   presenter,
                                                   refreshGrid,
                                                   column,
                                                   isNew,
                                                   isReadOnly ) );
        this.popup.editField = mock( ImageButton.class );
    }

    @Test
    public void noPatternSelected() {
        final GuidedDecisionTable52 model = new GuidedDecisionTable52();
        final ActionSetFieldCol52 column = new ActionSetFieldCol52();

        model.getActionCols().add( column );

        setup( model,
               column,
               false,
               false );

        popup.doFieldLabel();

        verify( popup,
                times( 1 ) ).setFieldLabelPleaseChooseAFactPatternFirst();
        verify( popup.editField,
                times( 1 ) ).setEnabled( false );
        verify( popup,
                never() ).setFieldLabelPleaseSelectAField();
        verify( popup,
                never() ).setFieldLabelToFieldName( any( String.class ) );
    }

    @Test
    public void patternSelectedNoFieldSelected() {
        final GuidedDecisionTable52 model = new GuidedDecisionTable52();
        final ActionSetFieldCol52 column = new ActionSetFieldCol52();
        column.setBoundName( "$a" );

        model.getActionCols().add( column );

        setup( model,
               column,
               false,
               false );

        popup.doFieldLabel();

        verify( popup,
                never() ).setFieldLabelPleaseChooseAFactPatternFirst();
        verify( popup,
                times( 1 ) ).setFieldLabelPleaseSelectAField();
        verify( popup.editField,
                times( 1 ) ).setEnabled( true );
        verify( popup,
                never() ).setFieldLabelToFieldName( any( String.class ) );
    }

    @Test
    public void patternAsConstantSelectedNoFieldSelected() {
        final GuidedDecisionTable52 model = new GuidedDecisionTable52();
        final ActionSetFieldCol52 column = new ActionSetFieldCol52();
        column.setBoundName( GuidedDecisionTableConstants.INSTANCE.Choose() );

        model.getActionCols().add( column );

        setup( model,
                column,
                false,
                false );

        popup.doFieldLabel();

        verify( popup,
                times( 1 ) ).setFieldLabelPleaseChooseAFactPatternFirst();
        verify( popup.editField,
                times( 1 ) ).setEnabled( false );
        verify( popup,
                never() ).setFieldLabelPleaseSelectAField();
        verify( popup,
                never() ).setFieldLabelToFieldName( any( String.class ) );
    }

    @Test
    public void patternSelectedFieldSelected() {
        final GuidedDecisionTable52 model = new GuidedDecisionTable52();

        final Pattern52 pattern = new Pattern52();
        pattern.setBoundName( "$a" );
        pattern.setFactType( "MyFact" );

        model.getConditions().add( pattern );

        final ActionSetFieldCol52 column = new ActionSetFieldCol52();
        column.setBoundName( "$a" );
        column.setFactField( "myField" );

        when( oracle.getFieldType( eq( "MyFact" ),
                                   eq( "myField" ) ) ).thenReturn( DataType.TYPE_STRING );

        model.getActionCols().add( column );

        setup( model,
               column,
               false,
               false );

        popup.doFieldLabel();

        verify( popup,
                never() ).setFieldLabelPleaseChooseAFactPatternFirst();
        verify( popup,
                never() ).setFieldLabelPleaseSelectAField();
        verify( popup,
                times( 1 ) ).setFieldLabelToFieldName( any( String.class ) );
        verify( popup.editField,
                times( 1 ) ).setEnabled( true );
    }

}
