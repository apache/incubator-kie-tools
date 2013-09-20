/*
 * Copyright 2013 JBoss Inc
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

package org.kie.workbench.common.screens.javaeditor.client.editor;

import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.common.services.shared.metadata.MetadataService;
import org.jboss.errai.common.client.api.Caller;
import org.kie.workbench.common.screens.javaeditor.client.type.JavaResourceType;
import org.kie.workbench.common.widgets.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.metadata.client.callbacks.MetadataSuccessCallback;
import org.kie.workbench.common.widgets.metadata.client.widget.MetadataWidget;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.common.MultiPageEditor;
import org.uberfire.client.common.Page;
import org.uberfire.client.editors.texteditor.TextEditorPresenter;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.workbench.type.FileNameUtil;

import static org.kie.commons.validation.PortablePreconditions.*;

@WorkbenchEditor(identifier = "JavaEditor", supportedTypes = { JavaResourceType.class })
public class JavaEditorPresenter
        extends TextEditorPresenter {

    private boolean isReadOnly = true;

    @Inject
    private Caller<MetadataService> metadataService;

    @Inject
    private MetadataWidget metadataWidget;

    @Inject
    private MultiPageEditor multiPage;

    @Inject
    private JavaResourceType type;

    @OnStartup
    public void init( final Path path ) {
        this.path = checkNotNull( "path", path );

        super.onStartup( path );

        this.path = path;

        IsWidget widget = super.getWidget();
        multiPage.addWidget(
                widget,
                CommonConstants.INSTANCE.EditTabTitle() );

        multiPage.addPage( new Page( metadataWidget,
                                     CommonConstants.INSTANCE.MetadataTabTitle() ) {
            @Override
            public void onFocus() {
                metadataWidget.showBusyIndicator( CommonConstants.INSTANCE.Loading() );
                metadataService.call( new MetadataSuccessCallback( metadataWidget,
                                                                   isReadOnly ),
                                      new HasBusyIndicatorDefaultErrorCallback( metadataWidget ) ).getMetadata( path );
            }

            @Override
            public void onLostFocus() {
                //Nothing to do
            }
        } );
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Java Editor [" + FileNameUtil.removeExtension( path,
                                                               type ) + "]";
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return multiPage;
    }

}
