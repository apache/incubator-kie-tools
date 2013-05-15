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

package org.kie.workbench.common.widgets.configresource.client.widget.unbound;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.widgets.configresource.client.resources.Images;
import org.kie.workbench.common.widgets.configresource.client.resources.i18n.ImportConstants;
import org.uberfire.client.common.BusyPopup;
import org.uberfire.client.common.ImageButton;

public class ImportsWidgetViewImpl
        extends Composite
        implements ImportsWidgetView {

    interface Binder
            extends UiBinder<Widget, ImportsWidgetViewImpl> {

    }

    private static Binder uiBinder = GWT.create( Binder.class );

    @UiField
    ListBox importsList;

    @UiField
    VerticalPanel buttonContainer;

    private ImageButton newImport;
    private ImageButton removeImport;

    private Presenter presenter;

    public ImportsWidgetViewImpl() {
        //Add buttons manually (rather that with UiBinder) otherwise we can't control whether their enabled or not
        this.newImport = new ImageButton( Images.INSTANCE.NewItem(),
                                          Images.INSTANCE.NewItemDisabled(),
                                          ImportConstants.INSTANCE.NewItem(),
                                          makeNewImportClickHandler() );
        this.removeImport = new ImageButton( Images.INSTANCE.Trash(),
                                             Images.INSTANCE.TrashDisabled(),
                                             ImportConstants.INSTANCE.Trash(),
                                             makeRemoveImportClickHandler() );
        initWidget( uiBinder.createAndBindUi( this ) );
        buttonContainer.add( newImport );
        buttonContainer.add( removeImport );
    }

    @Override
    public void setPresenter( Presenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void showPleaseSelectAnImport() {
        //TODO -Rikkola-
    }

    @Override
    public void addImport( String type ) {
        importsList.addItem( type );
    }

    @Override
    public String getSelected() {
        return importsList.getValue( importsList.getSelectedIndex() );
    }

    @Override
    public void removeImport( String selected ) {
        for ( int i = 0; i < importsList.getItemCount(); i++ ) {
            if ( importsList.getValue( i ).equals( selected ) ) {
                importsList.removeItem( i );
                break;
            }
        }
    }

    @Override
    public void setReadOnly( final boolean isReadOnly ) {
        importsList.setEnabled( !isReadOnly );
        newImport.setEnabled( !isReadOnly );
        removeImport.setEnabled( !isReadOnly );
    }

    private ClickHandler makeNewImportClickHandler() {
        return new ClickHandler() {
            @Override
            public void onClick( final ClickEvent event ) {
                presenter.onAddImport();
            }
        };
    }

    public ClickHandler makeRemoveImportClickHandler() {
        return new ClickHandler() {
            @Override
            public void onClick( final ClickEvent event ) {
                presenter.onRemoveImport();
            }
        };
    }

    @Override
    public void showBusyIndicator(String message) {
        BusyPopup.showMessage( message );
    }

    @Override
    public void hideBusyIndicator() {
        BusyPopup.close();
    }

}
