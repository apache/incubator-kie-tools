/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.kie.workbench.common.screens.explorer.client.widgets.business;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.common.services.project.model.Module;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.LinkedGroup;
import org.gwtbootstrap3.client.ui.LinkedGroupItem;
import org.gwtbootstrap3.client.ui.Panel;
import org.gwtbootstrap3.client.ui.PanelBody;
import org.gwtbootstrap3.client.ui.PanelCollapse;
import org.gwtbootstrap3.client.ui.PanelGroup;
import org.gwtbootstrap3.client.ui.constants.Toggle;
import org.jboss.errai.security.shared.api.identity.User;
import org.kie.workbench.common.screens.explorer.client.resources.i18n.ProjectExplorerConstants;
import org.kie.workbench.common.screens.explorer.client.resources.images.ProjectExplorerImageResources;
import org.kie.workbench.common.screens.explorer.client.utils.Classifier;
import org.kie.workbench.common.screens.explorer.client.utils.Utils;
import org.kie.workbench.common.screens.explorer.client.widgets.BaseViewImpl;
import org.kie.workbench.common.screens.explorer.client.widgets.BaseViewPresenter;
import org.kie.workbench.common.screens.explorer.client.widgets.View;
import org.kie.workbench.common.screens.explorer.client.widgets.loading.BusyIndicator;
import org.kie.workbench.common.screens.explorer.client.widgets.navigator.Explorer;
import org.kie.workbench.common.screens.explorer.client.widgets.navigator.NavigatorOptions;
import org.kie.workbench.common.screens.explorer.client.widgets.tagSelector.TagSelector;
import org.kie.workbench.common.screens.explorer.model.FolderItem;
import org.kie.workbench.common.screens.explorer.model.FolderItemType;
import org.kie.workbench.common.screens.explorer.model.FolderListing;
import org.kie.workbench.common.screens.explorer.utils.Sorters;
import org.uberfire.client.workbench.type.AnyResourceType;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.ext.widgets.common.client.accordion.TriggerWidget;

/**
 * Business View implementation
 */
@ApplicationScoped
public class BusinessViewWidget extends BaseViewImpl implements View {

    interface BusinessViewImplBinder
            extends
            UiBinder<Widget, BusinessViewWidget> {

    }

    private static final String ID_CLEANUP_PATTERN = "[^a-zA-Z0-9]";
    private static BusinessViewImplBinder uiBinder = GWT.create(BusinessViewImplBinder.class);
    //TreeSet sorts members upon insertion
    private final Set<FolderItem> sortedFolderItems = new TreeSet<FolderItem>(Sorters.ITEM_SORTER);
    private final NavigatorOptions businessOptions = new NavigatorOptions() {{
        showFiles(false);
        showHiddenFiles(false);
        showDirectories(true);
        allowUpLink(false);
        showItemAge(false);
        showItemMessage(false);
        showItemLastUpdater(false);
    }};
    @UiField
    BusyIndicator busyIndicator;
    @UiField
    Explorer explorer;
    @UiField
    PanelGroup itemsContainer;
    @UiField(provided = true)
    @Inject
    TagSelector tagSelector;
    @Inject
    Classifier classifier;
    @Inject
    User user;
    private Map<String, PanelCollapse> collapses = new HashMap<>();
    private BaseViewPresenter presenter;

    @PostConstruct
    public void init() {
        //Cannot create and bind UI until after injection points have been initialized
        initWidget(uiBinder.createAndBindUi(this));
        itemsContainer.setId(DOM.createUniqueId());
    }

    @Override
    public void init(final BaseViewPresenter presenter) {
        this.presenter = presenter;
        explorer.init(businessOptions,
                      Explorer.NavType.TREE,
                      presenter);
    }

    @Override
    public void setContent(final Module module,
                           final FolderListing folderListing,
                           final Map<FolderItem, List<FolderItem>> siblings) {
        explorer.setupHeader(module);
        explorer.loadContent(folderListing,
                             siblings);

        setItems(folderListing);
    }

    @Override
    public void setItems(final FolderListing folderListing) {
        renderItems(folderListing);
    }

