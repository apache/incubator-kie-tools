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
import java.util.Map;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.drools.guvnor.client.common.Util;
import org.drools.guvnor.client.mvp.StaticScreenService;
import org.drools.guvnor.vfs.JGitRepositoryConfigurationVO;
import org.drools.guvnor.vfs.Path;
import org.drools.guvnor.vfs.VFSService;
import org.drools.guvnor.vfs.VFSTempUtil;
import org.drools.guvnor.vfs.impl.PathImpl;
import org.drools.java.nio.file.DirectoryStream;
import org.drools.java.nio.file.attribute.BasicFileAttributes;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.TreeItem;

@Dependent
public class RepositoriesEditorPresenter implements StaticScreenService {
    
    @Inject
    Caller<VFSService>   vfsService;
    
    public interface View
        extends
        IsWidget {

        void addRepository(String repositoryName, String gitURL, String description, String link);
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
                    view.addRepository(r.getRepositoryName(), r.getGitURL(), r.getDescription(), r.getRootURI());
                }
            }
        } ).listJGitRepositories();
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