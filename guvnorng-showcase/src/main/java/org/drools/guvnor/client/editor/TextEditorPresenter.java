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

import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.guvnor.client.mvp.ScreenService;
import org.drools.guvnor.vfs.VFSService;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;

@Dependent
public class TextEditorPresenter implements ScreenService {

    @Inject View view;
    @Inject Caller<VFSService> vfsServices;

    public interface View extends IsWidget {

        void setContent(String content);

        String getContent();

        void setFocus();

        void setDirty(boolean dirty);

        boolean isDirty();
    }

    public void doSave() {
//        artifactService.call(new ResponseCallback() {
//            @Override
//            public void callback(Response response) {
//                if (response.getStatusCode() == Response.SC_NO_CONTENT) {
//                    view.setDirty(false);
//                } else {
//                    //error
//                }
//            }
//        }).save(getInput().getId(), view.getContent());
    }

    //    @Override
    public boolean isDirty() {
        return view.isDirty();
    }

    //    @Override
    public void createPartControl(AcceptsOneWidget container) {
        container.setWidget(view);
        loadContent();
    }

    public void loadContent() {
//        artifactService.call(new RemoteCallback<String>() {
//            @Override
//            public void callback(String content) {
//                if (content != null) {
//                    view.setContent(content);
//                } else {
//                    view.setContent("-- empty --");
//                }
//            }
//        }).getArtifactContent(getInput().getId());
        vfsServices.call(new RemoteCallback<String>() {
            @Override public void callback(String response) {
                if (response == null) {
                    view.setContent("-- empty --");
                } else {
                    view.setContent(response);
                }
            }
        }).readAllString(null);
    }

    //    @Override
    public String getName() {
        return "org.drools.guvnor.client.content.editor.TextEditor";
    }

    //    @Override
    public void dispose() {
    }

    //    @Override
    public void setFocus() {
        view.setFocus();
    }

    @Override public void onStart() {
        loadContent();
    }

    @Override public void onClose() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override public boolean mayClose() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override public void onReveal() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override public void onHide() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override public void mayOnHide() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}