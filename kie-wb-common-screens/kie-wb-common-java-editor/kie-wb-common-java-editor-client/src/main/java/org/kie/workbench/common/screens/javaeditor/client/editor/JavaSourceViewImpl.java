/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.javaeditor.client.editor;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.google.gwt.user.client.Window;
import org.kie.workbench.common.screens.javaeditor.client.widget.ViewJavaSourceWidget;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.metadata.client.KieEditorViewImpl;

public class JavaSourceViewImpl
        extends KieEditorViewImpl
        implements JavaSourceView {

    @Inject
    private ViewJavaSourceWidget javaSourceViewer;

    @PostConstruct
    public void initialize() {
        initWidget( javaSourceViewer );
    }

    @Override
    public void setContent( final String content ) {
        javaSourceViewer.setContent( content );
    }

    @Override
    public void clear() {
        javaSourceViewer.clearContent();
    }

}
