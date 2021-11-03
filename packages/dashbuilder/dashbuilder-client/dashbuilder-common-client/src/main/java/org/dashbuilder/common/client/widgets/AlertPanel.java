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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.constants.AlertType;
import org.uberfire.client.mvp.UberView;
import org.uberfire.mvp.Command;

@Dependent
public class AlertPanel implements IsWidget {

    public interface View extends UberView<AlertPanel> {

        void show(AlertType severity,
                  String message,
                  Integer width,
                  Command onOk,
                  Command onCancel);
    }

    View view = null;

    public AlertPanel() {
        this(new AlertPanelView());
    }

    @Inject
    public AlertPanel(View view) {
        this.view = view;
        view.init(this);
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public void show(AlertType severity, String message, Integer width, Command onOk, Command onCancel) {
        view.show(severity, message, width, onOk, onCancel);
    }
}
