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

import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.client.annotations.OnStart;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;

@Dependent
@WorkbenchEditor(identifier = "RepositoryEditor")
public class RepositoryEditorPresenter {

    @Inject
    Caller<VFSService> vfsService;

    private Path path = null;

    public interface View
            extends
            IsWidget {

        void addRepository(String repositoryName,
                String gitURL,
                String description,
                String link);

        void clear();
    }

    @Inject
    public View view;

    public RepositoryEditorPresenter() {
    }

    @OnStart
    public void onStart(final Path path) {
        this.path = path;

        vfsService.call(new RemoteCallback<Map>() {
            @Override
            public void callback(Map response) {
                view.clear();
                view.addRepository( path.getFileName(),
                                    (String) response.get("giturl"),
                                    (String) response.get("description"),
                                    path.toURI() );
            }
        }).readAttributes(this.path);
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "RepositoryEditor [" + path.getFileName() + "]";
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return view;
    }

}