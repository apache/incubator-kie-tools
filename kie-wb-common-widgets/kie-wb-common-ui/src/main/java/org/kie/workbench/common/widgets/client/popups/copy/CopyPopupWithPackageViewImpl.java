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

package org.kie.workbench.common.widgets.client.popups.copy;

import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.project.context.ProjectContext;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.utils.ProjectResourcePaths;
import org.gwtbootstrap3.client.shared.event.ModalShownEvent;
import org.gwtbootstrap3.client.shared.event.ModalShownHandler;
import org.gwtbootstrap3.client.ui.HelpBlock;
import org.kie.workbench.common.widgets.client.handlers.PackageListBox;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.client.file.CopyPopupViewImpl;
import org.uberfire.ext.editor.commons.client.resources.i18n.CommonConstants;
import org.uberfire.ext.widgets.common.client.common.FormStyleItem;

@Named("copyPopupWithPackageView")
public class CopyPopupWithPackageViewImpl extends CopyPopupViewImpl {

    private PackageListBox packageListBox;

    private final HelpBlock packageHelpInline = new HelpBlock();

    private FormStyleItem packageFormStyleItem;

    @Inject
    public CopyPopupWithPackageViewImpl( final PackageListBox packageListBox,
                                         final ProjectContext context ) {
        super();

        this.packageListBox = packageListBox;

        packageFormStyleItem = addAttribute( CommonConstants.INSTANCE.PackageColon(),
                                             this.packageListBox );
        packageFormStyleItem.getGroup().add( packageHelpInline );

        addShownHandler( new ModalShownHandler() {
            @Override
            public void onShown( ModalShownEvent shownEvent ) {
                final String path = presenter.getPath().toURI();

                if ( path.contains( ProjectResourcePaths.MAIN_RESOURCES_PATH )
                        || path.contains( ProjectResourcePaths.MAIN_SRC_PATH )
                        || path.contains( ProjectResourcePaths.TEST_RESOURCES_PATH )
                        || path.contains( ProjectResourcePaths.TEST_SRC_PATH ) ) {
                    packageListBox.setContext( context, true );
                    packageFormStyleItem.asWidget().setVisible( true );
                } else {
                    packageFormStyleItem.asWidget().setVisible( false );
                }
            }
        } );
    }

    @Override
    public Path getTargetPath() {

        final String path = presenter.getPath().toURI();
        final Package selectedPackage = packageListBox.getSelectedPackage();

        if ( path.contains( ProjectResourcePaths.MAIN_RESOURCES_PATH ) ) {
            return selectedPackage.getPackageMainResourcesPath();
        } else if ( path.contains( ProjectResourcePaths.MAIN_SRC_PATH ) ) {
            return selectedPackage.getPackageMainSrcPath();
        } else if ( path.contains( ProjectResourcePaths.TEST_RESOURCES_PATH ) ) {
            return selectedPackage.getPackageTestResourcesPath();
        } else if ( path.contains( ProjectResourcePaths.TEST_SRC_PATH ) ) {
            return selectedPackage.getPackageTestSrcPath();
        }

        return null;
    }

    public String getPackageName() {
        final Package selectedPackage = packageListBox.getSelectedPackage();

        if ( selectedPackage != null ) {
            return selectedPackage.getPackageName();
        }

        return null;
    }
}
