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

package org.uberfire.client.docks.view.bars;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ButtonGroup;
import org.gwtbootstrap3.client.ui.Heading;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.HeadingSize;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.uberfire.client.docks.view.menu.MenuBuilder;
import org.uberfire.client.resources.WebAppResource;
import org.uberfire.client.util.CSSLocatorsUtils;
import org.uberfire.client.workbench.docks.UberfireDockPosition;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;

public class DocksExpandedBar
        extends Composite implements ProvidesResize,
                                     RequiresResize {

    private static WebAppResource CSS = GWT.create(WebAppResource.class);
    @UiField
    FlowPanel titlePanel;

    @UiField
    FlowPanel targetPanel;

    Button collapse;

    Heading title;
    private UberfireDockPosition position;
    private ViewBinder uiBinder = GWT.create(ViewBinder.class);

    public DocksExpandedBar(UberfireDockPosition position) {
        initWidget(uiBinder.createAndBindUi(this));
        this.position = position;
        setupCSSLocators(position);
    }

    private void setupCSSLocators(UberfireDockPosition position) {

        getElement().addClassName(CSSLocatorsUtils.buildLocator("qe-docks-bar-expanded",
                                                                position.getShortName()));
    }

    @Override
    public void onResize() {
        resizeTargetPanel();
    }

    public void setup(String titleString,
                      ParameterizedCommand<String> closeCommand) {
        clear();
        createTitle(titleString);
        createButtons(titleString,
                      closeCommand);
        setupComponents();
        setupCSS();
    }

    private void setupComponents() {
        if (position == UberfireDockPosition.SOUTH) {
            titlePanel.add(collapse);
            titlePanel.add(title);
        } else if (position == UberfireDockPosition.WEST) {
            titlePanel.add(title);
            titlePanel.add(collapse);
        } else if (position == UberfireDockPosition.EAST) {
            titlePanel.add(collapse);
            titlePanel.add(title);
        }
    }

    public void addContextMenuItem(Widget menuItem) {
        if (menuItem != null) {
            final ButtonGroup bg = new ButtonGroup();
            bg.addStyleName(CSS.CSS().dockExpandedContentButton());
            bg.add(menuItem);
            titlePanel.add(bg);
        }
    }

    private void createTitle(String titleString) {
        title = new Heading(HeadingSize.H3,
                            titleString);
    }

    private void createButtons(final String identifier,
                               final ParameterizedCommand<String> closeCommand) {

        collapse = GWT.create(Button.class);
        collapse.setSize(ButtonSize.SMALL);
        collapse.addClickHandler(even -> closeCommand.execute(identifier));
    }

    private void setupCSS() {
        if (position == UberfireDockPosition.SOUTH) {
            titlePanel.addStyleName(CSS.CSS().dockExpandedContentPanelSouth());
            title.addStyleName(CSS.CSS().dockExpandedLabelSouth());
            collapse.addStyleName(CSS.CSS().dockExpandedButtonSouth());
            collapse.setIcon(IconType.CHEVRON_DOWN);
        } else if (position == UberfireDockPosition.WEST) {
            title.addStyleName(CSS.CSS().dockExpandedLabelWest());
            collapse.addStyleName(CSS.CSS().dockExpandedButtonWest());
            collapse.setIcon(IconType.CHEVRON_LEFT);
        } else if (position == UberfireDockPosition.EAST) {
            title.addStyleName(CSS.CSS().dockExpandedLabelEast());
            collapse.addStyleName(CSS.CSS().dockExpandedButtonEast());
            collapse.setIcon(IconType.CHEVRON_RIGHT);
        }
        setupDockContentSize();
    }

    public void setupDockContentSize() {
        //  goTo( PlaceRequest place, HasWidgets addTo ) lost widget size
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                resizeTargetPanel();
            }
        });
    }

    protected void resizeTargetPanel() {
        int width = calculateDockWidth();
        int height = calculateDockHeight();
        setPanelSize(width,
                     height);
    }

    int calculateDockWidth() {
        return getOffsetWidth();
    }

    int calculateDockHeight() {
        return getOffsetHeight() - titlePanel.getOffsetHeight();
    }

    public void setPanelSize(int width,
                             int height) {
        if (isValidHeightWidth(width,
                               height)) {
            targetPanel.setPixelSize(width,
                                     height);
        }
    }

    private boolean isValidHeightWidth(int height,
                                       int width) {
        if (height > 0 && width > 0) {
            return true;
        }
        return false;
    }

    public FlowPanel targetPanel() {
        return targetPanel;
    }

    public void clear() {
        targetPanel.clear();
        titlePanel.clear();
    }

    public UberfireDockPosition getPosition() {
        return position;
    }

    interface ViewBinder
            extends
            UiBinder<Widget, DocksExpandedBar> {

    }
}
