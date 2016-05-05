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

package org.uberfire.ext.widgets.core.client.editors.defaulteditor;

import javax.annotation.PostConstruct;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Container;
import org.uberfire.backend.vfs.Path;

public class DefaultFileEditorView
        extends Composite
        implements DefaultFileEditorPresenter.View {

    interface DefaultFileEditorViewBinder
            extends
            UiBinder<Container, DefaultFileEditorView> {

    }

    private static DefaultFileEditorViewBinder uiBinder = GWT.create(DefaultFileEditorViewBinder.class);

    @UiField
    DefaultEditorFileUpload fileUpload;

    @UiField
    Button downloadButton;

    @PostConstruct
    public void init() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void setPath(Path path) {
        fileUpload.setPath(path);
    }

    @UiHandler("downloadButton")
    public void handleClick(ClickEvent event) {
        fileUpload.download();
    }

}
