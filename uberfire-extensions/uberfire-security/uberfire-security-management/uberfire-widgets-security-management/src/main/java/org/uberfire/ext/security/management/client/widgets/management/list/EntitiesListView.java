/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.security.management.client.widgets.management.list;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.HeadingSize;
import org.gwtbootstrap3.client.ui.constants.IconPosition;
import org.uberfire.ext.security.management.client.resources.i18n.UsersManagementWidgetsConstants;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;

/**
 * <p>View implementation for listing entities with pagination features.</p>
 *           
 * @since 0.8.0
 */
@Dependent
public class EntitiesListView extends Composite
        implements
        EntitiesList.View {

    interface EntitiesListViewBinder
            extends
            UiBinder<Row, EntitiesListView> {

    }

    private static EntitiesListViewBinder uiBinder = GWT.create(EntitiesListViewBinder.class);

    interface EntitiesListViewStyle extends CssResource {
        String entityPanel();
        String entityGroup();
        String entitiesList();
        String entityListTitle();
        String entityListButton();
        String left();
    }

    @UiField
    EntitiesListViewStyle style;

    @UiField
    Row emptyEntitiesRow;
    
    @UiField
    Label emptyEntitiesLabel;
    
    @UiField
    LinkedGroup entitiesList;

    @UiField
    Pagination pagination;

    @UiField
    AnchorListItem firstPageAnchor;
    
    @UiField
    AnchorListItem prevPageAnchor;

    @UiField
    AnchorListItem currentPageAnchor;

    @UiField
    AnchorListItem nextPageAnchor;

    @UiField
    AnchorListItem lastPageAnchor;
    
    @UiField
    Badge totalBadge;
    
    @UiField
    HTML totalText;
    
    private EntitiesList presenter;
    private HandlerRegistration firstPageAnchorClickHandlerRegistration = null;
    private HandlerRegistration prevPageAnchorClickHandlerRegistration = null;
    private HandlerRegistration nextPageAnchorClickHandlerRegistration = null;
    private HandlerRegistration lastPageAnchorClickHandlerRegistration = null;

    @PostConstruct
    protected void initUIBinder() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @Override
    public void init(final EntitiesList presenter) {
        this.presenter = presenter;
    }


    @Override
    public EntitiesList.View configure(final String emptyEntitiesText, 
                                       final EntitiesList.PaginationConstraints paginationConstraints) {
        clear();
        final String emptyText = emptyEntitiesText != null ? emptyEntitiesText : UsersManagementWidgetsConstants.INSTANCE.emptyEntities();
        emptyEntitiesLabel.setText(emptyText);
        applyPaginationConstraints(paginationConstraints);
        return this;
    }

    @Override
    public EntitiesList.View add(final int index, final String identifier, final String title, 
                                 final HeadingSize titleSize, 
                                 final boolean canRead, final boolean canRemove, 
                                 final boolean canSelect, final boolean isSelected) {
        addEntityInList(index, identifier, title, titleSize, canRead, canRemove, canSelect, isSelected);
        emptyEntitiesRow.setVisible(false);
        return this;
    }

    @Override
    public EntitiesList.View clear() {
        entitiesList.clear();
        emptyEntitiesRow.setVisible(true);
        return this;
    }

    private void addEntityInList(final int index, final String id, final String title, final HeadingSize titleSize, 
                                 final boolean canRead, final boolean canRemove, 
                                 final boolean canSelect, final boolean isSelected) {

        final LinkedGroupItem groupItem = new LinkedGroupItem();
        groupItem.addStyleName(style.entityGroup());
        if (canRead) {
            groupItem.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(final ClickEvent clickEvent) {
                    presenter.onReadEntity(id);
                }
            });
        }

        final HorizontalPanel groupPanel = new HorizontalPanel();
        groupPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        groupPanel.addStyleName(style.entityPanel());

        // Entity selection feature.
        if (canSelect) {
            final CheckBox checkBox = new CheckBox();
            checkBox.addStyleName(style.entityListButton());
            checkBox.addStyleName(style.left());
            checkBox.setValue(isSelected);
            checkBox.addDomHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent clickEvent) {
                    clickEvent.stopPropagation();
                    final boolean value = checkBox.getValue();
                    presenter.onSelectEntity(id, index, checkBox.getValue());
                }
            }, ClickEvent.getType());
            
            groupPanel.add(checkBox);
        }

        // Entity title.
        final Heading heading = new Heading(titleSize);
        heading.setText(title);
        heading.addStyleName(style.entityListTitle());
        groupPanel.add(heading);
        
        // Entity remove from list feature.
        if (canRemove) {

            // The remove button.
            final org.gwtbootstrap3.client.ui.Button removeButton = new org.gwtbootstrap3.client.ui.Button();
            removeButton.addStyleName(style.entityListButton());
            removeButton.setSize(ButtonSize.EXTRA_SMALL);
            removeButton.setIconPosition(IconPosition.RIGHT);
            removeButton.setType(ButtonType.DEFAULT);
            removeButton.setText(UsersManagementWidgetsConstants.INSTANCE.remove());
            removeButton.setTitle(UsersManagementWidgetsConstants.INSTANCE.remove());
            removeButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(final ClickEvent clickEvent) {
                    presenter.onRemoveEntity(id);
                }
            });
            removeButton.setVisible(false);
            groupPanel.add(removeButton);
            
            // Show the button on mouse over.
            groupItem.addDomHandler(new MouseOverHandler() {
                @Override
                public void onMouseOver(final MouseOverEvent mouseOverEvent) {
                    mouseOverEvent.stopPropagation();
                    removeButton.setVisible(true);
                }
            }, MouseOverEvent.getType());

            // Hide the button on mouse over.
            groupItem.addDomHandler(new MouseOutHandler() {
                @Override
                public void onMouseOut(final MouseOutEvent mouseOverEvent) {
                    mouseOverEvent.stopPropagation();
                    removeButton.setVisible(false);
                }
            }, MouseOutEvent.getType());
            
        }

        groupItem.add(groupPanel);
        entitiesList.add(groupItem);
    }

    private void applyPaginationConstraints(final EntitiesList.PaginationConstraints constraints) {
        boolean existsPagination = constraints != null;
        if (existsPagination) {
            final boolean isFirstPageEnabled = constraints.isFirstPageEnabled();
            final boolean isFirstPageVisible = constraints.isFirstPageVisible();
            final boolean isPrevPageEnabled = constraints.isPrevPageEnabled();
            final boolean isPrevPageVisible = constraints.isPrevPageVisible();
            final int currentPage = constraints.getCurrentPage();
            final boolean isNextPageEnabled = constraints.isNextPageEnabled();
            final boolean isNextPageVisible = constraints.isNextPageVisible();
            final boolean isLastPageEnabled = constraints.isLastPageEnabled();
            final boolean isLastPageVisible = constraints.isLastPageVisible();
            final Integer total = constraints.getTotal();
            
            // Only show pagination if necesssary.
            existsPagination = isPrevPageVisible || isNextPageVisible;
            if (existsPagination) {

                // First page anchor.
                firstPageAnchor.setEnabled(isFirstPageEnabled);
                firstPageAnchor.setVisible(isFirstPageVisible);
                if (firstPageAnchorClickHandlerRegistration != null) firstPageAnchorClickHandlerRegistration.removeHandler();;
                firstPageAnchorClickHandlerRegistration = firstPageAnchor.addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(final ClickEvent clickEvent) {
                        clickEvent.stopPropagation();
                        if (isFirstPageEnabled) {
                            presenter.onGoToFirstPage();
                        }
                    }
                });

                // Previous page anchor.
                prevPageAnchor.setEnabled(isPrevPageEnabled);
                prevPageAnchor.setVisible(isPrevPageVisible);
                if (prevPageAnchorClickHandlerRegistration != null) prevPageAnchorClickHandlerRegistration.removeHandler();;
                prevPageAnchorClickHandlerRegistration = prevPageAnchor.addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(final ClickEvent clickEvent) {
                        clickEvent.stopPropagation();
                        if (isPrevPageEnabled) {
                            presenter.onGoToPrevPage();
                        }
                    }
                });
                
                // Current page anchor.
                currentPageAnchor.setText(Integer.toString(currentPage));

                // Next page anchor.
                nextPageAnchor.setEnabled(isNextPageEnabled);
                nextPageAnchor.setVisible(isNextPageVisible);
                if (nextPageAnchorClickHandlerRegistration != null) nextPageAnchorClickHandlerRegistration.removeHandler();;
                nextPageAnchorClickHandlerRegistration = nextPageAnchor.addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent clickEvent) {
                        clickEvent.stopPropagation();
                        if (isNextPageEnabled) {
                            presenter.onGoToNextPage();
                        }
                    }
                });

                // Last page anchor.
                lastPageAnchor.setEnabled(isLastPageEnabled);
                lastPageAnchor.setVisible(isLastPageVisible);
                if (lastPageAnchorClickHandlerRegistration != null) lastPageAnchorClickHandlerRegistration.removeHandler();;
                lastPageAnchorClickHandlerRegistration = lastPageAnchor.addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(final ClickEvent clickEvent) {
                        clickEvent.stopPropagation();
                        if (isLastPageEnabled) {
                            presenter.onGoToLastPage();
                        }
                    }
                });
            }

            // Show total if available.
            if (total == null || total == 0) {
                totalBadge.setVisible(false);
            } else {
                final String t = UsersManagementWidgetsConstants.INSTANCE.total() + " " + total.toString() + " " + presenter.getEntityType();
                totalText.setText(t);
                totalBadge.setVisible(true);
            }
        }
        pagination.setVisible(existsPagination);
    }
    
}