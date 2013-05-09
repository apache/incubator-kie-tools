/*
 * Copyright 2011 JBoss Inc
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
package org.kie.guvnor.guided.dtable.client.widget.table;

import com.google.gwt.resources.client.ImageResource;
import org.drools.guvnor.models.guided.dtable.shared.model.ActionCol52;
import org.drools.guvnor.models.guided.dtable.shared.model.BaseColumn;
import org.drools.guvnor.models.guided.dtable.shared.model.ConditionCol52;
import org.kie.workbench.widgets.decoratedgrid.client.widget.ResourcesProvider;
import org.kie.guvnor.guided.dtable.client.resources.Resources;

/**
 * A class to provide different resources for Decision Tables
 */
public class DecisionTableResourcesProvider
        implements
        ResourcesProvider<BaseColumn> {

    public int rowHeight() {
        return Resources.INSTANCE.css().rowHeight();
    }

    public int rowHeaderHeight() {
        return Resources.INSTANCE.css().rowHeaderHeight();
    }

    public int rowHeaderSplitterHeight() {
        return Resources.INSTANCE.css().rowHeaderSplitterHeight();
    }

    public int rowHeaderSorterHeight() {
        return Resources.INSTANCE.css().rowHeaderSorterHeight();
    }

    public int sidebarWidth() {
        return Resources.INSTANCE.css().sidebarWidth();
    }

    public int borderWidth() {
        return Resources.INSTANCE.css().borderWidth();
    }

    public int borderWidthThick() {
        return Resources.INSTANCE.css().borderWidthThick();
    }

    public String cellTableColumn( BaseColumn column ) {
        if ( column instanceof ConditionCol52 ) {
            return Resources.INSTANCE.css().conditionColumn();
        } else if ( column instanceof ActionCol52 ) {
            return Resources.INSTANCE.css().actionColumn();
        }
        return Resources.INSTANCE.css().metaColumn();
    }

    public String cellTable() {
        return Resources.INSTANCE.css().cellTable();
    }

    public String cellTableEvenRow() {
        return Resources.INSTANCE.css().cellTableEvenRow();
    }

    public String cellTableOddRow() {
        return Resources.INSTANCE.css().cellTableOddRow();
    }

    public String cellTableCell() {
        return Resources.INSTANCE.css().cellTableCell();
    }

    public String cellTableCellSelected() {
        return Resources.INSTANCE.css().cellTableCellSelected();
    }

    public String cellTableCellMultipleValues() {
        return Resources.INSTANCE.css().cellTableCellMultipleValues();
    }

    public String cellTableCellOtherwise() {
        return Resources.INSTANCE.css().cellTableCellOtherwise();
    }

    public String cellTableCellDiv() {
        return Resources.INSTANCE.css().cellTableCellDiv();
    }

    public String cellTableGroupDiv() {
        return Resources.INSTANCE.css().cellTableGroupDiv();
    }

    public String cellTableTextDiv() {
        return Resources.INSTANCE.css().cellTableTextDiv();
    }

    public String headerRowBottom() {
        return Resources.INSTANCE.css().headerRowBottom();
    }

    public String headerRowIntermediate() {
        return Resources.INSTANCE.css().headerRowIntermediate();
    }

    public String headerText() {
        return Resources.INSTANCE.css().headerText();
    }

    public String headerSplitter() {
        return Resources.INSTANCE.css().headerSplitter();
    }

    public String headerResizer() {
        return Resources.INSTANCE.css().headerResizer();
    }

    public String selectorSpacer() {
        return Resources.INSTANCE.css().selectorSpacer();
    }

    public String selectorSpacerOuterDiv() {
        return Resources.INSTANCE.css().selectorSpacerOuterDiv();
    }

    public String selectorSpacerInnerDiv() {
        return Resources.INSTANCE.css().selectorSpacerInnerDiv();
    }

    public String selectorCell() {
        return Resources.INSTANCE.css().selectorCell();
    }

    public ImageResource arrowSpacerIcon() {
        return Resources.INSTANCE.images().arrowSpacerIcon();
    }

    public ImageResource downArrowIcon() {
        return Resources.INSTANCE.tableImageResources().downArrow();
    }

    public ImageResource smallDownArrowIcon() {
        return Resources.INSTANCE.tableImageResources().smallDownArrow();
    }

    public ImageResource upArrowIcon() {
        return Resources.INSTANCE.tableImageResources().upArrow();
    }

    public ImageResource smallUpArrowIcon() {
        return Resources.INSTANCE.tableImageResources().smallUpArrow();
    }

    public ImageResource toggleUnmergeIcon() {
        return Resources.INSTANCE.images().toggleUnmergeIcon();
    }

    public ImageResource toggleMergeIcon() {
        return Resources.INSTANCE.images().toggleMergeIcon();
    }

    public ImageResource selectorAddIcon() {
        return Resources.INSTANCE.itemImages().newItem();
    }

    public ImageResource selectorDeleteIcon() {
        return Resources.INSTANCE.itemImages().deleteItemSmall();
    }

    public ImageResource collapseCellsIcon() {
        return Resources.INSTANCE.collapseExpand().collapse();
    }

    public ImageResource expandCellsIcon() {
        return Resources.INSTANCE.collapseExpand().expand();
    }

}
