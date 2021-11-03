/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.client.views.pfly.menu.megamenu;

import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.Image;
import org.jboss.errai.common.client.dom.ListItem;
import org.jboss.errai.common.client.dom.Node;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.common.client.dom.UnorderedList;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.views.pfly.resources.i18n.Constants;
import org.uberfire.client.workbench.widgets.menu.megamenu.WorkbenchMegaMenuPresenter;
import org.uberfire.client.workbench.widgets.menu.megamenu.base.BaseMenuItemPresenter;
import org.uberfire.client.workbench.widgets.menu.megamenu.base.HasChildren;
import org.uberfire.client.workbench.widgets.menu.megamenu.contextmenuitem.ChildContextMenuItemPresenter;
import org.uberfire.client.workbench.widgets.menu.megamenu.contextmenuitem.GroupContextMenuItemPresenter;
import org.uberfire.client.workbench.widgets.menu.megamenu.menuitem.ChildMenuItemPresenter;
import org.uberfire.client.workbench.widgets.menu.megamenu.menuitem.GroupMenuItemPresenter;
import org.uberfire.mvp.Command;

@Templated
public class WorkbenchMegaMenuView implements WorkbenchMegaMenuPresenter.View,
                                              org.jboss.errai.ui.client.local.api.IsElement {

    private WorkbenchMegaMenuPresenter presenter;

    @DataField("brand")
    private Div brand;

    @DataField("brand-image")
    private Image brandImage;

    @DataField("home-link")
    private Anchor homeLink;

    @DataField("menu-accessor-text")
    private Span menuAccessorText;

    @DataField("single-menu-items-container")
    private ListItem singleMenuItemsContainer;

    @DataField("single-menu-items")
    private UnorderedList singleMenuItems;

    @DataField("left-menu-items")
    private UnorderedList leftMenuItems;

    @DataField("right-menu-items")
    private UnorderedList rightMenuItems;

    @DataField("context-menu-items-container")
    private ListItem contextMenuItemsContainer;

    @DataField("context-menu-items")
    private UnorderedList contextMenuItems;

    private TranslationService translationService;

    private ManagedInstance<ChildMenuItemPresenter> childMenuItemPresenters;

    private ManagedInstance<GroupMenuItemPresenter> groupMenuItemPresenters;

    private ManagedInstance<ChildContextMenuItemPresenter> childContextMenuItemPresenters;

    private ManagedInstance<GroupContextMenuItemPresenter> groupContextMenuItemPresenters;

    Map<String, BaseMenuItemPresenter> menuItemByIdentifier = new HashMap<>();

    @Inject
    public WorkbenchMegaMenuView(final Div brand,
                                 final Image brandImage,
                                 final Anchor homeLink,
                                 final Span menuAccessorText,
                                 final ListItem singleMenuItemsContainer,
                                 final UnorderedList singleMenuItems,
                                 final UnorderedList leftMenuItems,
                                 final UnorderedList rightMenuItems,
                                 final ListItem contextMenuItemsContainer,
                                 final UnorderedList contextMenuItems,
                                 final TranslationService translationService,
                                 final ManagedInstance<ChildMenuItemPresenter> childMenuItemPresenters,
                                 final ManagedInstance<GroupMenuItemPresenter> groupMenuItemPresenters,
                                 final ManagedInstance<ChildContextMenuItemPresenter> childContextMenuItemPresenters,
                                 final ManagedInstance<GroupContextMenuItemPresenter> groupContextMenuItemPresenters) {
        this.brand = brand;
        this.brandImage = brandImage;
        this.homeLink = homeLink;
        this.menuAccessorText = menuAccessorText;
        this.singleMenuItemsContainer = singleMenuItemsContainer;
        this.singleMenuItems = singleMenuItems;
        this.leftMenuItems = leftMenuItems;
        this.rightMenuItems = rightMenuItems;
        this.contextMenuItemsContainer = contextMenuItemsContainer;
        this.contextMenuItems = contextMenuItems;
        this.translationService = translationService;
        this.childMenuItemPresenters = childMenuItemPresenters;
        this.groupMenuItemPresenters = groupMenuItemPresenters;
        this.childContextMenuItemPresenters = childContextMenuItemPresenters;
        this.groupContextMenuItemPresenters = groupContextMenuItemPresenters;
    }

    @Override
    public void init(final WorkbenchMegaMenuPresenter presenter) {
        this.presenter = presenter;
        this.homeLink.setTitle(translationService.format(Constants.Home));
    }

    @Override
    public void clear() {
        singleMenuItems.setTextContent("");
        rightMenuItems.setTextContent("");

        for (int index = leftMenuItems.getChildNodes().getLength() - 1; index >= 0; index--) {
            final Node child = leftMenuItems.getChildNodes().item(index);
            if (child.getChildNodes().getLength() > 0 && !child.equals(singleMenuItemsContainer)) {
                leftMenuItems.removeChild(child);
            }
        }

        if (!singleMenuItemsContainer.getClassList().contains("empty")) {
            singleMenuItemsContainer.getClassList().add("empty");
        }

        menuItemByIdentifier = new HashMap<>();
    }

    @Override
    public void clearContextMenu() {
        setContextMenuActive(false);
        contextMenuItems.setTextContent("");
    }

    @Override
    public void setHomeLinkAction(final Command command) {
        homeLink.setOnclick(event -> command.execute());
    }

    @Override
    public void setBrandImageAction(final Command command) {
        brandImage.setOnclick(event -> command.execute());
    }

    @Override
    public String getDefaultMenuText() {
        return translationService.format(Constants.Menu);
    }

    @Override
    public void setBrandImage(final String brandImageUrl) {
        brandImage.setSrc(brandImageUrl);
    }

    @Override
    public void setBrandImageTitle(final String brandImageLabel) {
        brandImage.setTitle(brandImageLabel);
    }

    @Override
    public void hideBrand() {
        brand.setHidden(true);
    }

    @Override
    public void setMenuAccessorText(final String menuAccessorText) {
        this.menuAccessorText.setTextContent(menuAccessorText);
    }

    @Override
    public void addMenuItemOnRight(ChildMenuItemPresenter itemPresenter) {
        rightMenuItems.appendChild(itemPresenter.getView().getElement());
    }

    @Override
    public void addMenuItemOnLeft(ChildMenuItemPresenter itemPresenter) {
        singleMenuItems.appendChild(itemPresenter.getView().getElement());

        if (singleMenuItemsContainer.getClassList().contains("empty")) {
            singleMenuItemsContainer.getClassList().remove("empty");
        }
    }

    @Override
    public void addMenuItemOnParent(ChildMenuItemPresenter itemPresenter,
                                    HasChildren parentPresenter) {
        parentPresenter.addChild(itemPresenter.getView());
    }

    @Override
    public void addCustomMenuItem(IsElement menu) {
        rightMenuItems.appendChild(menu.getElement());
    }

    @Override
    public void addCustomMenuItem(IsWidget menu) {
        appendWidgetToElement(rightMenuItems,
                              menu.asWidget());
    }

    @Override
    public void addGroupMenuItem(GroupMenuItemPresenter itemPresenter) {
        leftMenuItems.appendChild(itemPresenter.getView().getElement());
    }

    @Override
    public void addContextMenuItem(GroupContextMenuItemPresenter itemPresenter) {
        contextMenuItems.appendChild(itemPresenter.getView().getElement());
    }

    @Override
    public void addContextMenuItem(ChildContextMenuItemPresenter itemPresenter) {
        contextMenuItems.appendChild(itemPresenter.getView().getElement());
    }

    @Override
    public void addContextMenuItemOnParent(ChildContextMenuItemPresenter itemPresenter,
                                           HasChildren parentPresenter) {
        parentPresenter.addChild(itemPresenter.getView());
    }

    void appendWidgetToElement(final HTMLElement parent,
                               final Widget child) {
        DOMUtil.appendWidgetToElement(parent,
                                      child);
    }

    @Override
    public void setContextMenuActive(final boolean active) {
        if (active) {
            if (!contextMenuItemsContainer.getClassList().contains("active")) {
                contextMenuItemsContainer.getClassList().add("active");
            }
        } else {
            if (contextMenuItemsContainer.getClassList().contains("active")) {
                contextMenuItemsContainer.getClassList().remove("active");
            }
        }
    }
}
