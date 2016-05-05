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
import org.jboss.errai.security.shared.api.Group;
import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.security.management.api.Capability;
import org.uberfire.ext.security.management.client.ClientUserSystemManager;
import org.uberfire.ext.security.management.client.widgets.management.events.OnDeleteEvent;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

/**
 * <p>Viewer class for a Group instance.</p>
 * <p>Additionally it shows a delete button, if the service provider supports the <code>CAN_DELETE_GROUP</code> capability.</p>
 *
 * @since 0.8.0
 */
@Dependent
public class GroupViewer implements IsWidget, org.uberfire.ext.security.management.client.editor.group.GroupViewer {

    public interface View extends UberView<GroupViewer> {
        View show(final String name);
        View setShowDeleteButton(boolean isVisible);
        View clear();
    }
    
    ClientUserSystemManager userSystemManager;
    Event<OnDeleteEvent> onDeleteEvent;
    public View view;

    Group group;

    @Inject
    public GroupViewer(final ClientUserSystemManager userSystemManager,
                       final Event<OnDeleteEvent> onDeleteEvent,
                       final View view) {
        this.userSystemManager = userSystemManager;
        this.onDeleteEvent = onDeleteEvent;
        this.view = view;
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    /*  ******************************************************************************************************
                                 PUBLIC PRESENTER API 
     ****************************************************************************************************** */

    @Override
    public void show(final Group group) {
        clear();
        this.group = group;
        view.show(group.getName());
        view.setShowDeleteButton(canDelete());
    }
    
    public void clear() {
        view.clear();
        group = null;
    }
    
     /*  ******************************************************************************************************
                                 PACKAGE PROTECTED METHODS FOR USING AS CALLBACKS FOR THE VIEW 
     ****************************************************************************************************** */
    
    boolean canDelete() {
        return userSystemManager.isGroupCapabilityEnabled(Capability.CAN_DELETE_GROUP);
    }
    
    void onDelete() {
        GroupViewer.this.onDeleteEvent.fire(new OnDeleteEvent(GroupViewer.this, GroupViewer.this.group));
    }

}
