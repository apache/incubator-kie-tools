/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.client.widgets.presenters.session.impl;

import com.google.gwt.safehtml.shared.annotations.IsSafeHtml;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHTML;
import org.uberfire.client.workbench.widgets.ResizeFlowPanel;

/**
 * This provides a Panel that is compatible with RequiresResize and errai-ui @Templated Views
 * This is the outer most container of the SessionPresenterView that needs to support RequiresResize
 * to propagate RequiresResize to child elements. In order for errai-ui to correctly substitute
 * the remainder of the HTML template the Panel needs to implement HasHTML.
 */
public class SessionContainer extends ResizeFlowPanel implements HasHTML {

    private final HTML container = new HTML();

    public SessionContainer() {
        add(container);
    }

    @Override
    public String getHTML() {
        return container.getHTML();
    }

    @Override
    public void setHTML(final @IsSafeHtml String html) {
        container.setHTML(html);
    }

    @Override
    public String getText() {
        return container.getText();
    }

    @Override
    public void setText(final String text) {
        container.setText(text);
    }

    @Override
    public void onResize() {
        super.onResize();
    }
}
