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

package org.drools.guvnor.client.editors.texteditor;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.drools.guvnor.client.mvp.EditorService;
import org.drools.guvnor.vfs.Path;
import org.drools.guvnor.vfs.VFSService;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;

import com.google.gwt.user.client.ui.IsWidget;

@Dependent
public class TextEditorPresenter
    implements
    EditorService {

    public interface View
        extends
        IsWidget {

        void setContent(String content);

        String getContent();

        void setFocus();

        void setDirty(boolean dirty);

        boolean isDirty();
    }

    @Inject
    public View                view;

    @Inject
    private Caller<VFSService> vfsServices;

    private Path               path;

    @Override
    public void onStart(final Path path) {
        this.path = path;
        vfsServices.call( new RemoteCallback<String>() {
            @Override
            public void callback(String response) {
                if ( response == null ) {
                    view.setContent( "-- empty --" );
                } else {
                    view.setContent( response );
                }
            }
        } ).readAllString( path );
    }

    public void doSave() {
        vfsServices.call( new RemoteCallback<Path>() {
            @Override
            public void callback(Path response) {
                view.setDirty( false );
            }
        } ).write( path,
                   view.getContent() );
    }

    @Override
    public boolean isDirty() {
        return view.isDirty();
    }

    @Override
    public boolean onMayClose() {
        return true;
    }

    @Override
    public void onClose() {
        this.path = null;
    }

    @Override
    public void onReveal() {
        view.setFocus();
    }

    @Override
    public void onLostFocus() {
    }

    @Override
    public void onFocus() {
    }

}