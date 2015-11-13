/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.widgets.sandbox.client.markdown.viewer;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.lifecycle.OnStartup;

@Dependent
@WorkbenchEditor(identifier = "MarkdownViewer", supportedTypes = { MarkdownType.class })
public class MarkdownPresenter {

    public interface View
            extends
            IsWidget {

        void setContent( final String htmlContent );
    }

    @Inject
    private Caller<VFSService> vfsServices;

    @Inject
    public View view;

    private String path;

    @OnStartup
    public void onStartup( final Path path ) {
        this.path = path.getFileName();
        vfsServices.call( new RemoteCallback<String>() {
            @Override
            public void callback( final String response ) {
                if ( response == null ) {
                    view.setContent( "<p>-- empty --</p>" );
                } else {
                    view.setContent( response );
                }
            }
        } ).readAllString( path );
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Markdown Viewer [" + path + "]";
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return view;
    }

}
