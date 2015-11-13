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

package org.uberfire.ext.widgets.sandbox.client.markdown.editorlive;

import javax.annotation.PostConstruct;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ResizeLayoutPanel;
import org.uberfire.ext.widgets.sandbox.client.markdown.Markdown;

public class MarkdownLiveView extends Composite
        implements
        RequiresResize,
        MarkdownLivePresenter.View {

    interface MarkdownViewBinder
            extends
            UiBinder<ResizeLayoutPanel, MarkdownLiveView> {

    }

    private static MarkdownViewBinder uiBinder = GWT.create( MarkdownViewBinder.class );

    @UiField
    protected Markdown markdown;

    @PostConstruct
    public void init() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    public void setContent( final String content ) {
        markdown.setContent( content );
    }

    @Override
    public void onResize() {
        int height = getParent().getOffsetHeight();
        int width = getParent().getOffsetWidth();
        setPixelSize( width, height );
    }

}