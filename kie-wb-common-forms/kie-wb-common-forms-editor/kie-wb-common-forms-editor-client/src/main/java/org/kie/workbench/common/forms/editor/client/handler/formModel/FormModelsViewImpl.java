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

package org.kie.workbench.common.forms.editor.client.handler.formModel;

import java.util.List;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Composite;
import org.gwtbootstrap3.client.ui.Column;
import org.gwtbootstrap3.client.ui.Radio;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.constants.ColumnSize;
import org.gwtbootstrap3.client.ui.gwt.FlowPanel;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.forms.model.FormModel;

@Templated
public class FormModelsViewImpl extends Composite implements FormModelsView {

    @Inject
    @DataField
    private FlowPanel content;

    private FormModelCreationView currentCreationView;

    protected List<FormModelCreationView> creationViews;

    @Override
    public void setCreationViews( List<FormModelCreationView> creationViews ) {
        this.creationViews = creationViews;

        render();
    }

    protected void render() {
        content.clear();

        if ( creationViews.size() == 1 ) {
            currentCreationView = creationViews.get( 0 );
            content.add( currentCreationView );
        } else {
            currentCreationView = null;
            for ( FormModelCreationView view : creationViews ) {
                Row row = new Row();
                Column col = new Column( ColumnSize.MD_12 );
                Radio button = new Radio( "creationView" );
                button.setText( view.getLabel() );
                button.addClickHandler( event -> setCurrentView( view ) );
                col.add( button );
                row.add( col );
                content.add( row );
                row = new Row();
                col = new Column( ColumnSize.MD_12 );
                view.asWidget().setVisible( false );
                col.add( view );
                row.add( col );
                content.add( row );
            }
        }
    }

    protected void setCurrentView( FormModelCreationView view ) {
        if ( currentCreationView != null ) {
            currentCreationView.asWidget().setVisible( false );
        }
        currentCreationView = view;
        currentCreationView.asWidget().setVisible( true );
    }

    @Override
    public boolean isValid() {
        if ( currentCreationView != null ) {
            return currentCreationView.isValid();
        }
        return true;
    }

    @Override
    public FormModel getFormModel() {
        if ( currentCreationView != null ) {
            return currentCreationView.getFormModel();
        }
        return null;
    }

    @Override
    public void reset() {
        render();
    }
}
