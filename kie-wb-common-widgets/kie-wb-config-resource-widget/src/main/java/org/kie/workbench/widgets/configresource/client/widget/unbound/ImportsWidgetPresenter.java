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

package org.kie.workbench.widgets.configresource.client.widget.unbound;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import org.drools.workbench.models.commons.shared.imports.Import;
import org.drools.workbench.models.commons.shared.imports.Imports;
import org.kie.workbench.widgets.common.client.popups.text.FormPopup;
import org.kie.workbench.widgets.common.client.popups.text.PopupSetFieldCommand;

import static org.kie.commons.validation.PortablePreconditions.*;

public class ImportsWidgetPresenter
        implements ImportsWidgetView.Presenter,
                   IsWidget {

    private final ImportsWidgetView view;
    private final FormPopup addImportPopup;

    private Imports resourceImports;

    @Inject
    public ImportsWidgetPresenter( final ImportsWidgetView view,
                                   final FormPopup addImportPopup ) {
        this.view = view;
        this.addImportPopup = addImportPopup;
        view.setPresenter( this );
    }

    @Override
    public void setContent( final Imports resourceImports,
                            final boolean isReadOnly ) {
        this.resourceImports = checkNotNull( "resourceImports",
                                             resourceImports );

        view.setReadOnly( isReadOnly );

        for ( Import item : resourceImports.getImports() ) {
            view.addImport( item.getType() );
        }
    }

    @Override
    public void onAddImport() {
        addImportPopup.show( new PopupSetFieldCommand() {
            @Override
            public void setName( String name ) {
                final Import item = new Import( name );
                view.addImport( name );
                resourceImports.getImports().add( item );
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
