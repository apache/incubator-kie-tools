/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.widgets;

import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.widgets.client.handlers.NewResourceHandler;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.mvp.Command;

import static org.kie.workbench.common.screens.library.client.util.ResourceUtils.*;

public class ProjectActionsWidget {

    public interface View extends UberElement<ProjectActionsWidget> {

        void addResourceHandler( final NewResourceHandler newResourceHandler );
    }

    private View view;

    private ManagedInstance<NewResourceHandler> newResourceHandlers;

    private NewResourcePresenter newResourcePresenter;

    private Command showSettingsCommand;

    @Inject
    public ProjectActionsWidget( final View view,
                                 final ManagedInstance<NewResourceHandler> newResourceHandlers,
                                 final NewResourcePresenter newResourcePresenter ) {
        this.view = view;
        this.newResourceHandlers = newResourceHandlers;
        this.newResourcePresenter = newResourcePresenter;
    }

    public void init( final Command showSettingsCommand ) {
        this.showSettingsCommand = showSettingsCommand;

        view.init( this );
        for ( NewResourceHandler newResourceHandler : getNewResourceHandlers() ) {
            if ( newResourceHandler.canCreate()
                    && !isPackageHandler( newResourceHandler )
                    && !isProjectHandler( newResourceHandler ) ) {
                view.addResourceHandler( newResourceHandler );
            }
        }
    }

    public void goToProjectSettings() {
        showSettingsCommand.execute();
    }

    public NewResourcePresenter getNewResourcePresenter() {
        return newResourcePresenter;
    }

    public View getView() {
        return view;
    }

    Iterable<NewResourceHandler> getNewResourceHandlers() {
        return newResourceHandlers;
    }
}
