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

import java.util.Set;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.security.shared.api.Group;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.security.management.api.Capability;
import org.uberfire.ext.security.management.client.ClientUserSystemManager;
import org.uberfire.ext.security.management.client.widgets.management.editor.acl.ACLEditor;
import org.uberfire.ext.security.management.client.widgets.management.editor.acl.ACLSettings;
import org.uberfire.ext.security.management.client.widgets.management.editor.acl.ACLViewer;
import org.uberfire.ext.security.management.client.widgets.management.events.OnDeleteEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.OnEditEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.OnShowEvent;
import org.uberfire.security.authz.PermissionCollection;

/**
 * <p>Editor class for a Group instance.</p>
 * <p>Additionally it shows a delete button, if the service provider supports the <code>CAN_DELETE_GROUP</code> capability.</p>
 * @since 0.9.0
 */
@Dependent
public class GroupEditor implements IsWidget,
                                    org.uberfire.ext.security.management.client.editor.group.GroupEditor {

    public View view;
    ClientUserSystemManager userSystemManager;
    Event<OnDeleteEvent> onDeleteEvent;
    Event<OnEditEvent> onEditEvent;
    Event<OnShowEvent> onShowEvent;
    ACLSettings aclSettings;
    ACLViewer aclViewer;
    ACLEditor aclEditor;
    Group group;
    boolean isEditMode;
    @Inject
    public GroupEditor(final ClientUserSystemManager userSystemManager,
                       final Event<OnEditEvent> onEditEvent,
                       final Event<OnShowEvent> onShowEvent,
                       final Event<OnDeleteEvent> onDeleteEvent,
                       final ACLSettings aclSettings,
                       final ACLViewer aclViewer,
                       final ACLEditor aclEditor,
                       final View view) {
        this.userSystemManager = userSystemManager;
        this.onDeleteEvent = onDeleteEvent;
        this.aclSettings = aclSettings;
        this.aclViewer = aclViewer;
        this.aclEditor = aclEditor;
        this.onEditEvent = onEditEvent;
        this.onShowEvent = onShowEvent;
        this.view = view;
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    @PostConstruct
    public void init() {
        view.init(this);
        view.setACLSettings(aclSettings);
    }

    @Override
    public String name() {
        return group.getName();
    }

    @Override
    public PerspectiveActivity homePerspective() {
        return aclSettings.getHomePerspective();
    }

    @Override
    public int groupPriority() {
        return aclSettings.getPriority();
    }

    @Override
    public PermissionCollection permissions() {
        return aclEditor.getPermissions();
    }

    public ACLSettings getAclSettings() {
        return aclSettings;
    }

    public ACLEditor getAclEditor() {
        return aclEditor;
    }

    @Override
    public void show(final Group group) {
        clear();
        this.isEditMode = false;
        open(group);
        onShowEvent.fire(new OnShowEvent(this,
                                         group));
    }

    /*  ******************************************************************************************************
                                 PUBLIC PRESENTER API 
     ****************************************************************************************************** */

    @Override
    public void edit(final Group group) {
        clear();
        this.isEditMode = true;
        open(group);
    }

    public void clear() {
        view.clear();
        group = null;
    }

    @Override
    public void flush() {
        assert group != null;
        assert isEditMode;
        // No additional flush logic to perform here.
    }

    @Override
    public Group getValue() {
        return group;
    }

    @Override
    public void setViolations(final Set<ConstraintViolation<Group>> violations) {
        //  Currently no violations expected.
    }

    boolean canDelete() {
        return userSystemManager.isGroupCapabilityEnabled(Capability.CAN_DELETE_GROUP);
    }

     /*  ******************************************************************************************************
                                 PACKAGE PROTECTED METHODS FOR USING AS CALLBACKS FOR THE VIEW 
     ****************************************************************************************************** */

    void onDelete() {
        GroupEditor.this.onDeleteEvent.fire(new OnDeleteEvent(GroupEditor.this,
                                                              GroupEditor.this.group));
    }

    void onEdit() {
        onEditEvent.fire(new OnEditEvent(this,
                                         group));
    }

    protected void open(final Group group) {
        assert group != null;
        this.group = group;

        // Role name
        final String name = group.getName();
        view.show(name);

        // Edit mode
        view.setEditButtonVisible(!isEditMode);
        view.setDeleteButtonVisible(isEditMode && canDelete());

        // ACL Editor/Viewer
        if (isEditMode) {
            aclSettings.edit(group);
            aclEditor.edit(group);
            view.editACL(aclEditor);
        } else {
            aclSettings.show(group);
            aclViewer.show(group);
            view.showACL(aclViewer);
        }
    }

     /*  ******************************************************************************************************
                                 PRIVATE METHODS AND VALIDATORS
     ****************************************************************************************************** */

    public interface View extends UberView<GroupEditor> {

        View show(final String name);

        View setEditButtonVisible(boolean isVisible);

        View setDeleteButtonVisible(boolean isVisible);

        View clear();

        View setACLSettings(IsWidget aclSettings);

        View showACL(IsWidget aclViewer);

        View editACL(IsWidget aclEditor);
    }
}
