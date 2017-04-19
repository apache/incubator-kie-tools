/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.screens.explorer.client;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ButtonGroup;
import org.gwtbootstrap3.client.ui.Divider;
import org.gwtbootstrap3.client.ui.DropDownMenu;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.Toggle;
import org.kie.workbench.common.screens.explorer.client.resources.i18n.ProjectExplorerConstants;
import org.kie.workbench.common.screens.explorer.client.utils.URLHelper;
import org.uberfire.backend.vfs.Path;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.workbench.model.menu.impl.BaseMenuCustom;

public class ExplorerMenuViewImpl
        implements ExplorerMenuView {

    @Inject
    private ProjectScreenMenuItem projectScreenMenuItem;

    private final AnchorListItem businessView = new AnchorListItem(ProjectExplorerConstants.INSTANCE.projectView());
    private final AnchorListItem techView = new AnchorListItem(ProjectExplorerConstants.INSTANCE.repositoryView());
    private final AnchorListItem treeExplorer = new AnchorListItem(ProjectExplorerConstants.INSTANCE.showAsFolders());
    private final AnchorListItem breadcrumbExplorer = new AnchorListItem(ProjectExplorerConstants.INSTANCE.showAsLinks());
    private final AnchorListItem showTagFilter = new AnchorListItem(ProjectExplorerConstants.INSTANCE.enableTagFiltering());
    private final AnchorListItem archiveRepository = new AnchorListItem(ProjectExplorerConstants.INSTANCE.downloadRepository());

    private final AnchorListItem archiveProject = new AnchorListItem(ProjectExplorerConstants.INSTANCE.downloadProject());

    private ExplorerMenu presenter;

    @Override
    public void setPresenter(ExplorerMenu presenter) {
        this.presenter = presenter;
    }

    @PostConstruct
    protected void postConstruct() {
        businessView.setIconFixedWidth(true);
        businessView.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                presenter.onBusinessViewSelected();
            }
        });

        techView.setIconFixedWidth(true);
        techView.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                presenter.onTechViewSelected();
            }
        });

        treeExplorer.setIconFixedWidth(true);
        treeExplorer.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                presenter.onTreeExplorerSelected();
            }
        });

        breadcrumbExplorer.setIconFixedWidth(true);
        breadcrumbExplorer.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                presenter.onBreadCrumbExplorerSelected();
            }
        });

        showTagFilter.setIconFixedWidth(true);
        showTagFilter.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                presenter.onShowTagFilterSelected();
            }
        });

        archiveProject.setIcon(IconType.DOWNLOAD);
        archiveProject.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.onArchiveActiveProject();
            }
        });

        archiveRepository.setIcon(IconType.DOWNLOAD);
        archiveRepository.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.onArchiveActiveRepository();
            }
        });
    }

    @Override
    public void archive(Path path) {
        Window.open(URLHelper.getDownloadUrl(path),
                    "downloading",
                    "resizable=no,scrollbars=yes,status=no");
    }

    @Override
    public void showTreeNav() {
        treeExplorer.setIcon(IconType.CHECK);
        breadcrumbExplorer.setIcon(null);
    }

    @Override
    public void showBreadcrumbNav() {
        breadcrumbExplorer.setIcon(IconType.CHECK);
        treeExplorer.setIcon(null);
    }

    @Override
    public void showTechViewIcon() {
        techView.setIcon(IconType.CHECK);
    }

    @Override
    public void hideBusinessViewIcon() {
        businessView.setIcon(null);
    }

    @Override
    public void showBusinessViewIcon() {
        businessView.setIcon(IconType.CHECK);
    }

    @Override
    public void hideTechViewIcon() {
        techView.setIcon(null);
    }

    @Override
    public void showTagFilterIcon() {
        showTagFilter.setIcon(IconType.CHECK);
    }

    @Override
    public void hideTagFilterIcon() {
        showTagFilter.setIcon(null);
    }

    @Override
    public Menus asMenu() {

        return MenuFactory
                .newTopLevelCustomMenu(new MenuFactory.CustomMenuBuilder() {
                    @Override
                    public void push(MenuFactory.CustomMenuBuilder element) {
                    }

                    @Override
                    public MenuItem build() {
                        return new BaseMenuCustom() {
                            @Override
                            public Widget build() {
                                return new ButtonGroup() {{
                                    add(new Button() {{
                                        setToggleCaret(false);
                                        setDataToggle(Toggle.DROPDOWN);
                                        setIcon(IconType.COG);
                                        setSize(ButtonSize.SMALL);
                                        setTitle(ProjectExplorerConstants.INSTANCE.customizeView());
                                    }});
                                    add(new DropDownMenu() {{
                                        addStyleName("pull-right");
                                        add(businessView);
                                        add(techView);
                                        add(new Divider());
                                        add(breadcrumbExplorer);
                                        add(treeExplorer);
                                        add(new Divider());
                                        add(showTagFilter);
                                        add(new Divider());
                                        add(archiveProject);
                                        add(archiveRepository);
                                    }});
                                }};
                            }
                        };
                    }
                })
                .endMenu()
                .newTopLevelCustomMenu(new MenuFactory.CustomMenuBuilder() {
                    @Override
                    public void push(MenuFactory.CustomMenuBuilder element) {
                    }

                    @Override
                    public MenuItem build() {
                        return new BaseMenuCustom() {
                            @Override
                            public Widget build() {
                                return new Button() {{
                                    setIcon(IconType.REFRESH);
                                    setSize(ButtonSize.SMALL);
                                    setTitle(ProjectExplorerConstants.INSTANCE.refresh());
                                    addClickHandler(new ClickHandler() {
                                        @Override
                                        public void onClick(ClickEvent event) {
                                            presenter.onRefresh();
                                        }
                                    });
                                }};
                            }
                        };
                    }
                })
                .endMenu()
                .build();
    }
}
