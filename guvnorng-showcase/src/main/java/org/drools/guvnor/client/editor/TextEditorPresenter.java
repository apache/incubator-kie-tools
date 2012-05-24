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

import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.guvnor.client.mvp.AcceptItem;
import org.drools.guvnor.client.mvp.Activity;
import org.drools.guvnor.client.workbench.Position;
import org.drools.guvnor.shared.ArtifactService;
import org.jboss.errai.ioc.client.api.Caller;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class TextEditorPresenter implements Activity {

    @Override
    public String getNameToken() {
        return "TextEditor";
    }

    @Override
    public void start(AcceptItem tabbedPanel) {
        tabbedPanel.add("Text editor", view);
    }

    @Override
    public boolean mayStop() {
        return true;
    }

    @Override
    public void onStop() {
        //TODO: -Rikkola-
    }

    @Override
    public Position getPreferredPosition() {
        return Position.SELF;
    }

    public interface View extends IsWidget {

        void setContent(String content);

        String getContent();

        void setFocus();

        void setDirty(boolean dirty);

        boolean isDirty();
    }

    @Inject
    View view;
    @Inject
    Caller<ArtifactService> artifactService;

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

    @Override
    public void revealPlace(AcceptItem acceptPanel) {
        acceptPanel.add("Text editor", view);        
    }
    
}