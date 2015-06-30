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

import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.event.ShownEvent;
import com.github.gwtbootstrap.client.ui.event.ShownHandler;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterOKCancelButtons;

public class ValuePairEditorPopupViewImpl
        extends BaseModal
        implements ValuePairEditorPopupView {


    private Presenter presenter;

    private ValuePairEditor valuePairEditor;

    @Inject
    public ValuePairEditorPopupViewImpl( final ValuePairEditor valuePairEditor ) {

        this.valuePairEditor = valuePairEditor;
        valuePairEditor.showValidateButton( false );
        setTitle( "Value pair editor" );
        setMaxHeigth( "350px" );
        add( valuePairEditor );

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
        addShownHandler( new ShownHandler() {
            @Override public void onShown( ShownEvent shownEvent ) {
                valuePairEditor.refresh();
            }
        } );
    }

    @Override
    public void setPresenter( Presenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public String getName() {
        return valuePairEditor.getName();
    }

    @Override
    public void setName( String name ) {
        valuePairEditor.setName( name );
    }

    @Override
    public String getValue() {
        return valuePairEditor.getValue();
    }

    @Override
    public void setValue( String value ) {
        valuePairEditor.setValue( value );
    }

    @Override
    public String getAnnotationClassName() {
        return valuePairEditor.getAnnotationClassName();
    }

    @Override
    public void setAnnotationClassName( String annotationClassName ) {
        valuePairEditor.setAnnotationClassName( annotationClassName );
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
