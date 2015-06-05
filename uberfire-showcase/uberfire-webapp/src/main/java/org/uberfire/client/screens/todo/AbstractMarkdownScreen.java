/*
 *
 *  * Copyright 2012 JBoss Inc
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 *  * use this file except in compliance with the License. You may obtain a copy of
 *  * the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  * License for the specific language governing permissions and limitations under
 *  * the License.
 *
 */

package org.uberfire.client.screens.todo;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.client.annotations.DefaultPosition;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.Position;

/**
 * Created by Cristiano Nicolai.
 */
public abstract class AbstractMarkdownScreen extends Composite implements RequiresResize {

    protected static final String EMPTY = "<p>-- empty --</p>";

    protected HTML markdown = new HTML(EMPTY);

    @Inject
    protected Caller<VFSService> vfsServices;

    @PostConstruct
    public void init() {
        vfsServices.call(new RemoteCallback<Path>() {
            @Override
            public void callback(final Path o) {
                vfsServices.call(new RemoteCallback<String>() {
                    @Override
                    public void callback(final String response) {
                        if (response == null) {
                            setContent(EMPTY);
                        } else {
                            try {
                                setContent(parseMarkdown(response));
                            } catch (Exception e) {
                                setContent(EMPTY);
                                GWT.log("Error parsing markdown content", e);
                            }
                        }
                    }
                }).readAllString(o);
            }
        }).get(getMarkdownFileURI());
        markdown.getElement().getStyle().setPadding( 15, Style.Unit.PX );
        initWidget( markdown );
    }

    @WorkbenchPartView
    public Widget getView() {
        return this;
    }

    @DefaultPosition
    public Position getDefaultPosition() {
        return CompassPosition.EAST;
    }

    public abstract String getMarkdownFileURI();

    protected void setContent(final String content) {
        this.markdown.setHTML(content);
    }

    public static native String parseMarkdown(String content)/*-{
        return $wnd.marked(content);
    }-*/;

    @Override
    public void onResize() {
        int height = getParent().getOffsetHeight();
        int width = getParent().getOffsetWidth();
        setPixelSize( width, height );
    }
}
