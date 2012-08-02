/*
 * Copyright 2012 JBoss Inc
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

package org.drools.guvnor.client.editors.jbpm.inbox;

import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.Window;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;

import com.google.gwt.user.client.ui.IsWidget;
import java.util.List;
import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import org.drools.guvnor.client.editors.jbpm.inbox.events.AddTaskUIEvent;
import org.drools.guvnor.client.editors.jbpm.inbox.events.InboxAction;
import org.drools.guvnor.client.editors.jbpm.inbox.events.RefreshTasksUIEvent;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;

@Dependent
@WorkbenchScreen(identifier = "Inbox")
public class InboxPresenter {

    public interface InboxView
        extends
        IsWidget {
        DataGrid<TaskSummary> getDataGrid();
    }

    @Inject
    InboxView view;

    @Inject
    Caller<TaskServiceEntryPoint> taskServices;
    
    public InboxPresenter() {
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Task List ";
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return view;
    }

    public void addTask(@Observes(notifyObserver= Reception.IF_EXISTS) @AddTaskUIEvent InboxAction ia ){
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('salaboy')  ], }),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
        taskServices.call( new RemoteCallback<Long>() {
            @Override
            public void callback(Long taskId) {
                System.out.println("The returned Task Id is = "+taskId);
            }
        } ).addTask(str, null);
    }
    
    public void refreshTasks(@Observes(notifyObserver= Reception.IF_EXISTS) @RefreshTasksUIEvent InboxAction ia ){
        taskServices.call( new RemoteCallback<List<TaskSummary>>() {
            @Override
            public void callback(List<TaskSummary> tasks) {
                System.out.println("Number of tasks returned = "+tasks.size());
            }
        } ).getTasksAssignedAsPotentialOwner("salaboy", "en-UK");
    }
}
