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

package org.uberfire.client.markdown;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

public class Markdown extends Composite
        implements
        RequiresResize {

    interface MarkdownBinder
            extends
            UiBinder<Widget, Markdown> {

    }

    private static MarkdownBinder uiBinder = GWT.create( MarkdownBinder.class );

    @UiField
    HTMLPanel htmlContent;

    HTML content;

    public Markdown() {
        initWidget( uiBinder.createAndBindUi( this ) );
        content = new HTML();
        htmlContent.add( content );
    }

    public void setContent( final String content ) {
        this.content.setHTML( toHTML( content ) );
    }

    @Override
    public void onResize() {
//        int height = getParent().getOffsetHeight();
//        int width = getParent().getOffsetWidth();
//        setPixelSize( width, height );
    }

    public native String toHTML( final String text ) /*-{
        var converter = new $wnd.Showdown.converter();
        return converter.makeHtml(text);
    }-*/;

}