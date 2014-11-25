package org.drools.workbench.screens.testscenario.client;

import java.util.List;
import java.util.Map;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.models.datamodel.oracle.DropDownData;
import org.drools.workbench.models.datamodel.oracle.MethodInfo;
import org.drools.workbench.models.datamodel.rule.ActionCallMethod;
import org.drools.workbench.models.testscenarios.shared.CallFieldValue;
import org.drools.workbench.models.testscenarios.shared.CallMethod;
import org.drools.workbench.models.testscenarios.shared.ExecutionTrace;
import org.drools.workbench.models.testscenarios.shared.FactData;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.drools.workbench.screens.testscenario.client.resources.i18n.TestScenarioConstants;
import org.drools.workbench.screens.testscenario.client.resources.images.TestScenarioAltedImages;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.resources.CommonAltedImages;
import org.kie.workbench.common.widgets.client.resources.HumanReadable;
import org.uberfire.client.callbacks.Callback;
import com.google.gwt.user.client.ui.FlexTable;
import org.uberfire.ext.widgets.common.client.common.ImageButton;
import org.uberfire.ext.widgets.common.client.common.SmallLabel;
import org.uberfire.ext.widgets.common.client.common.popups.FormStylePopup;

public class CallMethodWidget extends Composite {

    protected final ScenarioParentWidget parent;
    protected final Scenario scenario;
    protected final CallMethod mCall;
    protected final String factName;
    private final ExecutionTrace executionTrace;

    final private FlexTable layout;
    private boolean isBoundFact = false;

    private String[] fieldCompletionTexts;
    private String[] fieldCompletionValues;
    private String variableClass;

    private final AsyncPackageDataModelOracle oracle;

    public CallMethodWidget( final String factName,
                             final ScenarioParentWidget parent,
                             final Scenario scenario,
                             final CallMethod mCall,
                             final ExecutionTrace executionTrace,
                             final AsyncPackageDataModelOracle oracle ) {
        super();
        this.factName = factName;
        this.parent = parent;
        this.scenario = scenario;
        this.mCall = mCall;
        this.executionTrace = executionTrace;
        this.oracle = oracle;

        this.layout = new FlexTable();

        layout.setStyleName( "model-builderInner-Background" ); // NON-NLS

        if ( this.oracle.isGlobalVariable( mCall.getVariable() ) ) {

            this.oracle.getMethodInfosForGlobalVariable( mCall.getVariable(),
                                                         new Callback<List<MethodInfo>>() {
                                                             @Override
                                                             public void callback( final List<MethodInfo> infos ) {
                                                                 CallMethodWidget.this.fieldCompletionTexts = new String[ infos.size() ];
                                                                 CallMethodWidget.this.fieldCompletionValues = new String[ infos.size() ];
                                                                 int i = 0;
                                                                 for ( MethodInfo info : infos ) {
                                                                     CallMethodWidget.this.fieldCompletionTexts[ i ] = info.getName();
                                                                     CallMethodWidget.this.fieldCompletionValues[ i ] = info.getNameWithParameters();
                                                                     i++;
                                                                 }

                                                                 CallMethodWidget.this.variableClass = (String) CallMethodWidget.this.oracle.getGlobalVariable( mCall.getVariable() );
                                                             }
                                                         } );
        } else {

            final FactData pattern = (FactData) scenario.getFactTypes().get( mCall.getVariable() );
            if ( pattern != null ) {
                this.oracle.getMethodInfos( pattern.getType(),
                                            new Callback<List<MethodInfo>>() {
                                                @Override
                                                public void callback( final List<MethodInfo> methodInfos ) {
                                                    CallMethodWidget.this.fieldCompletionTexts = new String[ methodInfos.size() ];
                                                    CallMethodWidget.this.fieldCompletionValues = new String[ methodInfos.size() ];
                                                    int i = 0;
                                                    for ( MethodInfo methodInfo : methodInfos ) {
                                                        CallMethodWidget.this.fieldCompletionTexts[ i ] = methodInfo.getName();
                                                        CallMethodWidget.this.fieldCompletionValues[ i ] = methodInfo.getNameWithParameters();
                                                        i++;
                                                    }
                                                    CallMethodWidget.this.variableClass = pattern.getType();
                                                    CallMethodWidget.this.isBoundFact = true;
                                                }
                                            } );
            }
        }

        doLayout();
        initWidget( this.layout );
    }

