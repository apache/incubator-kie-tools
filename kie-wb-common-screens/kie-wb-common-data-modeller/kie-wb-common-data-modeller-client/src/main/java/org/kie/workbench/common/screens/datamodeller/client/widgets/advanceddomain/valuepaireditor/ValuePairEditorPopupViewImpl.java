/*
 * Copyright 2015 JBoss Inc
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

package org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

import org.gwtbootstrap3.client.shared.event.ModalShownEvent;
import org.gwtbootstrap3.client.shared.event.ModalShownHandler;
import org.gwtbootstrap3.client.ui.Column;
import org.gwtbootstrap3.client.ui.Container;
import org.gwtbootstrap3.client.ui.Form;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.constants.ColumnSize;
import org.gwtbootstrap3.client.ui.constants.FormType;
import org.kie.workbench.common.screens.datamodeller.client.resources.i18n.Constants;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.booleans.BooleanValuePairEditor;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.enums.EnumValuePairEditor;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.enums.MultipleEnumValuePairEditor;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.generic.GenericValuePairEditor;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.multiple.MultipleValuePairEditor;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.numeric.NumericValuePairEditor;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.string.StringValuePairEditor;
import org.kie.workbench.common.services.datamodeller.core.AnnotationValuePairDefinition;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterOKCancelButtons;

public class ValuePairEditorPopupViewImpl
        extends BaseModal
        implements ValuePairEditorPopupView {

    private Presenter presenter;

    private ValuePairEditor valuePairEditor;

    private Form form = new Form();

    private Container container = new Container();

    private Row row = new Row();

    private Column column = new Column( ColumnSize.MD_12 );

    @Inject
    private ValuePairEditorProvider valuePairEditorProvider;

    @Inject
    public ValuePairEditorPopupViewImpl() {
        form.setType( FormType.HORIZONTAL );
        container.setFluid( true );
        container.add( row );
        row.add( column );
        column.add( form );
        setTitle( Constants.INSTANCE.advanced_domain_value_pair_editor_popup_title() );
        setBody( container );
        add( new ModalFooterOKCancelButtons(
                     new com.google.gwt.user.client.Command() {
                         @Override
                         public void execute() {
                             presenter.onOk();
                         }
                     },
                     new com.google.gwt.user.client.Command() {
                         @Override
                         public void execute() {
                             presenter.onCancel();
                         }
                     }
             )
           );
        addShownHandler( new ModalShownHandler() {
            @Override
            public void onShown( ModalShownEvent shownEvent ) {
                if ( valuePairEditor != null ) {
                    valuePairEditor.refresh();
                }
            }
        } );
    }

    @Override
    public void setPresenter( Presenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void init( AnnotationValuePairDefinition valuePairDefinition ) {
        valuePairEditor = valuePairEditorProvider.getValuePairEditor( valuePairDefinition );

        if ( valuePairEditor instanceof GenericValuePairEditor ) {
            valuePairEditor.showValidateButton( false );
        }
        form.add( valuePairEditor );

        valuePairEditor.addEditorHandler( new ValuePairEditorHandler() {
            @Override
            public void onValidate() {
                if ( valuePairEditor instanceof GenericValuePairEditor ) {
                    presenter.onValidate();
                }
            }

            @Override
            public void onValueChanged() {
                presenter.onValueChanged();
            }
        } );
    }

    @Override
    public boolean isGenericEditor() {
        return valuePairEditor instanceof GenericValuePairEditor;
    }

    @Override
    public boolean isValid() {
        return valuePairEditor.isValid();
    }

    @Override
    public Object getValue() {
        return valuePairEditor.getValue();
    }

    @Override
    public void setValue( Object value ) {
        //Not elegant
        if ( valuePairEditor instanceof MultipleValuePairEditor ) {
            List currentList = null;
            if ( value instanceof List ) {
                currentList = (List) value;
            } else if ( value != null ) {
                currentList = new ArrayList();
                currentList.add( value );
            }
            ( (MultipleValuePairEditor) valuePairEditor ).setValue( currentList );
        } else if ( valuePairEditor instanceof BooleanValuePairEditor ) {
            ( (BooleanValuePairEditor) valuePairEditor ).setValue( value != null ? Boolean.TRUE.equals( value ) : null );
        } else if ( valuePairEditor instanceof StringValuePairEditor ) {
            ( (StringValuePairEditor) valuePairEditor ).setValue( value != null ? value.toString() : null );
        } else if ( valuePairEditor instanceof EnumValuePairEditor ) {
            ( (EnumValuePairEditor) valuePairEditor ).setValue( value != null ? value.toString() : null );
        } else if ( valuePairEditor instanceof MultipleEnumValuePairEditor ) {
            List<String> enumValues = null;
            if ( value instanceof List ) {
                enumValues = new ArrayList<String>();
                for ( Object enumItem : ( (List) value ) ) {
                    enumValues.add( enumItem != null ? enumItem.toString() : null );
                }
            } else if ( value != null ) {
                enumValues = new ArrayList<String>();
                enumValues.add( value.toString() );
            }
            ( (MultipleEnumValuePairEditor) valuePairEditor ).setValue( enumValues );
        } else if ( valuePairEditor instanceof NumericValuePairEditor ) {
            ( (NumericValuePairEditor) valuePairEditor ).setValue( value );
        } else if ( valuePairEditor instanceof GenericValuePairEditor ) {
            ( (GenericValuePairEditor) valuePairEditor ).setValue( value != null ? value.toString() : null );
        }
    }

    @Override
    public void setErrorMessage( String errorMessage ) {
        valuePairEditor.setErrorMessage( errorMessage );
    }

    @Override
    public void clearErrorMessage() {
        valuePairEditor.clearErrorMessage();
    }

    @Override
    public void clear() {
        valuePairEditor.clear();
    }
}
