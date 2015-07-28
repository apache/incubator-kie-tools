/*
 * Copyright 2012 JBoss Inc
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

package org.kie.workbench.common.widgets.decoratedgrid.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import org.kie.workbench.common.widgets.client.resources.CollapseExpand;
import org.kie.workbench.common.widgets.client.resources.ItemImages;

/**
 * Resources for the Decision Table.
 */
public interface GridResources
        extends
        ClientBundle {

    GridResources INSTANCE = GWT.create( GridResources.class );

    interface GridStyle
            extends
            CssResource {

        int rowHeight();

        int rowHeaderHeight();

        int rowHeaderSplitterHeight();

        int rowHeaderSorterHeight();

        int sidebarWidth();

        int borderWidth();

        int borderWidthThick();

        String cellTable();

        String cellTableEvenRow();

        String cellTableOddRow();

        String cellTableCell();

        String cellTableCellSelected();

        String cellTableCellMultipleValues();

        String cellTableCellOtherwise();

        String cellTableCellDiv();

        String cellTableGroupDiv();

        String cellTableTextDiv();

        String headerRowBottom();

        String headerRowIntermediate();

        String headerText();

        String headerSplitter();

        String headerResizer();

        String selectorSpacer();

        String selectorSpacerOuterDiv();

        String selectorSpacerInnerDiv();

        String selectorCell();

        String contextMenu();

        String contextMenuItem();

        String contextMenuItemEnabled();

        String contextMenuItemDisabled();

    }

    @Source("images/emptyArrow.png")
    ImageResource arrowSpacerIcon();

    TableImageResources tableImageResources();

    @Source("images/icon-unmerge.png")
    ImageResource toggleUnmergeIcon();

    @Source("images/icon-merge.png")
    ImageResource toggleMergeIcon();

    ItemImages itemImages();

    CollapseExpand collapseExpand();

    @Source({ "css/grid.css" })
    GridStyle style();

}