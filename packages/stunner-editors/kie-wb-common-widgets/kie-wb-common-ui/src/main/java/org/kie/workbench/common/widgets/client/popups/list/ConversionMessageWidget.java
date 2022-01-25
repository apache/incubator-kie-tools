/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.widgets.client.popups.list;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.widgets.client.resources.CommonImages;
import org.kie.workbench.common.widgets.client.widget.WidthCalculator;

/**
 * A widget to display a single conversion result message
 */
public class ConversionMessageWidget extends Composite {

    private static WidthCalculator<String> widthCalculator = new WidthCalculator<>(new TextCell());

    @UiField
    Image image;

    @UiField
    Label label;

    interface ConversionMessageWidgetBinder
            extends
            UiBinder<Widget, ConversionMessageWidget> {

    }

    private static ConversionMessageWidgetBinder uiBinder = GWT.create(ConversionMessageWidgetBinder.class);

    public ConversionMessageWidget(final MessageType messageType, final String message) {
        initWidget(uiBinder.createAndBindUi(this));

        switch (messageType) {
            case ERROR:
                this.image.setResource(CommonImages.INSTANCE.error());
                break;
            case INFO:
                this.image.setResource(CommonImages.INSTANCE.information());
                break;
            case WARNING:
                this.image.setResource(CommonImages.INSTANCE.warning());
                break;
            default:
                throw new IllegalStateException("Unknown message type: " + messageType);
        }
        this.label.setText(message);

        //Make containing Panel the width of the content to ensure scroll bars operate correctly
        int width = widthCalculator.getElementWidth(message) + 32;
        setWidth(width + "px");
    }
}
