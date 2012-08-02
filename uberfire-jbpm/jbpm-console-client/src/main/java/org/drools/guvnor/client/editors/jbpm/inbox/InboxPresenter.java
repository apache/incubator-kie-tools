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

import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.drools.guvnor.client.editors.jbpm.inbox.events.InboxAction;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;

@ApplicationScoped
@WorkbenchScreen(identifier = "Inbox")
public class InboxPresenter {

    public interface InboxView
            extends
            IsWidget {

        void setPresenter(InboxPresenter presenter);

        void addTaskToGrid(TaskSummary task);
    }

    @Inject
    InboxView view;

    @Inject
    Caller<TaskServiceEntryPoint> taskServices;

    @PostConstruct
    public void init() {
        view.setPresenter(this);
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Task List ";
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return view;
    }

    public void addTask(final InboxAction ia) {
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('salaboy')  ], }),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
        taskServices.call(new RemoteCallback<Long>() {
            @Override
            public void callback(Long taskId) {
                System.out.println("The returned Task Id is = " + taskId);
            }
        }).addTask(str, null);
    }

    public void refreshTasks(final InboxAction ia) {
        taskServices.call(new RemoteCallback<List<TaskSummary>>() {
            @Override
            public void callback(List<TaskSummary> tasks) {
                System.out.println("Number of tasks returned = " + tasks.size() + " -> " + System.identityHashCode(this));
                for (TaskSummary task : tasks) {
                    view.addTaskToGrid(task);
                }
            }
        }).getTasksAssignedAsPotentialOwner("salaboy", "en-UK");
    }
}
