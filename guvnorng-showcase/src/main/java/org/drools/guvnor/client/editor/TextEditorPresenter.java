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

package org.drools.guvnor.client.editor;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.drools.guvnor.client.mvp.EditorService;
import org.drools.guvnor.client.mvp.PlaceManager;
import org.drools.guvnor.client.mvp.PlaceRequest;
import org.drools.guvnor.vfs.VFSService;
import org.drools.java.nio.file.ExtendedPath;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;

@Dependent
public class TextEditorPresenter implements EditorService {

    @Inject View view;
    @Inject Caller<VFSService> vfsServices;
    @Inject private PlaceManager placeManager;
    
    ExtendedPath path = null;
    
    @Override
    public void onStart() {
        PlaceRequest placeRequest = placeManager.getCurrentPlaceRequest();
        final String uriPath = placeRequest.getParameter("path", null);

        vfsServices.call(new RemoteCallback<ExtendedPath>() {
            @Override public void callback(ExtendedPath extendedPath) {
                vfsServices.call(new RemoteCallback<String>() {
                    @Override
                    public void callback(String response) {
                        if (response == null) {
                            view.setContent("-- empty --");
                        } else {
                            view.setContent(response);
                        }
                    }
                }).readAllString(extendedPath);
            }
        }).get(uriPath);
    }

    public interface View extends IsWidget {

        void setContent(String content);

        String getContent();

        void setFocus();

        void setDirty(boolean dirty);

        boolean isDirty();
    }

    public void doSave() {
        vfsServices.call(new RemoteCallback<ExtendedPath>() {
            @Override
            public void callback(ExtendedPath response) {
                view.setDirty(false);
            }
        }).write(path, view.getContent());
    }

    @Override
    public boolean isDirty() {
        return view.isDirty();
    }

    @Override public void onClose() {
        this.path = null;
    }

    @Override public boolean mayClose() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onReveal() {
        view.setFocus();
    }

    @Override public void onHide() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override public void mayOnHide() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}