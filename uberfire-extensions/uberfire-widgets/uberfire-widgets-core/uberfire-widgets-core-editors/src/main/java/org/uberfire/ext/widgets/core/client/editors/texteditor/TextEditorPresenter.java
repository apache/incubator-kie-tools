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

package org.uberfire.ext.widgets.core.client.editors.texteditor;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.AbstractActivity;
import org.uberfire.ext.widgets.common.client.ace.AceEditorMode;
import org.uberfire.security.ResourceType;
import org.uberfire.workbench.model.ActivityResourceType;

@ApplicationScoped
//@Named(TextEditorPresenter.IDENTIFIER) uncomment after removing TextEditorPresenterActivity
public class TextEditorPresenter extends AbstractActivity {

    public static final String IDENTIFIER = "TextEditor";

    @Inject
    private View view;
    protected Path path;

    @Override
    public ResourceType getResourceType() {
        return ActivityResourceType.EDITOR;
    }

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public void onOpen() {
        super.onOpen();
        view.setFocus();
    }

    @Override
    public void onClose() {
        super.onClose();
        this.path = null;
    }

    @Override
    public IsWidget getWidget() {
        return view;
    }

    public interface View
            extends
            IsWidget {

        void setContent(final String content,
                        final AceEditorMode mode);

        String getContent();

        void setFocus();

        void setReadOnly(final boolean isReadOnly);
    }
}