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

package org.uberfire.ext.widgets.common.client.menu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.IsWidget;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ButtonGroup;
import org.gwtbootstrap3.client.ui.Divider;
import org.gwtbootstrap3.client.ui.DropDownMenu;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.Pull;
import org.gwtbootstrap3.client.ui.constants.Toggle;
import org.uberfire.ext.widgets.common.client.resources.i18n.CommonConstants;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.impl.BaseMenuCustom;

public class RefreshSelectorMenuBuilder implements MenuFactory.CustomMenuBuilder {

    public interface SupportsRefreshInterval {

        void onUpdateRefreshInterval(boolean enableAutoRefresh, int newInterval);

    }

    private final CommonConstants constants = CommonConstants.INSTANCE;

    private SupportsRefreshInterval supportsRefreshInterval;

    private final DropDownMenu menuDropDownMenu = GWT.create(DropDownMenu.class);
    private final Button menuButton = GWT.create(Button.class);
    private final ButtonGroup buttonGroup = GWT.create(ButtonGroup.class);

    public RefreshSelectorMenuBuilder(final SupportsRefreshInterval supportsRefreshInterval) {
        this.supportsRefreshInterval = supportsRefreshInterval;
        setupMenuButton();
        setupMenuDropDown();
        setupMenuButtonGroup();
    }

    @Override
    public void push(MenuFactory.CustomMenuBuilder element) {
    }

    @Override
    public MenuItem build() {
        return new BaseMenuCustom<IsWidget>() {
            @Override
            public IsWidget build() {
                return buttonGroup;
            }

            @Override
            public boolean isEnabled() {
                return true;
            }

            @Override
            public void setEnabled(boolean enabled) {

            }

            @Override
            public String getSignatureId() {
                return "org.uberfire.ext.widgets.common.client.menu.RefreshSelectorMenuBuilder#refresh";
            }

        };
    }

    protected void setupMenuButtonGroup() {
        buttonGroup.add(menuButton);
        buttonGroup.add(menuDropDownMenu);
    }

    protected void setupMenuButton() {
        menuButton.setDataToggle(Toggle.DROPDOWN);
        menuButton.setIcon(IconType.COG);
        menuButton.setTitle(constants.AutoRefresh());
        menuButton.setSize(ButtonSize.SMALL);
    }

    protected void setupMenuDropDown() {
        menuDropDownMenu.setPull(Pull.RIGHT);
    }

    protected AnchorListItem createTimeSelector(int time,
                                                String name,
                                                int configuredSeconds,
                                                final AnchorListItem refreshDisableButton) {
        final AnchorListItem oneMinuteRadioButton = GWT.create(AnchorListItem.class);
        oneMinuteRadioButton.setText(name);
        oneMinuteRadioButton.setIconFixedWidth(true);
        final int selectedRefreshTime = time;
        if (configuredSeconds == selectedRefreshTime) {
            oneMinuteRadioButton.setIcon(IconType.CHECK);
        }

        oneMinuteRadioButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                oneMinuteRadioButton.setIcon(IconType.CHECK);
                supportsRefreshInterval.onUpdateRefreshInterval(true, selectedRefreshTime);
                refreshDisableButton.setActive(false);
                refreshDisableButton.setEnabled(true);
                refreshDisableButton.setText(constants.Disable_autorefresh());

            }
        });

        return oneMinuteRadioButton;
    }

    public void loadOptions(int configuredSeconds) {
        final AnchorListItem resetButton = new AnchorListItem(constants.Disable_autorefresh());

        if (configuredSeconds > 10) {
            supportsRefreshInterval.onUpdateRefreshInterval(true, configuredSeconds);
            resetButton.setEnabled(true);
        } else {
            supportsRefreshInterval.onUpdateRefreshInterval(false, 0);
            resetButton.setEnabled(false);
            resetButton.setText(constants.Autorefresh_Disabled());
        }

        final AnchorListItem oneMinuteRadioButton = createTimeSelector(60, "1 " + constants.Minute(), configuredSeconds, resetButton);
        final AnchorListItem fiveMinuteRadioButton = createTimeSelector(300, "5 " + constants.Minutes(), configuredSeconds, resetButton);
        final AnchorListItem tenMinuteRadioButton = createTimeSelector(600, "10 " + constants.Minutes(), configuredSeconds, resetButton);

        menuDropDownMenu.add(oneMinuteRadioButton);
        menuDropDownMenu.add(fiveMinuteRadioButton);
        menuDropDownMenu.add(tenMinuteRadioButton);

        oneMinuteRadioButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                fiveMinuteRadioButton.setIcon(null);
                tenMinuteRadioButton.setIcon(null);
            }
        });

        fiveMinuteRadioButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                oneMinuteRadioButton.setIcon(null);
                tenMinuteRadioButton.setIcon(null);
            }
        });

        tenMinuteRadioButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                oneMinuteRadioButton.setIcon(null);
                fiveMinuteRadioButton.setIcon(null);
            }
        });

        resetButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                supportsRefreshInterval.onUpdateRefreshInterval(false, 0);
                resetButton.setActive(false);
                resetButton.setEnabled(false);
                resetButton.setText(constants.Autorefresh_Disabled());
                oneMinuteRadioButton.setIcon(null);
                fiveMinuteRadioButton.setIcon(null);
                tenMinuteRadioButton.setIcon(null);
            }
        });

        menuDropDownMenu.add(new Divider());
        menuDropDownMenu.add(resetButton);
    }

}