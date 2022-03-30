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

package org.uberfire.ext.widgets.common.client.common;

import org.gwtproject.core.client.GWT;
import org.gwtproject.resources.client.ImageResource;
import org.gwtproject.uibinder.client.UiBinder;
import org.gwtproject.uibinder.client.UiField;
import org.gwtproject.user.client.ui.Composite;
import org.gwtproject.user.client.ui.Image;
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.Widget;

public class HeaderHTML extends Composite {

    private static HeaderHTMLBinder uiBinder = GWT.create(HeaderHTMLBinder.class);
    @UiField
    Label textLabel;
    @UiField
    Image image;

    public HeaderHTML() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    public void setText(String text) {
        textLabel.setText(text);
    }

    public void setImageResource(ImageResource imageResource) {
        image.setResource(imageResource);
    }

    interface HeaderHTMLBinder
            extends
            UiBinder<Widget, HeaderHTML> {

    }
}
