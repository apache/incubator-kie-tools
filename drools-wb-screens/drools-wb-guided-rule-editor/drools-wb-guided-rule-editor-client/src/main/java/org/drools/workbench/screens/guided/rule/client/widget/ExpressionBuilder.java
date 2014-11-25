/*
 * Copyright 2012 JBoss Inc
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

package org.drools.workbench.screens.guided.rule.client.widget;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.datamodel.oracle.FieldAccessorsAndMutators;
import org.drools.workbench.models.datamodel.oracle.MethodInfo;
import org.drools.workbench.models.datamodel.oracle.ModelField;
import org.drools.workbench.models.datamodel.rule.ExpressionFieldVariable;
import org.drools.workbench.models.datamodel.rule.ExpressionFormLine;
import org.drools.workbench.models.datamodel.rule.ExpressionMethod;
import org.drools.workbench.models.datamodel.rule.ExpressionMethodParameter;
import org.drools.workbench.models.datamodel.rule.ExpressionPart;
import org.drools.workbench.models.datamodel.rule.ExpressionText;
import org.drools.workbench.models.datamodel.rule.ExpressionUnboundFact;
import org.drools.workbench.models.datamodel.rule.ExpressionVariable;
import org.drools.workbench.models.datamodel.rule.FactPattern;
import org.drools.workbench.models.datamodel.rule.RuleModel;
import org.drools.workbench.screens.guided.rule.client.editor.ExpressionChangeEvent;
import org.drools.workbench.screens.guided.rule.client.editor.ExpressionChangeHandler;
import org.drools.workbench.screens.guided.rule.client.editor.ExpressionTypeChangeEvent;
import org.drools.workbench.screens.guided.rule.client.editor.ExpressionTypeChangeHandler;
import org.drools.workbench.screens.guided.rule.client.editor.HasExpressionChangeHandlers;
import org.drools.workbench.screens.guided.rule.client.editor.HasExpressionTypeChangeHandlers;
import org.drools.workbench.screens.guided.rule.client.editor.RuleModeller;
import org.drools.workbench.screens.guided.rule.client.resources.GuidedRuleEditorResources;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.resources.i18n.HumanReadableConstants;
import org.kie.workbench.common.widgets.client.widget.TextBoxFactory;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.widgets.common.client.common.ClickableLabel;
import org.uberfire.ext.widgets.common.client.common.SmallLabel;
import org.uberfire.ext.widgets.common.client.common.popups.FormStylePopup;

public class ExpressionBuilder extends RuleModellerWidget
        implements
        HasExpressionTypeChangeHandlers,
        HasExpressionChangeHandlers {

    private static final String DELETE_VALUE = "_delete_";
    private static final String TEXT_VALUE = "_text_";
    private static final String FIElD_VALUE_PREFIX = "fl";
    private static final String VARIABLE_VALUE_PREFIX = "va";
    // private static final String GLOBAL_COLLECTION_VALUE_PREFIX = "gc";
    private static final String GLOBAL_VARIABLE_VALUE_PREFIX = "gv";
    private static final String METHOD_VALUE_PREFIX = "mt";
    private final SmallLabelClickHandler slch = new SmallLabelClickHandler();
    private HorizontalPanel panel = new HorizontalPanel();
    private ExpressionFormLine expression;
    private boolean readOnly;

    private boolean isFactTypeKnown;

    public ExpressionBuilder( RuleModeller modeller,
                              EventBus eventBus,
                              ExpressionFormLine expression ) {
        this( modeller,
              eventBus,
              expression,
              false );
    }

    public ExpressionBuilder( RuleModeller modeller,
                              EventBus eventBus,
                              ExpressionFormLine expression,
                              Boolean readOnly ) {
        super( modeller,
               eventBus );
        this.expression = expression;

        if ( this.expression.isEmpty() ) {
            this.isFactTypeKnown = true;
        } else {
            this.isFactTypeKnown = getModeller().getDataModelOracle().isFactTypeRecognized( getModeller().getDataModelOracle().getFactNameFromType( this.expression.getRootExpression().getClassType() ) );
        }

        if ( readOnly == null ) {
            this.readOnly = !this.isFactTypeKnown;
        } else {
            this.readOnly = readOnly;
        }

        panel.setVerticalAlignment( HasVerticalAlignment.ALIGN_MIDDLE );
        panel.setStylePrimaryName( GuidedRuleEditorResources.INSTANCE.css().container() );

        StringBuilder bindingLabel = new StringBuilder();
        String binding = getBoundText();
        if ( binding != null && !binding.equals( "" ) ) {
            bindingLabel.append( "<b>" );
            bindingLabel.append( binding );
            bindingLabel.append( "</b>" );
        }

        if ( expression == null || expression.isEmpty() ) {
            if ( this.readOnly ) {
                panel.add( new SmallLabel( "<b>-</b>" ) );
            } else {
                panel.add( createStartPointWidget() );
            }
        } else {
            if ( this.readOnly ) {
                panel.add( createBindingWidgetForExpression( bindingLabel.toString() ) );
                panel.add( createWidgetForExpression() );
            } else {
                bindingLabel.append( "." );
                panel.add( createBindingWidgetForExpression( bindingLabel.toString() ) );
                panel.add( createWidgetForExpression() );
                panel.add( getWidgetForCurrentType() );
            }
        }
        initWidget( panel );
    }

    private String getBoundText() {
        if ( expression.isBound() ) {
            return "[" + expression.getBinding() + "] ";
        }
        return "[not bound]";
    }

    private Widget createStartPointWidget() {
        ListBox startPoint = new ListBox();
        panel.add( startPoint );

        startPoint.addItem( GuidedRuleEditorResources.CONSTANTS.ChooseDotDotDot(),
                            "" );

        // TODO {baunax} uncomment when global collections is implemented.
        // for (String gc : getDataModelOracle().getGlobalCollections()) {
        // startPoint.addItem(gc, GLOBAL_COLLECTION_VALUE_PREFIX + "." + gc);
        // }

        for ( String gv : getDataModelOracle().getGlobalVariables() ) {
            startPoint.addItem( gv,
                                GLOBAL_VARIABLE_VALUE_PREFIX + "." + gv );
        }

        for ( String v : getRuleModel().getAllLHSVariables() ) {
            startPoint.addItem( v,
                                VARIABLE_VALUE_PREFIX + "." + v );
        }

        startPoint.setVisibleItemCount( 1 );
        startPoint.addChangeHandler( new ChangeHandler() {

            public void onChange( ChangeEvent event ) {
                ListBox lb = (ListBox) event.getSource();
                int index = lb.getSelectedIndex();
                if ( index > 0 ) {
                    onStartPointChange( lb.getValue( index ) );
                }
            }
        } );
        return startPoint;
    }

    private void onStartPointChange( final String value ) {
        setModified( true );
        panel.clear();
        final int dotPos = value.indexOf( '.' );
        final String prefix = value.substring( 0,
                                               dotPos );
        final String attrib = value.substring( dotPos + 1 );

        if ( prefix.equals( VARIABLE_VALUE_PREFIX ) ) {
            FactPattern fact = getRuleModel().getLHSBoundFact( attrib );
            ExpressionPart variable;
            if ( fact != null ) {
                variable = new ExpressionVariable( fact );
            } else {
                //if the variable is not bound to a Fact Pattern then it must be bound to a Field
                String lhsBindingType = getRuleModel().getLHSBindingType( attrib );
                variable = new ExpressionFieldVariable( attrib,
                                                        lhsBindingType );
            }
            expression.appendPart( variable );
            onStartPointChangeUpdateWidget();

        } else if ( prefix.equals( GLOBAL_VARIABLE_VALUE_PREFIX ) ) {
            ExpressionPartHelper.getExpressionPartForGlobalVariable( getDataModelOracle(),
                                                                     attrib,
                                                                     new Callback<ExpressionPart>() {
                                                                         @Override
                                                                         public void callback( final ExpressionPart part ) {
                                                                             expression.appendPart( part );
                                                                             onStartPointChangeUpdateWidget();
                                                                         }
                                                                     } );
        }
    }

    private void onStartPointChangeUpdateWidget() {
        final Widget w = getWidgetForCurrentType();
        if ( !expression.isEmpty() ) {
            panel.add( createWidgetForExpression() );
        }
        if ( w != null ) {
            panel.add( w );
        }
        fireExpressionChangeEvent();
        fireExpressionTypeChangeEvent();
    }

    private Widget getWidgetForCurrentType() {
        if ( expression.isEmpty() ) {
            return createStartPointWidget();
        }

        final ChangeHandler changeHandler = new ChangeHandler() {
            public void onChange( ChangeEvent event ) {
                ListBox box = (ListBox) event.getSource();
                panel.remove( box );
                if ( box.getSelectedIndex() > 0 ) {
                    onChangeSelection( box.getValue( box.getSelectedIndex() ) );
                }
            }
        };

        final ListBox listBox = new ListBox();
        listBox.addItem( GuidedRuleEditorResources.CONSTANTS.ChooseDotDotDot(),
                         "" );
        listBox.addItem( "<==" + GuidedRuleEditorResources.CONSTANTS.DeleteItem(),
                         DELETE_VALUE );
        listBox.addItem( "-- Text --",
                         TEXT_VALUE );

        getCompletionsForCurrentType( expression.getParts().size() > 1,
                                      new Callback<Map<String, String>>() {
                                          @Override
                                          public void callback( final Map<String, String> completions ) {
                                              for ( Map.Entry<String, String> entry : completions.entrySet() ) {
                                                  listBox.addItem( entry.getKey(),
                                                                   entry.getValue() );
                                              }
                                              listBox.addChangeHandler( changeHandler );
                                          }
                                      } );

        return listBox;
    }

    private void onChangeSelection( String value ) {
        setModified( true );
        String prevFactName = null;
        final String oldType = getCurrentGenericType();

        if ( DELETE_VALUE.equals( value ) ) {
            expression.removeLast();
            onChangeSelectionUpdateExpressionWidget( oldType );

        } else if ( TEXT_VALUE.equals( value ) ) {
            expression.appendPart( new ExpressionText( "" ) );
            onChangeSelectionUpdateExpressionWidget( oldType );

        } else {
            int dotPos = value.indexOf( '.' );
            String prefix = value.substring( 0,
                                             dotPos );
            String attrib = value.substring( dotPos + 1 );

            prevFactName = getDataModelOracle().getFactNameFromType( getCurrentClassType() );
            // String genericType = SuggestionCompletionEngine.TYPE_OBJECT;
            if ( FIElD_VALUE_PREFIX.equals( prefix ) ) {
                ExpressionPartHelper.getExpressionPartForField( getDataModelOracle(),
                                                                prevFactName,
                                                                attrib,
                                                                new Callback<ExpressionPart>() {
                                                                    @Override
                                                                    public void callback( final ExpressionPart part ) {
                                                                        expression.appendPart( part );
                                                                        onChangeSelectionUpdateExpressionWidget( oldType );
                                                                    }
                                                                } );

            } else if ( METHOD_VALUE_PREFIX.equals( prefix ) ) {
                ExpressionPartHelper.getExpressionPartForMethod( getDataModelOracle(),
                                                                 prevFactName,
                                                                 attrib,
                                                                 new Callback<ExpressionPart>() {
                                                                     @Override
                                                                     public void callback( final ExpressionPart part ) {
                                                                         expression.appendPart( part );
                                                                         onChangeSelectionUpdateExpressionWidget( oldType );
                                                                     }
                                                                 } );
            }
        }
    }

    private void onChangeSelectionUpdateExpressionWidget( final String oldType ) {
        Widget w = getWidgetForCurrentType();

        panel.clear();
        if ( !expression.isEmpty() ) {
            panel.add( createWidgetForExpression() );
        }
        if ( w != null ) {
            panel.add( w );
        }
        fireExpressionChangeEvent();
        fireExpressionTypeChangeEvent( oldType );
    }

    private void getCompletionsForCurrentType( final boolean isNested,
                                               final Callback<Map<String, String>> callback ) {
        if ( DataType.TYPE_FINAL_OBJECT.equals( getCurrentGenericType() ) ) {
            callback.callback( Collections.EMPTY_MAP );
            return;
        }

        final String factName = getDataModelOracle().getFactNameFromType( getCurrentClassType() );
        if ( factName != null ) {
            // we currently only support 0 param method calls
            getMethods( isNested,
                        callback,
                        factName );

        } else {
            // else {We don't know anything about this type, so return empty map}
            callback.callback( Collections.EMPTY_MAP );
        }
    }

    private void getMethods( final boolean isNested,
                             final Callback<Map<String, String>> callback,
                             final String factName ) {
        getDataModelOracle().getMethodInfos( factName,
                                             new Callback<List<MethodInfo>>() {
                                                 @Override
                                                 public void callback( final List<MethodInfo> methodInfos ) {
                                                     fillMethods( methodInfos,
                                                                  factName,
                                                                  isNested,
                                                                  callback );
                                                 }
                                             } );
    }

    private void fillMethods( final List<MethodInfo> methodInfos,
                              final String factName,
                              final boolean isNested,
                              final Callback<Map<String, String>> callback ) {
        getDataModelOracle().getFieldCompletions( factName,
                                                  FieldAccessorsAndMutators.ACCESSOR,
                                                  new Callback<ModelField[]>() {

                                                      @Override
                                                      public void callback( final ModelField[] fields ) {
                                                          final Map<String, String> completions = new LinkedHashMap<String, String>();

                                                          //Add fields
                                                          for ( ModelField field : fields ) {
                                                              final String fieldName = field.getName();
                                                              if ( !isNested || !fieldName.equals( DataType.TYPE_THIS ) ) {
                                                                  completions.put( fieldName,
                                                                                   FIElD_VALUE_PREFIX + "." + fieldName );
                                                              }
                                                          }
                                                          //Add methods
                                                          for ( MethodInfo methodInfo : methodInfos ) {
                                                              if ( !methodInfo.getGenericType().equals( DataType.TYPE_VOID ) ) {
                                                                  final String methodName = methodInfo.getName();
                                                                  final String methodNameWithParams = methodInfo.getNameWithParameters();
                                                                  completions.put( methodName,
                                                                                   METHOD_VALUE_PREFIX + "." + methodNameWithParams );
                                                              }
                                                          }
                                                          callback.callback( completions );
                                                      }
                                                  } );
    }

    private RuleModel getRuleModel() {
        return this.getModeller().getModel();
    }

    private AsyncPackageDataModelOracle getDataModelOracle() {
        return this.getModeller().getDataModelOracle();
    }

    private String getCurrentClassType() {
        return expression.getClassType();
    }

    private String getCurrentGenericType() {
        //If the last ExpressionPart is ExpressionText then we can't show any Fields or Methods from which to select
        if ( expression.getParts().isEmpty() ) {
            return null;
        }
        if ( expression.getParts().get( expression.getParts().size() - 1 ) instanceof ExpressionText ) {
            return DataType.TYPE_FINAL_OBJECT;
        }
        return expression.getGenericType();
    }

    private String getPreviousGenericType() {
        return expression.getPreviousGenericType();
    }

    private String getCurrentParametricType() {
        return expression.getParametricType();
    }

    @Override
    public boolean isReadOnly() {
        return this.readOnly;
    }

    @Override
    public boolean isFactTypeKnown() {
        return this.isFactTypeKnown;
    }

    /**
     * @see HasExpressionTypeChangeHandlers(ExpressionTypeChangeHandler)
     */
    public HandlerRegistration addExpressionTypeChangeHandler( ExpressionTypeChangeHandler handler ) {
        return addHandler( handler,
                           ExpressionTypeChangeEvent.getType() );
    }

    private void fireExpressionChangeEvent() {
        fireEvent( new ExpressionChangeEvent() );
    }

    private void fireExpressionTypeChangeEvent() {
        fireExpressionTypeChangeEvent( getPreviousGenericType() );
    }

    private void fireExpressionTypeChangeEvent( String previousGenericType ) {
        String currentGenericType = getCurrentGenericType();
        if ( ( previousGenericType == null || !previousGenericType.equals( currentGenericType ) ) || currentGenericType != null ) {
            fireEvent( new ExpressionTypeChangeEvent( previousGenericType,
                                                      currentGenericType ) );
        }
    }

    public HandlerRegistration addExpressionChangeHandler( ExpressionChangeHandler handler ) {
        return addHandler( handler,
                           ExpressionChangeEvent.getType() );
    }

    private void showBindingPopUp() {
        final FormStylePopup popup = new FormStylePopup( GuidedRuleEditorResources.CONSTANTS.ExpressionEditor() );
        popup.setWidth( 500 + "px" );
        HorizontalPanel vn = new HorizontalPanel();
        final TextBox varName = new TextBox();
        Button ok = new Button( HumanReadableConstants.INSTANCE.Set() );
        vn.add( new Label( GuidedRuleEditorResources.CONSTANTS.BindTheExpressionToAVariable() ) );
        vn.add( varName );
        vn.add( ok );

        ok.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent event ) {
                String var = varName.getText();
                if ( getModeller().isVariableNameUsed( var ) ) {
                    Window.alert( GuidedRuleEditorResources.CONSTANTS.TheVariableName0IsAlreadyTaken( var ) );
                    return;
                }
                expression.setBinding( var );
                getModeller().refreshWidget();
                popup.hide();
            }
        } );

        popup.addRow( vn );
        popup.show();
    }

    private class SmallLabelClickHandler
            implements
            ClickHandler {

        public void onClick( ClickEvent event ) {
            showBindingPopUp();
        }
    }

    private ClickableLabel createBindingWidgetForExpression( final String text ) {
        ClickableLabel label = new ClickableLabel( text,
                                                   slch,
                                                   !this.readOnly );
        return label;
    }

    //Render Widgets for the Expression. ExpressionMethodParameter and ExpressionText parts
    //are represented by a TextBox to allow the User to edit the values, Updates are
    //reflected in the model.
    private Widget createWidgetForExpression() {
        final HorizontalPanel container = new HorizontalPanel();
        container.setVerticalAlignment( HasVerticalAlignment.ALIGN_MIDDLE );
        container.setStylePrimaryName( GuidedRuleEditorResources.INSTANCE.css().container() );
        for ( ExpressionPart expressionPart : expression.getParts() ) {
            if ( expressionPart instanceof ExpressionUnboundFact ) {
                continue;
            } else if ( this.readOnly ) {
                container.add( new Label( expressionPart.getName() ) );
            } else if ( expressionPart instanceof ExpressionMethod ) {
                container.add( new Label( expressionPart.getName() ) );
                container.add( new Label( "(" ) );
                final ExpressionMethod em = (ExpressionMethod) expressionPart;
                final List<ExpressionFormLine> emParams = em.getOrderedParams();
                for ( int index = 0; index < emParams.size(); index++ ) {
                    final ExpressionFormLine paramValueHolder = emParams.get( index );
                    final String paramDataType = em.getParameterDataType( paramValueHolder );
                    final ExpressionMethodParameter paramValue = ( (ExpressionMethodParameter) paramValueHolder.getRootExpression() );
                    final TextBox paramValueEditor = TextBoxFactory.getTextBox( paramDataType );
                    paramValueEditor.addValueChangeHandler( new ValueChangeHandler<String>() {
                        @Override
                        public void onValueChange( ValueChangeEvent<String> event ) {
                            paramValue.setText( event.getValue() );
                        }
                    } );
                    paramValueEditor.setText( paramValue.getName() );
                    container.add( paramValueEditor );
                    if ( index < emParams.size() - 1 ) {
                        container.add( new Label( ", " ) );
                    }
                }
                container.add( new Label( ")" ) );
            } else if ( !( expressionPart instanceof ExpressionText ) ) {
                container.add( new Label( expressionPart.getName() ) );
            } else {
                final TextBox tb = new TextBox();
                final ExpressionText expressionTextPart = (ExpressionText) expressionPart;
                tb.setText( expressionTextPart.getName() );
                tb.addChangeHandler( new ChangeHandler() {
                    @Override
                    public void onChange( final ChangeEvent changeEvent ) {
                        expressionTextPart.setText( tb.getText() );
                    }
                } );
                container.add( tb );
            }
            container.add( new Label( "." ) );
        }
        return container;
    }

}
