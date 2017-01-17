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
import org.drools.workbench.models.datamodel.oracle.FieldAccessorsAndMutators;
import org.drools.workbench.models.datamodel.oracle.ModelField;
import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableConstants;
import org.drools.workbench.screens.guided.dtable.client.resources.images.GuidedDecisionTableImageResources508;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.drools.workbench.screens.guided.rule.client.editor.CEPWindowOperatorsDropdown;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.html.Text;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.resources.i18n.HumanReadableConstants;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.widgets.common.client.common.popups.FormStylePopup;

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

    private GuidedDecisionTable52 model;

    private Pattern52 pattern;

    private ConditionCol52 column;

    @BeforeClass
    public static void setupApplicationPreferences() {
        ApplicationPreferences.setUp( new HashMap<String, String>() {{
            put( ApplicationPreferences.DATE_FORMAT,
                 "dd-MM-yyyy" );
        }} );
    }

    @Before
    public void setUp() throws Exception {
        model = new GuidedDecisionTable52();
        pattern = new Pattern52();
        column = new ConditionCol52();

        pattern.setFactType( "Pattern" );
        pattern.setBoundName( "$p2" );
        column.setFactField( "field2" );
        column.setBinding( "$p2" );
        column.setOperator( "==" );
        column.setValueList( "xyz" );

        setUpPopup( model,
                    pattern,
                    column,
                    false,
                    false );

    }

    private void setUpPopup( final GuidedDecisionTable52 model,
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
        final Pattern52 p1 = new Pattern52();
        p1.setBoundName( "$p1" );
        final Pattern52 p2 = new Pattern52();
        p2.setBoundName( "$p2" );
        model.getConditions().add( p1 );
        model.getConditions().add( p2 );

        popup.loadPatterns();

        final ArgumentCaptor<Integer> indexArgumentCaptor = ArgumentCaptor.forClass( Integer.class );

        verify( popup,
                times( 1 ) ).selectListBoxItem( any( ListBox.class ),
                                                indexArgumentCaptor.capture() );

        assertEquals( 1,
                      indexArgumentCaptor.getValue().intValue() );
    }

    @Test
    @SuppressWarnings("unchecked")
    public void fieldIsPreSelectedWhenBeingEdited() {
        final ModelField[] modelFields = new ModelField[]{
                new ModelField( "field1",
                                "java.lang.Integer",
                                ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                ModelField.FIELD_ORIGIN.DECLARED,
                                FieldAccessorsAndMutators.ACCESSOR,
                                DataType.TYPE_NUMERIC_INTEGER ),
                new ModelField( "field2",
                                "java.lang.Integer",
                                ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                ModelField.FIELD_ORIGIN.DECLARED,
                                FieldAccessorsAndMutators.ACCESSOR,
                                DataType.TYPE_NUMERIC_INTEGER )
        };

        doAnswer( ( InvocationOnMock invocation ) -> {
            final Callback callback = (Callback) invocation.getArguments()[ 2 ];
            callback.callback( modelFields );
            return null;
        } ).when( oracle ).getFieldCompletions( eq( "Pattern" ),
                                                eq( FieldAccessorsAndMutators.ACCESSOR ),
                                                any( Callback.class ) );
        popup.loadFields();

        final ArgumentCaptor<Integer> indexArgumentCaptor = ArgumentCaptor.forClass( Integer.class );

        verify( popup,
                times( 1 ) ).selectListBoxItem( any( ListBox.class ),
                                                indexArgumentCaptor.capture() );

        assertEquals( 1,
                      indexArgumentCaptor.getValue().intValue() );
    }

    @Test
    public void testConfirmFieldChangePopUp() throws Exception {
        FormStylePopup fieldChangePopUp = mock( FormStylePopup.class );

        popup.view = spy( new ConditionPopupView( popup ) ) ;

        popup.confirmFieldChangePopUp( fieldChangePopUp, "newSelectedField" );

        assertEquals( null, popup.getEditingCol().getOperator() );
        assertEquals( null, popup.getEditingCol().getValueList() );

        verify( popup.view ).setFieldLabelText( "newSelectedField" );

        verify( popup.view ).setValueListWidgetText( "" );

        verify( popup.view ).enableLiteral( true );
        verify( popup.view ).enableFormula( true );
        verify( popup.view ).enablePredicate( true );
        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL, popup.getEditingCol().getConstraintValueType() );

        verify( popup.view, never() ).addLimitedEntryValue();
        verify( popup.view, never() ).setLimitedEntryVisibility( anyBoolean() );

        verify( popup.view ).addDefaultValue();
        verify( popup.view ).setDefaultValueVisibility( false );
        assertEquals( null, popup.getEditingCol().getDefaultValue() );

        verify( popup.view ).setOperatorLabelText( GuidedDecisionTableConstants.INSTANCE.pleaseSelectAnOperator() );

        verify( popup.view ).enableEditField( true );
        verify( popup.view ).enableEditOperator( true );

        verify( fieldChangePopUp ).hide();
        verify( popup.view ).enableFooter( true );
    }

    @Test
    public void testConfirmFieldChangePopUpNoChange() throws Exception {
        FormStylePopup fieldChangePopUp = mock( FormStylePopup.class );

        popup.view = spy( new ConditionPopupView( popup ) ) ;

        popup.confirmFieldChangePopUp( fieldChangePopUp, "field2" );

        assertEquals( "==", popup.getEditingCol().getOperator() );
        assertEquals( "xyz", popup.getEditingCol().getValueList() );

        verify( popup.view ).setFieldLabelText( "field2" );

        verify( popup.view ).setValueListWidgetText( "xyz" );

        verify( popup.view ).enableLiteral( true );
        verify( popup.view ).enableFormula( true );
        verify( popup.view ).enablePredicate( true );
        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL, popup.getEditingCol().getConstraintValueType() );

        verify( popup.view, never() ).addLimitedEntryValue();
        verify( popup.view, never() ).setLimitedEntryVisibility( anyBoolean() );

        verify( popup.view ).addDefaultValue();
        verify( popup.view ).setDefaultValueVisibility( true );
        assertEquals( null, popup.getEditingCol().getDefaultValue().getStringValue() );

        verify( popup.view ).setOperatorLabelText( HumanReadableConstants.INSTANCE.isEqualTo() );

        verify( popup.view ).enableEditField( true );
        verify( popup.view ).enableEditOperator( true );

        verify( fieldChangePopUp ).hide();
        verify( popup.view ).enableFooter( true );
    }

    @Test
    public void testCancelFieldChangePopUp() throws Exception {
        FormStylePopup fieldChangePopUp = mock( FormStylePopup.class );

        popup.view = spy( new ConditionPopupView( popup ) ) ;

        popup.cancelFieldChangePopUp( fieldChangePopUp );

        assertEquals( "==", popup.getEditingCol().getOperator() );
        assertEquals( "xyz", popup.getEditingCol().getValueList() );

        verify( popup.view, never() ).setFieldLabelText( anyString() );

        verify( popup.view, never() ).setValueListWidgetText( anyString() );

        verify( popup.view, never() ).enableLiteral( anyBoolean() );
        verify( popup.view, never() ).enableFormula( anyBoolean() );
        verify( popup.view, never() ).enablePredicate( anyBoolean() );
        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL, popup.getEditingCol().getConstraintValueType() );

        verify( popup.view, never() ).addLimitedEntryValue();
        verify( popup.view, never() ).setLimitedEntryVisibility( anyBoolean() );

        verify( popup.view, never() ).addDefaultValue();
        verify( popup.view, never() ).setDefaultValueVisibility( anyBoolean() );

        verify( popup.view, never() ).setOperatorLabelText( anyString() );

        verify( popup.view, never() ).enableEditField( anyBoolean() );
        verify( popup.view, never() ).enableEditOperator( anyBoolean() );

        verify( fieldChangePopUp ).hide();
        verify( popup.view ).enableFooter( true );
    }
}
