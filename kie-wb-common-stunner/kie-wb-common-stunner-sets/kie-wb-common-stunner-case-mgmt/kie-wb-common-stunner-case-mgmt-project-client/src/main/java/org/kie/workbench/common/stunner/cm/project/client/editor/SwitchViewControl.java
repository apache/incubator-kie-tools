/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.cm.project.client.editor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Tooltip;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.IconPosition;
import org.gwtbootstrap3.client.ui.constants.IconSize;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.Placement;

public class SwitchViewControl extends Composite {

    @UiField
    HorizontalPanel switchViewPanel;

    @UiField
    Button caseViewButton;

    @UiField
    Tooltip caseViewTooltip;

    @UiField
    Button processViewButton;

    @UiField
    Tooltip processViewTooltip;

    private SwitchViewControlBinder uiBinder = GWT.create(SwitchViewControlBinder.class);

    SwitchViewControl(final String caseViewIconType,
                      final String caseViewTooltipText,
                      final Runnable caseViewSwitchHandler,
                      final String processViewIconType,
                      final String processViewTooltipText,
                      final Runnable processViewSwitchHandler) {
        initWidget(uiBinder.createAndBindUi(this));

        configureButton(caseViewButton,
                        caseViewIconType,
                        true,
                        caseViewTooltip,
                        caseViewTooltipText,
                        processViewButton,
                        caseViewSwitchHandler);

        configureButton(processViewButton,
                        processViewIconType,
                        false,
                        processViewTooltip,
                        processViewTooltipText,
                        caseViewButton,
                        processViewSwitchHandler);
    }

    void configureButton(final Button button,
                         final String iconType,
                         final boolean selected,
                         final Tooltip tooltip,
                         final String tooltipText,
                         final Button otherButton,
                         final Runnable handler) {
        button.setSize(ButtonSize.DEFAULT);
        button.setType(selected ? ButtonType.INFO : ButtonType.LINK);

        button.setIcon(IconType.valueOf(iconType));
        button.setIconFixedWidth(true);
        button.setIconSize(IconSize.LARGE);
        button.setIconPosition(IconPosition.LEFT);

        tooltip.setTitle(tooltipText);
        tooltip.setContainer("body");
        tooltip.setPlacement(Placement.AUTO);

        button.addClickHandler(event -> {
            select(button);
            deselect(otherButton);
            handler.run();
        });
    }

    private void select(final Button button) {
        button.setType(ButtonType.INFO);
    }

    private void deselect(final Button button) {
        button.setType(ButtonType.LINK);
    }

    interface SwitchViewControlBinder extends UiBinder<Widget, SwitchViewControl> {

    }
}
