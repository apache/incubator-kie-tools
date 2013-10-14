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

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
import org.drools.workbench.models.datamodel.oracle.ModelField;
import org.drools.workbench.models.datamodel.rule.ExpressionCollectionIndex;
import org.drools.workbench.models.datamodel.rule.ExpressionFieldVariable;
import org.drools.workbench.models.datamodel.rule.ExpressionFormLine;
import org.drools.workbench.models.datamodel.rule.ExpressionMethod;
import org.drools.workbench.models.datamodel.rule.ExpressionPart;
import org.drools.workbench.models.datamodel.rule.ExpressionText;
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
import org.drools.workbench.screens.guided.rule.client.resources.i18n.Constants;
import org.kie.workbench.common.widgets.client.callbacks.Callback;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.resources.i18n.HumanReadableConstants;
import org.uberfire.client.common.ClickableLabel;
import org.uberfire.client.common.FormStylePopup;
import org.uberfire.client.common.SmallLabel;

public class ExpressionBuilder extends RuleModellerWidget
        implements
        HasExpressionTypeChangeHandlers,
        HasExpressionChangeHandlers {

    private static final String DELETE_VALUE = "_delete_";
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

        StringBuilder bindingLabel = new StringBuilder();
        String binding = getBoundText();
        if ( binding != null && !binding.equals( "" ) ) {
            bindingLabel.append( "<b>" );
            bindingLabel.append( getBoundText() );
            bindingLabel.append( "</b>" );
        }
        bindingLabel.append( expression.getText( false ) );

        if ( expression == null || expression.isEmpty() ) {
            if ( this.readOnly ) {
                panel.add( new SmallLabel( "<b>-</b>" ) );
            } else {
                panel.add( createStartPointWidget() );
            }
        } else {
            if ( this.readOnly ) {
                panel.add( createWidgetForExpression( bindingLabel.toString() ) );
            } else {
                bindingLabel.append( "." );
                panel.add( createWidgetForExpression( bindingLabel.toString() ) );
                panel.add( getWidgetForCurrentType() );
            }
        }
        initWidget( panel );
    }

    private String getBoundText() {
        if ( expression.isBound() ) {
            return "[" + expression.getBinding() + "] ";
        }
        return "";
    }

    private Widget createStartPointWidget() {
        ListBox startPoint = new ListBox();
        panel.add( startPoint );

        startPoint.addItem( Constants.INSTANCE.ChooseDotDotDot(),
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
                    ExpressionBuilder.this.makeDirty();
                    onStartPointChange( lb.getValue( index ) );
                }
            }
        } );
        return startPoint;
    }

    @Override
    public void makeDirty() {
        super.makeDirty();
        setModified( true );
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
            panel.add( createWidgetForExpression( expression.getText() + "." ) );
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

        ChangeHandler ch = new ChangeHandler() {
            public void onChange( ChangeEvent event ) {
                ListBox box = (ListBox) event.getSource();
                panel.remove( box );
                if ( box.getSelectedIndex() > 0 ) {
                    onChangeSelection( box.getValue( box.getSelectedIndex() ) );
                }
            }
        };

        ListBox lb = new ListBox();
        lb.setVisibleItemCount( 1 );
        lb.addItem( Constants.INSTANCE.ChooseDotDotDot(),
                    "" );
        lb.addItem( "<==" + Constants.INSTANCE.DeleteItem(),
                    DELETE_VALUE );
        for ( Map.Entry<String, String> entry : getCompletionsForCurrentType( expression.getParts().size() > 1 ).entrySet() ) {
            lb.addItem( entry.getKey(),
                        entry.getValue() );
        }
        lb.addChangeHandler( ch );
        return lb;
    }

    private void onCollectionChange( String value ) {
        if ( "size".contains( value ) ) {
            expression.appendPart( new ExpressionMethod( "size",
                                                         "int",
                                                         DataType.TYPE_NUMERIC_INTEGER ) );
        } else if ( "isEmpty".equals( value ) ) {
            expression.appendPart( new ExpressionMethod( "isEmpty",
                                                         "boolean",
                                                         DataType.TYPE_BOOLEAN ) );
        } else {
            ExpressionCollectionIndex collectionIndex;
            String factName = getDataModelOracle().getFactNameFromType( getCurrentParametricType() );
            if ( getCurrentParametricType() != null && factName != null ) {
                collectionIndex = new ExpressionCollectionIndex( "get",
                                                                 getCurrentParametricType(),
                                                                 factName );
            } else {
                collectionIndex = new ExpressionCollectionIndex( "get",
                                                                 "java.lang.Object",
                                                                 DataType.TYPE_OBJECT );
            }
            if ( "first".equals( value ) ) {
                collectionIndex.putParam( "index",
                                          new ExpressionFormLine( new ExpressionText( "0" ) ) );
                expression.appendPart( collectionIndex );
            } else if ( "last".equals( value ) ) {
                ExpressionFormLine index = new ExpressionFormLine( expression );
                index.appendPart( new ExpressionMethod( "size",
                                                        "int",
                                                        DataType.TYPE_NUMERIC_INTEGER ) );
                index.appendPart( new ExpressionText( "-1" ) );

                collectionIndex.putParam( "index",
                                          index );
                expression.appendPart( collectionIndex );
            }
        }
    }

    private void onChangeSelection( String value ) {
        setModified( true );
        String prevFactName = null;
        final String oldType = getCurrentGenericType();

        if ( DELETE_VALUE.equals( value ) ) {
            expression.removeLast();
        } else if ( DataType.TYPE_COLLECTION.equals( getCurrentGenericType() ) ) {
            onCollectionChange( value );
        } else if ( DataType.TYPE_STRING.equals( getCurrentGenericType() ) ) {
            if ( "size".equals( value ) ) {
                expression.appendPart( new ExpressionMethod( "size",
                                                             "int",
                                                             DataType.TYPE_NUMERIC_INTEGER ) );
            } else if ( "isEmpty".equals( value ) ) {
                expression.appendPart( new ExpressionText( ".size() == 0",
                                                           "",
                                                           DataType.TYPE_NUMERIC_INTEGER ) );
            }
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
            panel.add( createWidgetForExpression( expression.getText() + "." ) );
        }
        if ( w != null ) {
            panel.add( w );
        }
        fireExpressionChangeEvent();
        fireExpressionTypeChangeEvent( oldType );
    }

    private Map<String, String> getCompletionsForCurrentType( final boolean isNested ) {
        final Map<String, String> completions = new LinkedHashMap<String, String>();

        if ( DataType.TYPE_FINAL_OBJECT.equals( getCurrentGenericType() ) ) {
            return completions;
        }

        if ( DataType.TYPE_COLLECTION.equals( getCurrentGenericType() ) ) {
            completions.put( "size()",
                             "size" );
            completions.put( "first()",
                             "first" );
            completions.put( "last()",
                             "last" );
            completions.put( "isEmpty()",
                             "isEmpty" );
            return completions;
        }

        if ( DataType.TYPE_STRING.equals( getCurrentGenericType() ) ) {
            completions.put( "size()",
                             "size" );
            completions.put( "isEmpty()",
                             "isEmpty" );
            return completions;
        }

        if ( DataType.TYPE_BOOLEAN.equals( getCurrentGenericType() )
                || DataType.TYPE_NUMERIC_BIGDECIMAL.equals( getCurrentGenericType() )
                || DataType.TYPE_NUMERIC_BIGINTEGER.equals( getCurrentGenericType() )
                || DataType.TYPE_NUMERIC_BYTE.equals( getCurrentGenericType() )
                || DataType.TYPE_NUMERIC_DOUBLE.equals( getCurrentGenericType() )
                || DataType.TYPE_NUMERIC_FLOAT.equals( getCurrentGenericType() )
                || DataType.TYPE_NUMERIC_INTEGER.equals( getCurrentGenericType() )
                || DataType.TYPE_NUMERIC_LONG.equals( getCurrentGenericType() )
                || DataType.TYPE_NUMERIC_SHORT.equals( getCurrentGenericType() )
                || DataType.TYPE_DATE.equals( getCurrentGenericType() )
                || DataType.TYPE_OBJECT.equals( getCurrentGenericType() ) ) {
            return completions;
        }

        final String factName = getDataModelOracle().getFactNameFromType( getCurrentClassType() );
        if ( factName != null ) {
            // we currently only support 0 param method calls
            getDataModelOracle().getMethodNames( factName,
                                                 0,
                                                 new Callback<List<String>>() {
                                                     @Override
                                                     public void callback( final List<String> methodNames ) {
                                                         getDataModelOracle().getFieldCompletions( factName,
                                                                                                   new Callback<ModelField[]>() {

                                                                                                       @Override
                                                                                                       public void callback( final ModelField[] fields ) {
                                                                                                           for ( ModelField field : fields ) {

                                                                                                               //You can't use "this" in a nested accessor
                                                                                                               final String fieldName = field.getName();
                                                                                                               if ( !isNested || !fieldName.equals( DataType.TYPE_THIS ) ) {

                                                                                                                   boolean changed = false;
                                                                                                                   for ( Iterator<String> i = methodNames.iterator(); i.hasNext(); ) {
                                                                                                                       String method = i.next();
                                                                                                                       if ( method.startsWith( fieldName ) ) {
                                                                                                                           completions.put( method,
                                                                                                                                            METHOD_VALUE_PREFIX + "." + method );
                                                                                                                           i.remove();
                                                                                                                           changed = true;
                                                                                                                       }
                                                                                                                   }
                                                                                                                   if ( !changed ) {
                                                                                                                       completions.put( fieldName,
                                                                                                                                        FIElD_VALUE_PREFIX + "." + fieldName );
                                                                                                                   }
                                                                                                               }
                                                                                                           }
                                                                                                       }
                                                                                                   } );
                                                     }
                                                 } );

        }
        // else {We don't know anything about this type, so return empty map}
        return completions;
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
        final FormStylePopup popup = new FormStylePopup();
        popup.setWidth( 500 + "px" );
        HorizontalPanel vn = new HorizontalPanel();
        final TextBox varName = new TextBox();
        Button ok = new Button( HumanReadableConstants.INSTANCE.Set() );
        vn.add( new Label( Constants.INSTANCE.BindTheExpressionToAVariable() ) );
        vn.add( varName );
        vn.add( ok );

        ok.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent event ) {
                String var = varName.getText();
                if ( getModeller().isVariableNameUsed( var ) ) {
                    Window.alert( Constants.INSTANCE.TheVariableName0IsAlreadyTaken( var ) );
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

    private ClickableLabel createWidgetForExpression( String text ) {
        ClickableLabel label = new ClickableLabel( text,
                                                   slch,
                                                   !this.readOnly );
        return label;
    }
}
