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
package org.uberfire.ext.widgets.common.client.resources;

import com.google.gwt.resources.client.CssResource;

public interface CommonCss
        extends
        CssResource {

    @ClassName("whiteTopLeftCornerClass")
    String whiteTopLeftCornerClass();

    @ClassName("whiteTopRightCornerClass")
    String whiteTopRightCornerClass();

    @ClassName("whiteBottomLeftCornerClass")
    String whiteBottomLeftCornerClass();

    @ClassName("whiteBottomRightCornerClass")
    String whiteBottomRightCornerClass();

    @ClassName("whiteBottomClass")
    String whiteBottomClass();

    @ClassName("whiteTopClass")
    String whiteTopClass();

    @ClassName("whiteSideLeftClass")
    String whiteSideLeftClass();

    @ClassName("whiteSideRightClass")
    String whiteSideRightClass();

    @ClassName("whiteCenterClass")
    String whiteCenterClass();

    @ClassName("greyTopLeftCornerClass")
    String greyTopLeftCornerClass();

    @ClassName("greyTopRightCornerClass")
    String greyTopRightCornerClass();

    @ClassName("greyBottomLeftCornerClass")
    String greyBottomLeftCornerClass();

    @ClassName("greyBottomRightCornerClass")
    String greyBottomRightCornerClass();

    @ClassName("greyBottomClass")
    String greyBottomClass();

    @ClassName("greyTopClass")
    String greyTopClass();

    @ClassName("greySideLeftClass")
    String greySideLeftClass();

    @ClassName("greySideRightClass")
    String greySideRightClass();

    @ClassName("greyCenterClass")
    String greyCenterClass();

    @ClassName("clean-textarea")
    String cleanTextArea();

    @ClassName("busy-popup")
    String busyPopup();

    @ClassName("titleTextCellContainer")
    String titleTextCellContainer();

    @ClassName("titleTextCellDescription")
    String titleTextCellDescription();

    @ClassName("dataGrid")
    String dataGrid();

    @ClassName("dataGridHeader")
    String dataGridHeader();

    @ClassName("dataGridContent")
    String dataGridContent();

    @ClassName("dataGridRow")
    String dataGridRow();

    @ClassName("columnPickerPopup")
    String columnPickerPopup();

    @ClassName("columnPickerButton")
    String columnPickerButton();
}
