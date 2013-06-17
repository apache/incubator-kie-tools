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
package org.drools.workbench.screens.guided.dtable.client.widget.table;

import com.google.gwt.resources.client.ImageResource;
import org.drools.workbench.models.guided.dtable.shared.model.ActionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.screens.guided.dtable.client.resources.GuidedDecisionTableResources;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.ResourcesProvider;
import org.drools.workbench.screens.guided.dtable.client.resources.GuidedDecisionTableResources;

/**
 * A class to provide different resources for Decision Tables
 */
public class DecisionTableResourcesProvider
        implements
        ResourcesProvider<BaseColumn> {

    public int rowHeight() {
        return GuidedDecisionTableResources.INSTANCE.css().rowHeight();
    }

    public int rowHeaderHeight() {
        return GuidedDecisionTableResources.INSTANCE.css().rowHeaderHeight();
    }

    public int rowHeaderSplitterHeight() {
        return GuidedDecisionTableResources.INSTANCE.css().rowHeaderSplitterHeight();
    }

    public int rowHeaderSorterHeight() {
        return GuidedDecisionTableResources.INSTANCE.css().rowHeaderSorterHeight();
    }

    public int sidebarWidth() {
        return GuidedDecisionTableResources.INSTANCE.css().sidebarWidth();
    }

    public int borderWidth() {
        return GuidedDecisionTableResources.INSTANCE.css().borderWidth();
    }

    public int borderWidthThick() {
        return GuidedDecisionTableResources.INSTANCE.css().borderWidthThick();
    }

    public String cellTableColumn( BaseColumn column ) {
        if ( column instanceof ConditionCol52 ) {
            return GuidedDecisionTableResources.INSTANCE.css().conditionColumn();
        } else if ( column instanceof ActionCol52 ) {
            return GuidedDecisionTableResources.INSTANCE.css().actionColumn();
        }
        return GuidedDecisionTableResources.INSTANCE.css().metaColumn();
    }

    public String cellTable() {
        return GuidedDecisionTableResources.INSTANCE.css().cellTable();
    }

    public String cellTableEvenRow() {
        return GuidedDecisionTableResources.INSTANCE.css().cellTableEvenRow();
    }

    public String cellTableOddRow() {
        return GuidedDecisionTableResources.INSTANCE.css().cellTableOddRow();
    }

    public String cellTableCell() {
        return GuidedDecisionTableResources.INSTANCE.css().cellTableCell();
    }

    public String cellTableCellSelected() {
        return GuidedDecisionTableResources.INSTANCE.css().cellTableCellSelected();
    }

    public String cellTableCellMultipleValues() {
        return GuidedDecisionTableResources.INSTANCE.css().cellTableCellMultipleValues();
    }

    public String cellTableCellOtherwise() {
        return GuidedDecisionTableResources.INSTANCE.css().cellTableCellOtherwise();
    }

    public String cellTableCellDiv() {
        return GuidedDecisionTableResources.INSTANCE.css().cellTableCellDiv();
    }

    public String cellTableGroupDiv() {
        return GuidedDecisionTableResources.INSTANCE.css().cellTableGroupDiv();
    }

    public String cellTableTextDiv() {
        return GuidedDecisionTableResources.INSTANCE.css().cellTableTextDiv();
    }

    public String headerRowBottom() {
        return GuidedDecisionTableResources.INSTANCE.css().headerRowBottom();
    }

    public String headerRowIntermediate() {
        return GuidedDecisionTableResources.INSTANCE.css().headerRowIntermediate();
    }

    public String headerText() {
        return GuidedDecisionTableResources.INSTANCE.css().headerText();
    }

    public String headerSplitter() {
        return GuidedDecisionTableResources.INSTANCE.css().headerSplitter();
    }

    public String headerResizer() {
        return GuidedDecisionTableResources.INSTANCE.css().headerResizer();
    }

    public String selectorSpacer() {
        return GuidedDecisionTableResources.INSTANCE.css().selectorSpacer();
    }

    public String selectorSpacerOuterDiv() {
        return GuidedDecisionTableResources.INSTANCE.css().selectorSpacerOuterDiv();
    }

    public String selectorSpacerInnerDiv() {
        return GuidedDecisionTableResources.INSTANCE.css().selectorSpacerInnerDiv();
    }

    public String selectorCell() {
        return GuidedDecisionTableResources.INSTANCE.css().selectorCell();
    }

    public ImageResource arrowSpacerIcon() {
        return GuidedDecisionTableResources.INSTANCE.images().arrowSpacerIcon();
    }

    public ImageResource downArrowIcon() {
        return GuidedDecisionTableResources.INSTANCE.tableImageResources().downArrow();
    }

    public ImageResource smallDownArrowIcon() {
        return GuidedDecisionTableResources.INSTANCE.tableImageResources().smallDownArrow();
    }

    public ImageResource upArrowIcon() {
        return GuidedDecisionTableResources.INSTANCE.tableImageResources().upArrow();
    }

    public ImageResource smallUpArrowIcon() {
        return GuidedDecisionTableResources.INSTANCE.tableImageResources().smallUpArrow();
    }

    public ImageResource toggleUnmergeIcon() {
        return GuidedDecisionTableResources.INSTANCE.images().toggleUnmergeIcon();
    }

    public ImageResource toggleMergeIcon() {
        return GuidedDecisionTableResources.INSTANCE.images().toggleMergeIcon();
    }

    public ImageResource selectorAddIcon() {
        return GuidedDecisionTableResources.INSTANCE.itemImages().newItem();
    }

    public ImageResource selectorDeleteIcon() {
        return GuidedDecisionTableResources.INSTANCE.itemImages().deleteItemSmall();
    }

    public ImageResource collapseCellsIcon() {
        return GuidedDecisionTableResources.INSTANCE.collapseExpand().collapse();
    }

    public ImageResource expandCellsIcon() {
        return GuidedDecisionTableResources.INSTANCE.collapseExpand().expand();
    }

}
