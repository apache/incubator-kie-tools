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
package org.drools.workbench.screens.guided.template.client.editor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import org.drools.workbench.screens.guided.template.client.resources.GuidedTemplateEditorResources;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.ResourcesProvider;

/**
 *
 */
public class TemplateDataTableResourcesProvider
        implements
        ResourcesProvider<TemplateDataColumn> {

    protected GuidedTemplateEditorResources INSTANCE = GWT.create( GuidedTemplateEditorResources.class );

    public int rowHeight() {
        return INSTANCE.css().rowHeight();
    }

    public int rowHeaderHeight() {
        return INSTANCE.css().rowHeaderHeight();
    }

    public int rowHeaderSplitterHeight() {
        return INSTANCE.css().rowHeaderSplitterHeight();
    }

    public int rowHeaderSorterHeight() {
        return INSTANCE.css().rowHeaderSorterHeight();
    }

    public int sidebarWidth() {
        return INSTANCE.css().sidebarWidth();
    }

    public int borderWidth() {
        return INSTANCE.css().borderWidth();
    }

    public int borderWidthThick() {
        return INSTANCE.css().borderWidthThick();
    }

    public String cellTableColumn( TemplateDataColumn column ) {
        return INSTANCE.css().templateColumn();
    }

    public String cellTable() {
        return INSTANCE.css().cellTable();
    }

    public String cellTableEvenRow() {
        return INSTANCE.css().cellTableEvenRow();
    }

    public String cellTableOddRow() {
        return INSTANCE.css().cellTableOddRow();
    }

    public String cellTableCell() {
        return INSTANCE.css().cellTableCell();
    }

    public String cellTableCellSelected() {
        return INSTANCE.css().cellTableCellSelected();
    }

    public String cellTableCellMultipleValues() {
        return INSTANCE.css().cellTableCellMultipleValues();
    }

    public String cellTableCellOtherwise() {
        return INSTANCE.css().cellTableCellOtherwise();
    }

    public String cellTableCellDiv() {
        return INSTANCE.css().cellTableCellDiv();
    }

    public String cellTableGroupDiv() {
        return INSTANCE.css().cellTableGroupDiv();
    }

    public String cellTableTextDiv() {
        return INSTANCE.css().cellTableTextDiv();
    }

    public String headerRowBottom() {
        return INSTANCE.css().headerRowBottom();
    }

    public String headerRowIntermediate() {
        return INSTANCE.css().headerRowIntermediate();
    }

    public String headerText() {
        return INSTANCE.css().headerText();
    }

    public String headerSplitter() {
        return INSTANCE.css().headerSplitter();
    }

    public String headerResizer() {
        return INSTANCE.css().headerResizer();
    }

    public String selectorSpacer() {
        return INSTANCE.css().selectorSpacer();
    }

    public String selectorSpacerOuterDiv() {
        return INSTANCE.css().selectorSpacerOuterDiv();
    }

    public String selectorSpacerInnerDiv() {
        return INSTANCE.css().selectorSpacerInnerDiv();
    }

    public String selectorCell() {
        return INSTANCE.css().selectorCell();
    }

    public ImageResource arrowSpacerIcon() {
        return INSTANCE.images().arrowSpacerIcon();
    }

    public ImageResource downArrowIcon() {
        return INSTANCE.tableImageResources().downArrow();
    }

    public ImageResource smallDownArrowIcon() {
        return INSTANCE.tableImageResources().smallDownArrow();
    }

    public ImageResource upArrowIcon() {
        return INSTANCE.tableImageResources().upArrow();
    }

    public ImageResource smallUpArrowIcon() {
        return INSTANCE.tableImageResources().smallUpArrow();
    }

    public ImageResource toggleUnmergeIcon() {
        return INSTANCE.images().toggleUnmergeIcon();
    }

    public ImageResource toggleMergeIcon() {
        return INSTANCE.images().toggleMergeIcon();
    }

    public ImageResource selectorAddIcon() {
        return INSTANCE.itemImages().newItem();
    }

    public ImageResource selectorDeleteIcon() {
        return INSTANCE.itemImages().deleteItemSmall();
    }

    public ImageResource collapseCellsIcon() {
        return INSTANCE.collapseExpand().collapse();
    }

    public ImageResource expandCellsIcon() {
        return INSTANCE.collapseExpand().expand();
    }

}
