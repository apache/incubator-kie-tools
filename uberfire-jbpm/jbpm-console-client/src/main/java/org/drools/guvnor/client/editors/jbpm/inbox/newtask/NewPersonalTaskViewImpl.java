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

import org.jboss.bpm.console.client.model.TaskSummary;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DatePicker;
import java.util.Date;
import org.uberfire.client.mvp.PlaceManager;

@Dependent
public class NewPersonalTaskViewImpl extends Composite implements NewPersonalTaskPresenter.InboxView {

    @Inject
    private UiBinder<Widget, NewPersonalTaskViewImpl> uiBinder;
    @Inject
    private PlaceManager placeManager;
    @Inject
    private NewPersonalTaskPresenter presenter;
    @UiField
    public Button addTaskButton;
    @UiField
    public Button clearButton;
    
    @UiField
    public TextBox userText;
    @UiField
    public TextBox groupText;
    @UiField
    public TextBox taskNameText;
    @UiField
    public TextBox taskDescriptionText;
    @UiField
    public TextBox taskPriorityText;
    @UiField
    public DatePicker dueDate;
    
    
    @PostConstruct
    public void init() {

        initWidget(uiBinder.createAndBindUi(this));
       
    }

    @UiHandler("addTaskButton")
    public void addTaskButton(ClickEvent e) {
        presenter.addTask(userText.getText(), groupText.getText(), 
                            taskNameText.getText(), taskDescriptionText.getText(), 
                            dueDate.getValue(), taskPriorityText.getText());
    }

    

    @UiHandler("clearButton")
    public void clearButton(ClickEvent e) {
       userText.setText("");
       groupText.setText("");
       taskNameText.setText("");
       taskDescriptionText.setText("");
       dueDate.setValue(new Date());

    }

    
}
