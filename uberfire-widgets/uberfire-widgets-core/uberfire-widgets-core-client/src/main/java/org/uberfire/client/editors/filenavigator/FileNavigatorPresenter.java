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

package org.uberfire.client.editors.filenavigator;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.backend.repositories.NewRepositoryEvent;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.repositories.RepositoryRemovedEvent;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;

@Dependent
@WorkbenchScreen(identifier = "FileNavigator")
public class FileNavigatorPresenter {

    @Inject
    private View view;

    public interface View
            extends
            IsWidget {

        void setFocus();

        void reset();

        void removeIfExists( final Repository repo );

        void addNewRepository( final Repository repo );
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return view;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Navigator";
    }

    public void newRootDirectory( @Observes NewRepositoryEvent event ) {
        view.addNewRepository( event.getNewRepository() );
    }

    public void removeRootDirectory( @Observes RepositoryRemovedEvent event ) {
        view.removeIfExists( event.getRepository() );
    }

}