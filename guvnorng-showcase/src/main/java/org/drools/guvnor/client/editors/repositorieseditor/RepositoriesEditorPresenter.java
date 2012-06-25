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

import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.drools.guvnor.client.mvp.StaticScreenService;
import org.drools.guvnor.vfs.JGitRepositoryConfigurationVO;
import org.drools.guvnor.vfs.VFSService;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.jboss.errai.ioc.client.container.IOCBeanManager;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.IsWidget;

@Dependent
public class RepositoriesEditorPresenter
    implements
    StaticScreenService {

    @Inject
    Caller<VFSService>     vfsService;
    
    @Inject
    private IOCBeanManager iocManager;

    public interface View
        extends
        IsWidget {
        void addRepository(String repositoryName,
                           String gitURL,
                           String description,
                           String link);

        Button getCreateRepoButton();

        Button getCloneRepoButton();
    }

    @Inject
    public View view;

    public RepositoriesEditorPresenter() {
    }

    @Override
    public void onStart() {
        vfsService.call( new RemoteCallback<List<JGitRepositoryConfigurationVO>>() {
            @Override
            public void callback(List<JGitRepositoryConfigurationVO> repositories) {
                for ( final JGitRepositoryConfigurationVO r : repositories ) {
                    String link = "#RepositoryEditor?gitURL=null&description=null&repositoryName="+r.getRepositoryName();
                    view.addRepository( r.getRepositoryName(),
                                        r.getGitURL(),
                                        r.getDescription(),
                                        link );
                }
            }
        } ).listJGitRepositories();

        view.getCreateRepoButton().addClickHandler(
                                                    new ClickHandler() {
                                                        @Override
                                                        public void onClick(ClickEvent event) {
                                                            NewRepositoryWizard newRepositoryWizard = iocManager.lookupBean( NewRepositoryWizard.class ).getInstance();
                                                            newRepositoryWizard.show();
                                                        }
                                                    } );

        view.getCloneRepoButton().addClickHandler(
                                                   new ClickHandler() {
                                                       @Override
                                                       public void onClick(ClickEvent event) {
                                                           CloneRepositoryWizard cloneRepositoryWizard = iocManager.lookupBean( CloneRepositoryWizard.class ).getInstance();
                                                           cloneRepositoryWizard.show();
                                                       }
                                                   } );
    }

    @Override
    public void onClose() {
    }

    @Override
    public boolean mayClose() {
        return true;
    }

    @Override
    public boolean mayHide() {
        return true;
    }

    @Override
    public void onReveal() {
    }

    @Override
    public void onHide() {
    }

}