/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dashbuilder.displayer.client.widgets;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.web.bindery.event.shared.HandlerRegistration;
import org.dashbuilder.displayer.DisplayerSubType;
import org.dashbuilder.displayer.DisplayerType;
import org.dashbuilder.displayer.client.resources.i18n.DisplayerTypeConstants;
import org.dashbuilder.displayer.client.resources.images.DisplayerImagesResources;
import org.gwtbootstrap3.client.ui.Image;
import org.gwtbootstrap3.client.ui.constants.ImageType;

public class DisplayerSubtypeSelectorView extends Composite implements DisplayerSubtypeSelector.View {

    DisplayerSubtypeSelector presenter;
    Map<DisplayerSubType,DisplayerSubTypeImageWidget> imageWidgets = new HashMap<DisplayerSubType, DisplayerSubTypeImageWidget>(5);
    FlexTable subtypes = new FlexTable();
    VerticalPanel subtypePanel = new VerticalPanel();
    DisplayerSubTypeImageWidget selectedWidget = null;

    @Override
    public void init(DisplayerSubtypeSelector presenter) {
        this.presenter = presenter;
        subtypePanel.add(subtypes);
        initWidget(subtypePanel);
    }

    @Override
    public void clear() {
        subtypes.removeAllRows();
        imageWidgets.clear();
    }

    @Override
    public void show(DisplayerType type, final DisplayerSubType subtype) {
        String resourcePrefix = type.toString() + "_" + subtype.toString();
        ImageResource selectedIR = (ImageResource) DisplayerImagesResources.INSTANCE.getResource(resourcePrefix + DisplayerImagesResources.SELECTED_SUFFIX);
        ImageResource unselectedIR = (ImageResource) DisplayerImagesResources.INSTANCE.getResource(resourcePrefix + DisplayerImagesResources.UNSELECTED_SUFFIX);
        String tooltip = DisplayerTypeConstants.INSTANCE.getString(resourcePrefix + "_tt");

        final DisplayerSubTypeImageWidget dstiw = new DisplayerSubTypeImageWidget(selectedIR, unselectedIR, tooltip, false);
        imageWidgets.put(subtype, dstiw);
        subtypes.setWidget(subtypes.getRowCount(), 0, dstiw);

        dstiw.setSelectClickHandler(e -> {
            if (!dstiw.isSelected) {
                select(subtype);
                presenter.onSelect(subtype);
            }
        });
    }

    @Override
    public void select(DisplayerSubType subtype) {
        if (selectedWidget != null) {
            selectedWidget.unselect();
        }
        selectedWidget = imageWidgets.get(subtype);
        selectedWidget.select();
    }

    @Override
    public void showDefault(DisplayerType type) {
        // Show a default image for those chart types that don't have any subtypes
        ImageResource selectedIR = (ImageResource)DisplayerImagesResources.INSTANCE.getResource(type.toString() + DisplayerImagesResources.DEFAULT_SUFFIX );
        String tooltip = DisplayerTypeConstants.INSTANCE.getString(type.toString() + DisplayerImagesResources.DEFAULT_SUFFIX + "_tt");
        DisplayerSubTypeImageWidget dstiw = new DisplayerSubTypeImageWidget(selectedIR, null, tooltip, true);
        subtypes.clear();
        subtypes.setWidget(0, 0, dstiw);
    }


    public class DisplayerSubTypeImageWidget extends Composite {

        private FlexTable container = new FlexTable();
        private boolean isSelected = false;
        private Image selected;
        private Image unselected;

        public DisplayerSubTypeImageWidget(ImageResource selectedImage,
                                           ImageResource unselectedImage,
                                           String tooltip,
                                           boolean initiallySelected) {

            initWidget(container);

            isSelected = initiallySelected;

            if (selectedImage != null) {
                selected = new Image(selectedImage);
                selected.setType(ImageType.THUMBNAIL);
                selected.setTitle(tooltip);
                selected.setVisible(isSelected);
                selected.addStyleName("selDispSubtype"); //for selenium
                container.setWidget(0, 0, selected);
            }

            if (unselectedImage != null) {
                unselected = new Image(unselectedImage);
                unselected.setType(ImageType.THUMBNAIL);
                unselected.setTitle(tooltip);
                unselected.setVisible(!isSelected);
                unselected.getElement().getStyle().setCursor(Style.Cursor.POINTER);
                container.setWidget(0, 1, unselected);
            }
        }

        public HandlerRegistration setSelectClickHandler(ClickHandler selectedClickHandler) {
            return unselected != null ? unselected.addClickHandler(selectedClickHandler) : null;
        }

        public void select() {
            isSelected = true;
            selected.setVisible(true);
            unselected.setVisible(false);
        }

        public void unselect() {
            isSelected = false;
            selected.setVisible(false);
            unselected.setVisible(true);
        }
    }
}
