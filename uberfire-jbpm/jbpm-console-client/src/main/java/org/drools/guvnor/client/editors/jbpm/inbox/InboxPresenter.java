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

import org.jboss.bpm.console.client.model.TaskSummary;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import java.util.Date;
import java.util.Set;
import javax.annotation.PostConstruct;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;

@Dependent
@WorkbenchScreen(identifier = "Inbox")
public class InboxPresenter {

    public interface InboxView
            extends
            IsWidget {
    }
    @Inject
    InboxView view;
    @Inject
    Caller<TaskServiceEntryPoint> taskServices;
    private ListDataProvider<TaskSummary> dataProvider = new ListDataProvider<TaskSummary>();

    @WorkbenchPartTitle
    public String getTitle() {
        return "Inbox";
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return view;
    }

    public InboxPresenter() {
    }

    @PostConstruct
    public void init() {
        dataProvider.getList().add(new TaskSummary(999, 1, "My First Hardcoded Task", "Needs to be Done", "Task Desc", "Reserved", 1, false, "salaboy", "salaboy", new Date(), new Date(), new Date(), "haha2", 1));
        dataProvider.refresh();
    }

    public void addTask(String userId, String groupId) {
        String str = "(with (new Task()) { priority = " + Math.random() % 5 + ", taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = ";
        if(userId != null && !userId.equals("")){
                str += " [new User('"+userId+"')  ], }),";
        }
        if(groupId != null && !groupId.equals("")){
                str += " [new Group('"+groupId+"')  ], }),";
        }
        str += "names = [ new I18NText( 'en-UK', 'This is my task " + Math.random() + " name " + new Date() + "')] })";
        taskServices.call(new RemoteCallback<Long>() {
            @Override
            public void callback(Long taskId) {
                System.out.println("The returned Task Id is = " + taskId);
                dataProvider.refresh();
            }
        }).addTask(str, null);
        dataProvider.refresh();
    }

    public void refreshTasks(String userId) {
        taskServices.call(new RemoteCallback<List<TaskSummary>>() {
            @Override
            public void callback(List<TaskSummary> tasks) {
                System.out.println("Number of tasks returned = " + tasks.size() + " -> " + System.identityHashCode(this));
                dataProvider.setList(tasks);
                dataProvider.refresh();

            }
        }).getTasksAssignedAsPotentialOwner(userId, "en-UK");
        dataProvider.refresh();
    }

    public void startTasks(Set<TaskSummary> selectedTasks, String userId) {
        for (TaskSummary ts : selectedTasks) {
            taskServices.call(new RemoteCallback<List<TaskSummary>>() {
                @Override
                public void callback(List<TaskSummary> tasks) {

                    dataProvider.refresh();

                }
            }).start(ts.getId(), userId);
        }
        dataProvider.refresh();

    }
    public void claimTasks(Set<TaskSummary> selectedTasks, String userId) {
        for (TaskSummary ts : selectedTasks) {
            taskServices.call(new RemoteCallback<List<TaskSummary>>() {
                @Override
                public void callback(List<TaskSummary> tasks) {

                    dataProvider.refresh();

                }
            }).claim(ts.getId(), userId);
        }
        dataProvider.refresh();

    }

    public void completeTasks(Set<TaskSummary> selectedTasks, String userId) {
        for (TaskSummary ts : selectedTasks) {
            taskServices.call(new RemoteCallback<List<TaskSummary>>() {
                @Override
                public void callback(List<TaskSummary> tasks) {

                    dataProvider.refresh();

                }
            }).complete(ts.getId(), userId, null);
        }
        dataProvider.refresh();
    }

    public void addDataDisplay(HasData<TaskSummary> display) {
        dataProvider.addDataDisplay(display);
    }

    public ListDataProvider<TaskSummary> getDataProvider() {
        return dataProvider;
    }

    public void refreshData() {
        dataProvider.refresh();
    }
}
