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

package org.kie.workbench.common.widgets.configresource.client.widget.bound;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.models.commons.shared.imports.Import;
import org.drools.workbench.models.commons.shared.imports.Imports;
import org.kie.commons.data.Pair;
import org.kie.workbench.common.services.datamodel.events.ImportAddedEvent;
import org.kie.workbench.common.services.datamodel.events.ImportRemovedEvent;
import org.kie.workbench.common.services.datamodel.oracle.PackageDataModelOracle;
import org.kie.workbench.common.widgets.client.popups.list.FormListPopup;
import org.kie.workbench.common.widgets.client.popups.list.PopupItemSelectedCommand;

import static org.kie.commons.validation.PortablePreconditions.*;

public class ImportsWidgetPresenter
        implements ImportsWidgetView.Presenter,
                   IsWidget {

    private ImportsWidgetView view;
    private FormListPopup addImportPopup;

    private Event<ImportAddedEvent> importAddedEvent;
    private Event<ImportRemovedEvent> importRemovedEvent;

    private PackageDataModelOracle oracle;
    private Imports resourceImports;
    private List<Pair<String, String>> imports;
    
    public ImportsWidgetPresenter( ) {
    } 

    @Inject
    public ImportsWidgetPresenter( final ImportsWidgetView view,
                                   final FormListPopup addImportPopup,
                                   final Event<ImportAddedEvent> importAddedEvent,
                                   final Event<ImportRemovedEvent> importRemovedEvent ) {
        this.view = view;
        this.addImportPopup = addImportPopup;
        this.importAddedEvent = importAddedEvent;
        this.importRemovedEvent = importRemovedEvent;
        view.setPresenter( this );
    }

    @Override
    public void setContent( final PackageDataModelOracle oracle,
                            final Imports resourceImports,
                            final boolean isReadOnly ) {
        this.oracle = checkNotNull( "oracle",
                                    oracle );
        this.resourceImports = checkNotNull( "resourceImports",
                                             resourceImports );

        view.setReadOnly( isReadOnly );

        //Add existing imports to view
        for ( Import item : resourceImports.getImports() ) {
            view.addImport( item.getType() );
        }

        //Get list of potential imports
        imports = new ArrayList<Pair<String, String>>();
        for ( String item : oracle.getExternalFactTypes() ) {
            Pair<String, String> pair = new Pair( item,
                                                  item );
            imports.add( pair );
        }
    }

    @Override
    public void onAddImport() {
        addImportPopup.show( imports,
                             new PopupItemSelectedCommand() {

                                 @Override
                                 public void setSelectedItem( final Pair<String, String> selectedItem ) {
                                     final String importClassName = selectedItem.getK1();
                                     final Import item = new Import( importClassName );
                                     view.addImport( importClassName );
                                     resourceImports.getImports().add( item );
                                     oracle.filter();

                                     //Signal change to any other interested consumers (e.g. some editors support rendering of unknown fact-types)
                                     importAddedEvent.fire( new ImportAddedEvent( oracle,
                                                                                  item ) );
                                 }

                             } );
    }

    @Override
    public void onRemoveImport() {
        String selected = view.getSelected();
        if ( selected == null ) {
            view.showPleaseSelectAnImport();
        } else {
            final Import item = new Import( selected );
            view.removeImport( selected );
            resourceImports.removeImport( item );
            oracle.filter();

            //Signal change to any other interested consumers (e.g. some editors support rendering of unknown fact-types)
            importRemovedEvent.fire( new ImportRemovedEvent( oracle,
                                                             item ) );
        }
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public boolean isDirty() {
        return false; // TODO: -Rikkola-
    }


    public void setNotDirty() {
        // TODO: -Rikkola-
    }

}
