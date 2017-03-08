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

package org.uberfire.ext.security.management.client.widgets.management.editor.acl.node;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.ext.security.management.client.resources.i18n.SecurityManagementConstants;

@Dependent
@Templated
public class MultiplePermissionNodeEditorView extends Composite
        implements MultiplePermissionNodeEditor.View {

    @Inject
    @DataField
    Div rootPanel;
    @Inject
    @DataField
    Div headerPanel;
    @Inject
    @DataField
    Anchor nodeAnchor;
    @Inject
    @DataField
    Div nodeAnchorPanel;
    @Inject
    @DataField
    Span nodeAnchorHelp;
    @Inject
    @DataField
    FlowPanel nodePermissions;
    @Inject
    @DataField
    Div collapsePanel;
    @Inject
    @DataField
    FlowPanel nodeChildren;
    @Inject
    @DataField
    Anchor addChildAnchor;
    @Inject
    @DataField
    Anchor clearChildrenAnchor;
    @Inject
    @DataField
    Div addChildPanel;
    @Inject
    @DataField
    Div clearChildrenPanel;
    @Inject
    @DataField
    Anchor cancelAnchor;
    @Inject
    @DataField
    FlowPanel childSelectorPanel;
    boolean expanded;
    boolean mouseOver;
    private MultiplePermissionNodeEditor presenter;

    @Override
    public void init(MultiplePermissionNodeEditor presenter) {
        this.presenter = presenter;

        String collapseId = Document.get().createUniqueId();
        collapsePanel.setId(collapseId);
        nodeAnchor.setHref("#" + collapseId);

        addChildAnchor.setTextContent(" " + SecurityManagementConstants.INSTANCE.addChildException());
        clearChildrenAnchor.setTextContent(" " + SecurityManagementConstants.INSTANCE.clearChildren());
    }

    @Override
    public void setNodeName(String name) {
        nodeAnchor.setTextContent(name);
    }

    @Override
    public void setNodePanelWidth(int width) {
        nodeAnchorPanel.getStyle().setProperty("width",
                                               width + "px");
    }

    @Override
    public void setNodeFullName(String name) {
        nodeAnchor.setTitle(name);
        nodeAnchorHelp.setTitle(name);
        nodeAnchorHelp.setClassName("acl-node-help-panel");
    }

    @Override
    public void setResourceName(String name) {

    }

    @Override
    public void addPermission(PermissionSwitchToogle permissionSwitch) {
        nodePermissions.add(permissionSwitch);
    }

    @Override
    public void addChildEditor(PermissionNodeEditor editor,
                               boolean dynamic) {
        if (dynamic) {
            editor.setLeftMargin(20);

            FlowPanel row = new FlowPanel();
            org.gwtbootstrap3.client.ui.Anchor anchor = new org.gwtbootstrap3.client.ui.Anchor();
            anchor.addStyleName("pf pficon-delete");
            anchor.getElement().getStyle().setWidth(20,
                                                    Style.Unit.PX);
            anchor.getElement().getStyle().setDisplay(Style.Display.TABLE_CELL);
            anchor.getElement().getStyle().setVerticalAlign(Style.VerticalAlign.MIDDLE);
            anchor.addClickHandler(event -> presenter.onRemoveChild(editor));
            editor.asWidget().getElement().getStyle().setDisplay(Style.Display.TABLE_CELL);
            editor.asWidget().getElement().getStyle().setProperty("paddingLeft",
                                                                  "0px");

            row.getElement().getStyle().setProperty("paddingLeft",
                                                    "20px");
            row.add(anchor);
            row.add(editor);
            nodeChildren.add(row);
        } else {
            nodeChildren.add(editor);
        }
    }

    @Override
    public void addChildSeparator() {
        FlowPanel separator = new FlowPanel();
        separator.getElement().getStyle().setHeight(1,
                                                    Style.Unit.PX);
        separator.getElement().getStyle().setBackgroundColor("lightgrey");
        nodeChildren.add(separator);
    }

    @Override
    public boolean hasChildren() {
        return nodeChildren.getWidgetCount() > 0;
    }

    @Override
    public void clearChildren() {
        nodeChildren.clear();
    }

    @Override
    public String getChildSelectorHint(String resourceName) {
        return SecurityManagementConstants.INSTANCE.selectResourceInstance(resourceName);
    }

    @Override
    public String getChildSearchHint(String resourceName) {
        return SecurityManagementConstants.INSTANCE.searchResourceInstance(resourceName);
    }

    @Override
    public String getChildrenNotFoundMsg(String resourceName) {
        return SecurityManagementConstants.INSTANCE.resourceInstanceNotFound(resourceName);
    }

    @Override
    public void setChildSelector(IsWidget childSelector) {
        childSelectorPanel.clear();
        childSelectorPanel.add(childSelector);
    }

    @Override
    public void showChildSelector() {
        addChildPanel.getStyle().removeProperty("display");
    }

    @Override
    public void hideChildSelector() {
        addChildPanel.getStyle().setProperty("display",
                                             "none");
    }

    @Override
    public void setAddChildEnabled(boolean enabled) {
        if (enabled) {
            addChildAnchor.getStyle().removeProperty("display");
        } else {
            addChildAnchor.getStyle().setProperty("display",
                                                  "none");
        }
    }

    @Override
    public void setClearChildrenEnabled(boolean enabled) {
        if (enabled) {
            clearChildrenAnchor.getStyle().removeProperty("display");
        } else {
            clearChildrenAnchor.getStyle().setProperty("display",
                                                       "none");
        }
    }

    @Override
    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
        this.mouseOver = false;
        String rootPanelCss = expanded ? "acl-root-panel-expanded" : "acl-root-panel";
        String headerPanelCss = expanded ? "acl-header-panel-expanded" : "acl-header-panel";
        rootPanel.setClassName(rootPanelCss);
        headerPanel.setClassName(headerPanelCss);
    }

    @EventHandler("nodeAnchor")
    private void onNodeClick(ClickEvent event) {
        presenter.onNodeClick();
    }

    @EventHandler("addChildAnchor")
    private void onAddChild(ClickEvent event) {
        presenter.onAddChildStart();
    }

    @EventHandler("clearChildrenAnchor")
    private void onClearChildren(ClickEvent event) {
        presenter.onClearChildren();
    }

    @EventHandler("cancelAnchor")
    private void onCancelAdd(ClickEvent event) {
        presenter.onAddChildCancel();
    }

    @EventHandler("headerPanel")
    private void onHeaderMouseOver(MouseOverEvent event) {
        if (!expanded) {
            headerPanel.setClassName("acl-header-panel-over");
            mouseOver = true;
        }
    }

    @EventHandler("headerPanel")
    private void onHeaderMouseOut(MouseOutEvent event) {
        if (mouseOver) {
            headerPanel.setClassName("acl-header-panel");
            mouseOver = false;
        }
    }
}
