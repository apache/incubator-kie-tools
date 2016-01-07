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

package org.uberfire.ext.security.management.client.widgets.management.editor.user;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.ext.security.management.client.ClientUserSystemManager;
import org.uberfire.ext.security.management.client.resources.i18n.UsersManagementWidgetsConstants;
import org.uberfire.ext.security.management.client.widgets.management.editor.AssignedEntitiesExplorer;
import org.uberfire.ext.security.management.client.widgets.management.list.EntitiesList;
import org.uberfire.ext.security.management.client.widgets.management.list.EntitiesPagedList;
import org.uberfire.ext.security.management.client.widgets.popup.ConfirmBox;
import org.uberfire.mvp.Command;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * <p>Base presenter class for user's assigned entities explorer widget.</p>
 * <p>It's considered an Editor due to it allows removing assigned user's entities.</p>
 * 
 * @since 0.8.0
 */
public abstract class UserAssignedEntitiesExplorer<T> implements IsWidget {

    protected final static int PAGE_SIZE = 5;

    ClientUserSystemManager userSystemManager;
    ConfirmBox confirmBox;
    EntitiesPagedList<T> entitiesList;
    public AssignedEntitiesExplorer view;

    @Inject
    public UserAssignedEntitiesExplorer(final ClientUserSystemManager userSystemManager,
                                        final ConfirmBox confirmBox,
                                        final EntitiesPagedList<T> entitiesList,
                                        final AssignedEntitiesExplorer view) {
        this.userSystemManager = userSystemManager;
        this.confirmBox = confirmBox;
        this.entitiesList = entitiesList;
        this.view = view;
    }

    protected Set<T> entities = new LinkedHashSet<T>(); 
    protected boolean isEditMode;

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    protected abstract String getEmptyText();

    protected abstract String getEntityType();

    protected abstract String getTitle();

    protected abstract String getEntityId(final T entity);

    protected abstract String getEntityName(final T entity);
    
    protected abstract String getEnsureRemoveText();

    protected abstract boolean canAssignEntities();

    protected abstract void doShow();

    protected abstract void removeEntity(String name);

    /*  ******************************************************************************************************
                                 PUBLIC PRESENTER API 
     ****************************************************************************************************** */

    @PostConstruct
    public void init() {
        entitiesList.setPageSize(PAGE_SIZE);
        entitiesList.setEmptyEntitiesText(getEmptyText());
        view.configure(getTitle(), entitiesList.view);
    }

    public void show(final User user) {
        clear();
        this.isEditMode = false;
        open(user);
    }

    public void edit(final User user) {
        clear();
        this.isEditMode = true;
        open(user);
    }

    public void flush() {
        assert isEditMode;
        // No additional flush logic to perform here.
    }

    public Set<T> getValue() {
        return entities;
    }

    public void setViolations(Set<ConstraintViolation<User>> constraintViolations) {
        //  Currently no violations expected.
    }

    public void clear() {
        view.clear();
        entitiesList.clear();
        isEditMode = false;
        entities.clear();
    }
    
    
    /*  ******************************************************************************************************
                                 OTHER METHODS AND VALIDATORS
     ****************************************************************************************************** */
    
    protected EntitiesList.Callback<T> getCallback() {
        return new EntitiesList.Callback<T>() {
            @Override
            public String getEntityType() {
                return UserAssignedEntitiesExplorer.this.getEntityType();
            }

            @Override
            public boolean canRead() {
                return true;
            }

            @Override
            public boolean canRemove() {
                return canAssignEntities();
            }

            @Override
            public boolean canSelect() {
                return false;
            }

            @Override
            public boolean isSelected(final String identifier) {
                return false;
            }

            @Override
            public String getIdentifier(final T entity) {
                return UserAssignedEntitiesExplorer.this.getEntityId(entity);
            }

            @Override
            public String getTitle(final T entity) {
                return UserAssignedEntitiesExplorer.this.getEntityName(entity);
            }

            @Override
            public void onReadEntity(final String identifier) {
                // Not allowed.
            }

            @Override
            public void onRemoveEntity(final String identifier) {
                if (identifier != null) {
                    confirmBox.show(UsersManagementWidgetsConstants.INSTANCE.confirmAction(), UserAssignedEntitiesExplorer.this.getEnsureRemoveText(),
                            new Command() {
                                @Override
                                public void execute() {
                                    removeEntity(identifier);
                                }
                            });

                }
            }

            @Override
            public void onSelectEntity(final String identifier, final boolean isSelected) {
                // Entity selection not available for the explorer widget.
            }

            @Override
            public void onChangePage(final int currentPage, final int goToPage) {
                // Do nothing by default, let the entitiesList paginate.
            }
        };
    }
    
    protected void open(final User user) {
        assert user != null;
        doShow();
    }

}