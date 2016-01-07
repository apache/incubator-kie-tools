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

package org.uberfire.ext.security.management.client.widgets.management.editor.group;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.ext.security.management.client.resources.i18n.UsersManagementWidgetsConstants;
import org.uberfire.ext.security.management.client.widgets.management.editor.AssignedEntitiesEditor;
import org.uberfire.ext.security.management.client.widgets.management.editor.AssignedEntitiesInlineEditor;
import org.uberfire.ext.security.management.client.widgets.management.events.AddUsersToGroupEvent;
import org.uberfire.ext.security.management.client.widgets.management.explorer.ExplorerViewContext;
import org.uberfire.ext.security.management.client.widgets.management.explorer.UsersExplorer;
import org.uberfire.mvp.Command;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.Set;

/**
 * <p>Presenter class for assign users to a new group.</p>
 * 
 * @since 0.8.0
 */
@Dependent
public class GroupUsersAssignment implements IsWidget {

    AssignedEntitiesEditor<GroupUsersAssignment> view;
    UsersExplorer usersExplorer;
    Event<AddUsersToGroupEvent> addUsersToGroupEvent;

    @Inject
    public GroupUsersAssignment(@AssignedEntitiesInlineEditor final AssignedEntitiesEditor<GroupUsersAssignment> view, 
                                final UsersExplorer usersExplorer, 
                                final Event<AddUsersToGroupEvent> addUsersToGroupEvent) {
        this.view = view;
        this.usersExplorer = usersExplorer;
        this.addUsersToGroupEvent = addUsersToGroupEvent;
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    /*  ******************************************************************************************************
                                 PUBLIC PRESENTER API 
     ****************************************************************************************************** */
    
    @PostConstruct
    public void init() {
        view.init(this);
        view.configure(usersExplorer.view);
        view.configureSave(UsersManagementWidgetsConstants.INSTANCE.addUsersToGroup(), saveEditorCallback);
    }
    
    public void show(final String header) {
        // Clear current view.
        clear();

        showUsersModal();
        view.show(header);
    }

    public void hide() {
        view.hide();
    }
    
    public void clear() {
        usersExplorer.clear();
    }
    

     /*  ******************************************************************************************************
                                     PRIVATE METHODS FOR INTERNAL PRESENTER LOGIC 
         ****************************************************************************************************** */

    final Command saveEditorCallback = new Command() {
        @Override
        public void execute() {
            hide();
            // Fire the assign selection event.
            final Set<String> selectedUsers= usersExplorer.getSelectedEntities();
            // Delegate the recently updated assigned groups for the user.
            addUsersToGroupEvent.fire(new AddUsersToGroupEvent(GroupUsersAssignment.this, selectedUsers));
        }
    };
    
    private void showUsersModal() {
        usersExplorer.show(new ExplorerViewContext() {


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
                return null;
            }
        });
    }
    
}
