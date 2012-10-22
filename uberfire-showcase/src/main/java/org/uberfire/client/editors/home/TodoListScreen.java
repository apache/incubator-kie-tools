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

package org.uberfire.client.editors.home;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.markdown.Markdown;

@Dependent
@WorkbenchScreen(identifier = "TodoListScreen")
public class TodoListScreen
        extends Composite
        implements RequiresResize {

    interface ViewBinder
            extends
            UiBinder<Widget, TodoListScreen> {

    }

    private static ViewBinder uiBinder = GWT.create(ViewBinder.class);

    @UiField
    protected Markdown markdown;

    @PostConstruct
    public void init() {
        initWidget(uiBinder.createAndBindUi(this));

        markdown.setContent("UberFire Todo List\n===\nUberFire is an amazing project, with a bright future, but as an Alpha version we know that it needs some improvements, here are some things in our todo list:\n\n  - Improve Design \n  - Implement Themes");
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Todo List";
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return this;
    }

    @Override
    public void onResize() {
        int height = getParent().getOffsetHeight();
        int width = getParent().getOffsetWidth();
        setPixelSize(width, height);
    }

}