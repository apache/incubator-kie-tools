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

package org.drools.guvnor.client.editors.repositorieseditor;

import java.util.Collection;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.guvnor.backend.FileExplorerRootService;
import org.drools.guvnor.backend.Root;
import org.drools.guvnor.backend.VFSService;
import org.drools.guvnor.client.annotations.OnStart;
import org.drools.guvnor.client.annotations.WorkbenchPartTitle;
import org.drools.guvnor.client.annotations.WorkbenchPartView;
import org.drools.guvnor.client.annotations.WorkbenchScreen;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.jboss.errai.ioc.client.container.IOCBeanManager;

@Dependent
@WorkbenchScreen(identifier = "RepositoriesEditor")
public class RepositoriesEditorPresenter {

    @Inject
    private Caller<VFSService>              vfsService;

    @Inject
    private Caller<FileExplorerRootService> rootService;

    @Inject
    private IOCBeanManager                  iocManager;

    public interface View
        extends
        IsWidget {

        void addRepository(String repositoryName,
                           String gitURL,
                           String description,
                           String link);

        void clear();

        Button getCreateRepoButton();

        Button getCloneRepoButton();
    }

    @Inject
    public View view;

    public RepositoriesEditorPresenter() {
    }

    @OnStart
    public void onStart() {

        view.clear();

        rootService.call(new RemoteCallback<Collection<Root>>() {
            @Override public void callback(Collection<Root> response) {
                for ( final Root root : response ) {
                    vfsService.call(new RemoteCallback<Map>() {
                        @Override
                        public void callback(Map response) {
                            view.addRepository( root.getPath().getFileName(),
                                    (String) response.get("giturl"),
                                    (String) response.get("description"),
                                    root.getPath().toURI() );
                        }
                    }).readAttributes(root.getPath());
                }
            }
        }).listRoots();

        view.getCreateRepoButton().addClickHandler( new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                NewRepositoryWizard newRepositoryWizard = iocManager.lookupBean( NewRepositoryWizard.class ).getInstance();
                newRepositoryWizard.show();
            }
        } );

        view.getCloneRepoButton().addClickHandler( new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                CloneRepositoryWizard cloneRepositoryWizard = iocManager.lookupBean( CloneRepositoryWizard.class ).getInstance();
                cloneRepositoryWizard.show();
            }
        } );
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "RepositoriesEditor";
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return view;
    }

    public void newRootDirectory(@Observes final Root root) {
        vfsService.call(new RemoteCallback<Map>() {
            @Override
            public void callback(Map response) {
                view.addRepository( root.getPath().getFileName(),
                        (String) response.get("giturl"),
                        (String) response.get("description"),
                        root.getPath().toURI() );
            }
        }).readAttributes(root.getPath());
    }
}