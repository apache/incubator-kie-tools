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
import org.uberfire.ext.security.management.client.widgets.management.editor.AssignedEntitiesEditor;
import org.uberfire.ext.security.management.client.widgets.management.explorer.AbstractEntityExplorer;
import org.uberfire.ext.security.management.client.widgets.management.explorer.ExplorerViewContext;
import org.uberfire.mvp.Command;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * <p>Presenter base class for user's assigned entities editor.</p>
 *
 * @since 0.8.0
 */
public abstract class UserAssignedEntitiesEditor<T> implements IsWidget {

    ClientUserSystemManager userSystemManager;
    AbstractEntityExplorer<T> entitiesExplorer;
    public AssignedEntitiesEditor view;

    protected Set<T> entities = new LinkedHashSet<T>();
    protected boolean isEditMode;
    
    @Inject
    public UserAssignedEntitiesEditor(final ClientUserSystemManager userSystemManager,
                                      final AbstractEntityExplorer<T> entitiesExplorer,
                                      final AssignedEntitiesEditor view) {
        this.userSystemManager = userSystemManager;
        this.entitiesExplorer = entitiesExplorer;
        this.view = view;
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }
    
    
    protected abstract String getCancelText();
    protected abstract String getAddText();
    protected abstract String getTitle();
    protected abstract String getEntityIdentifier(T entity);
    
    
    /*  ******************************************************************************************************
                                 PUBLIC PRESENTER API 
     ****************************************************************************************************** */
    
    @PostConstruct
    public void init() {
        view.init(this);
        view.configure(entitiesExplorer.view);
        view.configureClose(getCancelText(), closeEditorCallback);
        view.configureSave(getAddText(), saveEditorCallback);
        entitiesExplorer.setPageSize(10);
    }

    /*  ******************************************************************************************************
                                 PUBLIC PRESENTER API 
     ****************************************************************************************************** */
    
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

    public void hide() {
        view.hide();
    }

    public void clear() {
        entitiesExplorer.clear();
        entities.clear();
    }
    
    
    /*  ******************************************************************************************************
                                 PRIVATE METHODS AND VALIDATORS
     ****************************************************************************************************** */
    
    protected ExplorerViewContext getViewContext() {
        return new ExplorerViewContext() {


            @Override
            public boolean canCreate() {
                return false;
            }

            @Override
            public boolean canRead() {
                return false;
            }

            @Override
            public boolean canDelete() {
                return false;
            }

            @Override
            public boolean canSelect() {
                return true;
            }

            @Override
            public Set<String> getSelectedEntities() {
                if (entities != null && !entities.isEmpty()) {
                    final Set<String> result = new HashSet<String>(entities.size());
                    for (final T entity : entities) {
                        result.add(UserAssignedEntitiesEditor.this.getEntityIdentifier(entity));
                    }
                    return result;
                }
                return null;
            }

            @Override
            public Set<String> getConstrainedEntities() {
                return new HashSet<String>();
            }

        };
    }
    
    protected void open(final User user) {
        assert user != null;
        view.show(getTitle() + " " + user.getIdentifier());
    }
    
    protected void onSave(final Set<String> selectedEntities) {
        
    }

    final Command closeEditorCallback = new Command() {
        @Override
        public void execute() {
            hide();
        }
    };

    final Command saveEditorCallback = new Command() {
        
        @Override
        public void execute() {
            hide();

            final Set<String> selected = entitiesExplorer.getSelectedEntities();
            entities.clear();
            onSave(selected);
            entitiesExplorer.clear();
        }
        
    };

}
