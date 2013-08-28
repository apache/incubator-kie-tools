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

package org.uberfire.client.editors.repository.edit;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.common.client.api.Caller;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.repositories.RepositoryService;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.mvp.PlaceRequest;

@Dependent
@WorkbenchScreen(identifier = "RepositoryEditor")
public class RepositoryEditorPresenter {

    @Inject
    Caller<RepositoryService> repositoryService;

    private String alias = null;

    public interface View
            extends
            IsWidget {

        void addRepository( String repositoryName,
                            String gitURL,
                            String description,
                            String link );

        void clear();
    }

    @Inject
    public View view;

    public RepositoryEditorPresenter() {
    }

    @OnStartup
    public void onStartup( final PlaceRequest place ) {
        this.alias = place.getParameters().get( "alias" );

        repositoryService.call( new RemoteCallback<Repository>() {
            @Override
            public void callback( final Repository repo ) {
                view.clear();
                view.addRepository( repo.getAlias(),
                                    repo.getUri(),
                                    "[empty]",
                                    repo.getRoot().toURI() );

            }
        } ).getRepository( alias );
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "RepositoryEditor [" + alias + "]";
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return view;
    }

}