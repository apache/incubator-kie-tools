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

package org.uberfire.client.editors.repositoryeditor;

import javax.annotation.PostConstruct;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

public class RepositoryEditorView extends Composite
    implements
    RequiresResize,
    RepositoryEditorPresenter.View {

    interface RepositoryEditorViewBinder
        extends
        UiBinder<Widget, RepositoryEditorView> {
    }

    private static RepositoryEditorViewBinder uiBinder = GWT.create( RepositoryEditorViewBinder.class );

    @UiField
    public HTMLPanel                          panel;

    @PostConstruct
    public void init() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    public void addRepository(String repositoryName,
                              String gitURL,
                              String description,
                              String link) {
        panel.add( new HTML( "<li>" +
                               "<h3>" + repositoryName + "</h3>" +
                               "<div>" +
                                   "<p> Origin: <a href=\"" + gitURL + "\" target=\"_blank\">" + gitURL + "</a></p>" +
                                   "<p> Description: " + description + "</p>" +
                                   "<p >Last updated: </p>" +
                             "</div>" +
                             "</li>"

        ) );
    }

    @Override
    public void clear() {
        panel.clear();
    }

    @Override
    public void onResize() {
        int height = getParent().getOffsetHeight();
        int width = getParent().getOffsetWidth();
        panel.setPixelSize( width,
                            height );
    }

}