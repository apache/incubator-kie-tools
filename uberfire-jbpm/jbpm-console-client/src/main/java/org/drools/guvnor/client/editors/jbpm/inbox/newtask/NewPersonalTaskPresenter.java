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
package org.drools.guvnor.client.editors.jbpm.inbox.newtask;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import java.util.Date;
import javax.annotation.PostConstruct;
import org.drools.guvnor.client.editors.jbpm.inbox.TaskServiceEntryPoint;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;

@Dependent
@WorkbenchScreen(identifier = "New Task")
public class NewPersonalTaskPresenter {

    public interface InboxView
            extends
            IsWidget {
    }
    @Inject
    InboxView view;
    @Inject
    Caller<TaskServiceEntryPoint> taskServices;

    @WorkbenchPartTitle
    public String getTitle() {
        return "New Task";
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return view;
    }

    public NewPersonalTaskPresenter() {
    }

    @PostConstruct
    public void init() {
        
    }

    public void addTask(final String userId, String groupId, String taskName, String taskDescription, Date dueDate, String priority) {
        String str = "(with (new Task()) { priority = " + priority + ", taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = ";
        if (userId != null && !userId.equals("")) {
            str += " [new User('" + userId + "')  ], }),";
        }
        if (groupId != null && !groupId.equals("")) {
            str += " [new Group('" + groupId + "')  ], }),";
        }
        
        str += "names = [ new I18NText( 'en-UK', '" + taskName + "')] })";
        taskServices.call(new RemoteCallback<Long>() {
            @Override
            public void callback(Long taskId) {
                System.out.println("The returned Task Id is = " + taskId);
                
            }
        }).addTask(str, null);
        
    }

   
   
}