    private void doLayout() {
        layout.clear();
        layout.setWidget( 0,
                          0,
                          getSetterLabel() );
        FlexTable inner = new FlexTable();
        int i = 0;
        for ( CallFieldValue val : mCall.getCallFieldValues() ) {

            inner.setWidget( i,
                             0,
                             fieldSelector( val ) );
            inner.setWidget( i,
                             1,
                             valueEditor( val ) );
            i++;
        }
        layout.setWidget( 0,
                          1,
                          inner );
        layout.setWidget( 0,
                          2,
                          new DeleteButton() );
    }

    private Widget getSetterLabel() {
        HorizontalPanel horiz = new HorizontalPanel();

        if ( mCall.getState() == ActionCallMethod.TYPE_UNDEFINED ) {
            Image edit = TestScenarioAltedImages.INSTANCE.AddFieldToFact();
            edit.setTitle( TestScenarioConstants.INSTANCE.AddAnotherFieldToThisSoYouCanSetItsValue() );

            edit.addClickHandler( new ClickHandler() {

                public void onClick( ClickEvent event ) {
                    Image w = (Image) event.getSource();
                    showAddFieldPopup( w );

                }
            } );

            horiz.add( new SmallLabel( HumanReadable.getActionDisplayName( "call" ) + " [" + mCall.getVariable() + "]" ) ); // NON-NLS
            horiz.add( edit );
        } else {
            horiz.add( new SmallLabel( HumanReadable.getActionDisplayName( "call" ) + " [" + mCall.getVariable() + "." + mCall.getMethodName() + "]" ) ); // NON-NLS
        }

        return horiz;
    }

    protected void showAddFieldPopup( final Widget w ) {
        final FormStylePopup popup = new FormStylePopup( TestScenarioAltedImages.INSTANCE.Wizard(),
                                                         TestScenarioConstants.INSTANCE.ChooseAMethodToInvoke() );
        ListBox box = new ListBox();
        box.addItem( "..." );

        for ( int i = 0; i < fieldCompletionTexts.length; i++ ) {
            box.addItem( fieldCompletionTexts[ i ],
                         fieldCompletionValues[ i ] );
        }

        box.setSelectedIndex( 0 );

        popup.addAttribute( TestScenarioConstants.INSTANCE.ChooseAMethodToInvoke(),
                            box );
        box.addChangeHandler( new ChangeHandler() {

            public void onChange( ChangeEvent event ) {
                mCall.setState( ActionCallMethod.TYPE_DEFINED );
                ListBox sourceW = (ListBox) event.getSource();
                final String methodName = sourceW.getItemText( sourceW.getSelectedIndex() );
                final String methodNameWithParams = sourceW.getValue( sourceW.getSelectedIndex() );

                mCall.setMethodName( methodName );

                oracle.getMethodParams( variableClass,
                                        methodNameWithParams,
                                        new Callback<List<String>>() {
                                            @Override
                                            public void callback( final List<String> fieldList ) {
                                                // String fieldType = oracle.getFieldType( variableClass, fieldName );
                                                int i = 0;
                                                for ( String fieldParameter : fieldList ) {
                                                    mCall.addFieldValue( new CallFieldValue( methodName,
                                                                                             String.valueOf( i ),
                                                                                             fieldParameter ) );
                                                    i++;
                                                }

                                                parent.renderEditor();
                                                popup.hide();
                                            }
                                        } );
            }
        } );

        popup.show();
    }

    private Widget valueEditor( final CallFieldValue val ) {

        String type = "";
        if ( oracle.isGlobalVariable( this.mCall.getVariable() ) ) {
            type = oracle.getGlobalVariable( this.mCall.getVariable() );
        } else {
            Map<String, String> mFactTypes = scenario.getVariableTypes();
            type = mFactTypes.get( this.mCall.getVariable() );
        }

        DropDownData enums = oracle.getEnums(
                type,
                val.field,
                this.mCall.getCallFieldValuesMap()
                                            );
        return new MethodParameterCallValueEditor( val,
                                                   enums,
                                                   executionTrace,
                                                   scenario,
                                                   val.type,
                                                   oracle );
    }

    private Widget fieldSelector( final CallFieldValue val ) {
        return new SmallLabel( val.type );
    }

    protected void onDelete() {
        if ( Window.confirm( TestScenarioConstants.INSTANCE.AreYouSureToRemoveCallMethod() ) ) {
            scenario.removeFixture( mCall );
            parent.renderEditor();
        }
    }

    class DeleteButton extends ImageButton {

        public DeleteButton() {
            super( CommonAltedImages.INSTANCE.DeleteItemSmall(),
                   TestScenarioConstants.INSTANCE.RemoveCallMethod() );

            addClickHandler( new ClickHandler() {

                public void onClick( ClickEvent event ) {
                    onDelete();
                }
            } );
        }
    }

}
