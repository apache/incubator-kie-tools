/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.editor.commons.client.file.popups.commons;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import org.gwtbootstrap3.client.ui.Anchor;
import org.gwtbootstrap3.client.ui.TextBox;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.ext.editor.commons.client.resources.i18n.Constants;

@Dependent
@Templated
public class ToggleCommentView implements ToggleCommentPresenter.View,
                                          IsElement {

    @Inject
    @DataField("addComment")
    Anchor addComment;

    @Inject
    @DataField("commentTextBox")
    TextBox commentTextBox;

    @Inject
    private TranslationService translationService;

    private ToggleCommentPresenter presenter;

    @Override
    public void init( final ToggleCommentPresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public String getComment() {
        return commentTextBox.getValue();
    }

    @PostConstruct
    public void setup() {
        commentTextBox.setText( "" );
        commentTextBox.setVisible( false );
        commentTextBox.setPlaceholder( translationService.format( Constants.ToggleCommentView_EnterComment ) );
    }

    @EventHandler("addComment")
    public void addComment( ClickEvent event ) {
        toggleCommentTextBox();
        event.preventDefault();
    }

    private void toggleCommentTextBox() {
        commentTextBox.setVisible( !commentTextBox.isVisible() );
    }
}
