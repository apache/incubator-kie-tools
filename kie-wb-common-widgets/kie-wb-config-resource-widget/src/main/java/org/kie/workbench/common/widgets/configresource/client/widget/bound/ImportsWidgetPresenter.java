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
import org.kie.workbench.common.services.datamodel.events.ImportAddedEvent;
import org.kie.workbench.common.services.datamodel.events.ImportRemovedEvent;
import org.kie.workbench.common.services.datamodel.oracle.PackageDataModelOracle;
import org.kie.workbench.common.widgets.client.popups.list.FormListPopup;

import static org.kie.commons.validation.PortablePreconditions.*;

public class ImportsWidgetPresenter implements ImportsWidgetView.Presenter,
                                               IsWidget {

    private ImportsWidgetView view;
    private FormListPopup addImportPopup;

    private Event<ImportAddedEvent> importAddedEvent;
    private Event<ImportRemovedEvent> importRemovedEvent;

    private PackageDataModelOracle oracle;
    private Imports resourceImports;

    public ImportsWidgetPresenter() {
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
        view.init( this );
    }

    @Override
    public void setContent( final PackageDataModelOracle oracle,
                            final Imports resourceImports,
                            final boolean isReadOnly ) {
        this.oracle = checkNotNull( "oracle",
                                    oracle );
        this.resourceImports = checkNotNull( "resourceImports",
                                             resourceImports );

        //Get list of potential imports
        final List<Import> allAvailableImportTypes = new ArrayList<Import>();
        for ( String importType : oracle.getExternalFactTypes() ) {
            allAvailableImportTypes.add( new Import(importType) );
        }

        view.setContent( allAvailableImportTypes,
                         resourceImports.getImports(),
                         isReadOnly );
    }

    @Override
    public void onAddImport( final Import importType ) {
        //resourceImports.getImports().add( importType );
        oracle.filter();

        //Signal change to any other interested consumers (e.g. some editors support rendering of unknown fact-types)
        importAddedEvent.fire( new ImportAddedEvent( oracle,
                                                     importType ) );
    }

    @Override
    public void onRemoveImport( final Import importType ) {
        //resourceImports.removeImport( importType );
        oracle.filter();

        //Signal change to any other interested consumers (e.g. some editors support rendering of unknown fact-types)
        importRemovedEvent.fire( new ImportRemovedEvent( oracle,
                                                         importType ) );
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    @Override
    public boolean isDirty() {
        return view.isDirty();
    }

    @Override
    public void setNotDirty() {
        view.setNotDirty();
    }

}