    @Override
    public void renderItems(FolderListing folderListing) {
        tagSelector.loadContent(presenter.getActiveContentTags(),
                                presenter.getCurrentTag());
        itemsContainer.clear();
        sortedFolderItems.clear();
        for (final FolderItem content : folderListing.getContent()) {
            if (!content.getType().equals(FolderItemType.FOLDER)) {
                sortedFolderItems.add(content);
            }
        }

        if (!sortedFolderItems.isEmpty()) {
            final Map<ClientResourceType, Collection<FolderItem>> resourceTypeGroups = classifier.group(sortedFolderItems);
            final TreeMap<ClientResourceType, Collection<FolderItem>> sortedResourceTypeGroups = new TreeMap<>(Sorters.RESOURCE_TYPE_GROUP_SORTER);
            sortedResourceTypeGroups.putAll(resourceTypeGroups);

            for (final Map.Entry<ClientResourceType, Collection<FolderItem>> entry : sortedResourceTypeGroups.entrySet()) {
                final LinkedGroup itemsNavList = new LinkedGroup();
                itemsNavList.getElement().getStyle().setMarginBottom(0,
                                                                     Style.Unit.PX);
                final PanelCollapse collapse = new PanelCollapse();
                final String collapseId = getCollapseId(entry.getKey());
                final PanelCollapse oldCollapse = collapses.get(collapseId);
                final boolean in = (oldCollapse != null) ? oldCollapse.isIn() : false;
                collapse.setId(collapseId);
                collapse.setIn(in);
                final PanelBody body = new PanelBody();
                body.getElement().getStyle().setPadding(0,
                                                        Style.Unit.PX);
                collapse.add(body);
                body.add(itemsNavList);

                for (FolderItem folderItem : entry.getValue()) {
                    itemsNavList.add(makeItemNavLink(entry.getKey(),
                                                     folderItem));
                }

                itemsContainer.add(new Panel() {{
                    add(makeTriggerWidget(entry.getKey(),
                                          collapse));
                    add(collapse);
                }});

                collapses.put(collapseId,
                              collapse);
            }
        } else {
            itemsContainer.add(new Label(ProjectExplorerConstants.INSTANCE.noItemsExist()));
        }
    }

    private TriggerWidget makeTriggerWidget(final ClientResourceType resourceType,
                                            final PanelCollapse collapse) {
        final String description = getResourceTypeDescription(resourceType);
        if (resourceType.getIcon() != null) {
            return new TriggerWidget(resourceType.getIcon(),
                                     description,
                                     !collapse.isIn()) {{
                setDataToggle(Toggle.COLLAPSE);
                setDataParent(itemsContainer.getId());
                setDataTargetWidget(collapse);
            }};
        }
        return new TriggerWidget(description,
                                 !collapse.isIn()) {{
            setDataToggle(Toggle.COLLAPSE);
            setDataParent(itemsContainer.getId());
            setDataTargetWidget(collapse);
        }};
    }

    @Override
    public void showHiddenFiles(boolean show) {
        // No hidden files here.
    }

    @Override
    public void setNavType(Explorer.NavType navType) {
        explorer.setNavType(navType,
                            businessOptions);
    }

    @Override
    public void hideTagFilter() {
        tagSelector.hide();
        if (presenter.getActiveContent() != null) {
            renderItems(presenter.getActiveContent());
        }
    }

    @Override
    public void showTagFilter() {
        tagSelector.show();
    }

    @Override
    public void hideHeaderNavigator() {
        explorer.hideHeaderNavigator();
    }

    @Override
    public void showHeaderNavigator() {
        explorer.showHeaderNavigator();
    }

    @Override
    public Explorer getExplorer() {
        return explorer;
    }

    private String getResourceTypeDescription(final ClientResourceType resourceType) {
        String description = resourceType.getDescription();
        description = (description == null || description.isEmpty()) ? ProjectExplorerConstants.INSTANCE.miscellaneous_files() : description;
        return description;
    }

    private IsWidget makeItemNavLink(final ClientResourceType resourceType,
                                     final FolderItem folderItem) {
        String _fileName = folderItem.getFileName();
        if (!(resourceType instanceof AnyResourceType)) {
            _fileName = Utils.getBaseFileName(_fileName,
                                              resourceType.getSuffix());
        }
        _fileName = _fileName.replaceAll(" ",
                                         "\u00a0");
        final String fileName = _fileName;

        final LinkedGroupItem navLink = new LinkedGroupItem() {{
            setText(fileName);
            addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    presenter.onItemSelected(folderItem);
                }
            });
        }};

        Image lockImage;
        if (folderItem.getLockedBy() == null) {
            lockImage = new Image(ProjectExplorerImageResources.INSTANCE.lockEmpty());
        } else if (folderItem.getLockedBy().equals(user.getIdentifier())) {
            lockImage = new Image(ProjectExplorerImageResources.INSTANCE.lockOwned());
            lockImage.setTitle(ProjectExplorerConstants.INSTANCE.lockOwnedHint());
        } else {
            lockImage = new Image(ProjectExplorerImageResources.INSTANCE.lock());
            lockImage.setTitle(ProjectExplorerConstants.INSTANCE.lockHint() + " " + folderItem.getLockedBy());
        }

        navLink.getWidget(0)
                .getElement()
                .setInnerHTML("<span>" + lockImage.toString() + " " + fileName + "</span>");

        return navLink;
    }

    private String getCollapseId(ClientResourceType resourceType) {
        return resourceType != null ? resourceType.getShortName().replaceAll(ID_CLEANUP_PATTERN,
                                                                             "") : "";
    }

    @Override
    public void showBusyIndicator(final String message) {
        showContent(false);
        busyIndicator.showBusyIndicator(message);

    }

    @Override
    public void hideBusyIndicator() {
        showContent(true);
        busyIndicator.hideBusyIndicator();
    }

    @Override
    public void showContent(final boolean isVisible) {
        if (isVisible && presenter.canShowTags()) {
            tagSelector.show();
        } else {
            tagSelector.hide();
        }

        explorer.setVisible(isVisible);
        itemsContainer.setVisible(isVisible);
    }
}
