/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.common.client.widgets;

import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.Dependent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Alert;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.AlertType;
import org.uberfire.mvp.Command;

@Dependent
public class AlertPanelView extends Composite implements AlertPanel.View {

    interface ViewBinder extends UiBinder<Widget, AlertPanelView> {}
    private static ViewBinder uiBinder = GWT.create(ViewBinder.class);

    @UiField
    Alert alert;

    @UiField
    Button okButton;

    @UiField
    Button cancelButton;

    AlertPanel presenter;

    Map<AlertType,String> pflyIconMap = new HashMap<AlertType, String>();

    @Override
    public void init(AlertPanel presenter) {
        this.presenter = presenter;
        initWidget(uiBinder.createAndBindUi(this));
        pflyIconMap.put(AlertType.SUCCESS, "pficon-ok");
        pflyIconMap.put(AlertType.INFO, "pficon-info");
        pflyIconMap.put(AlertType.WARNING, "pficon-warning-triangle-o");
        pflyIconMap.put(AlertType.DANGER, "pficon-error-circle-o");
        pflyIconMap.put(AlertType.DEFAULT, "pficon-info");
    }

    @Override
    public void show(AlertType severity, String message, Integer width, final Command onOk, final Command onCancel) {
        alert.setType(severity);
        alert.addStyleName("pficon");
        alert.addStyleName(pflyIconMap.get(severity));
        alert.setText("  " + message + "  ");
        if (width != null) {
            alert.setWidth(width + "px");
        }

        if (onOk != null) {
            okButton.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent clickEvent) {
                    onOk.execute();
                }
            });
        }
        if (onCancel != null) {
            cancelButton.setVisible(true);
            cancelButton.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent clickEvent) {
                    onCancel.execute();
                }
            });
        }
    }
}
