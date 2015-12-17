/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.screens.projecteditor.client.handlers;

import org.guvnor.common.services.project.service.PackageAlreadyExistsException;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.kie.workbench.common.screens.projecteditor.client.resources.ProjectEditorResources;
import org.uberfire.ext.widgets.common.client.callbacks.DefaultErrorCallback;
import org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup;

/**
 * Error handler for PackageAlreadyExistsException exceptions, delegating all others to DefaultErrorCallback
 */
public class NewPackageErrorCallback extends DefaultErrorCallback {

    @Override
    public boolean error( final Message message,
                          final Throwable throwable ) {
        try {
            throw throwable;

        } catch ( PackageAlreadyExistsException e ) {
            ErrorPopup.showMessage( ProjectEditorResources.CONSTANTS.ExceptionPackageAlreadyExists0( e.getFile() ) );

        } catch ( Throwable e ) {
            super.error( message,
                         throwable );
        }
        return false;
    }

}
