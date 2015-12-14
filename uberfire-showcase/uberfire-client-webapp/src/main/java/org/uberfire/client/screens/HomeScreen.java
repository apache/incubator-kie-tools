/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.client.screens;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;

import org.uberfire.client.ShowcaseEntryPoint.DumpLayout;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.util.Layouts;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnShutdown;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.shared.Mood;

import org.gwtbootstrap3.client.ui.Label;
import com.google.gwt.user.client.ui.IsWidget;

@Dependent
@WorkbenchScreen(identifier = "HomeScreen")
public class HomeScreen {

    private static final String ORIGINAL_TEXT = "How do you feel?";

    private final Label label = new Label( ORIGINAL_TEXT );

    @WorkbenchPartTitle
    public String getTitle() {
        return "homeScreen";
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return label;
    }

    public void onMoodChange(@Observes Mood mood) {
        label.setText("I understand you are feeling " + mood.getText());
    }

    public void dumpHierarchy(@Observes DumpLayout e) {
        System.out.println("Containment hierarchy of HomeScreen label:");
        System.out.println(Layouts.getContainmentHierarchy( label ));
    }

    @PostConstruct
    void postConstruct() {
        System.out.println("HomeScreen@" + System.identityHashCode( this ) + " bean created");
    }

    @OnStartup
    void onStartup() {
        System.out.println("HomeScreen@" + System.identityHashCode( this ) + " starting");
    }

    @OnOpen
    void onOpen() {
        System.out.println("HomeScreen@" + System.identityHashCode( this ) + " opening");
    }

    @OnClose
    void onClose() {
        System.out.println("HomeScreen@" + System.identityHashCode( this ) + " closing");
    }

    @OnShutdown
    void onShutdown() {
        System.out.println("HomeScreen@" + System.identityHashCode( this ) + " shutting down");
    }

    @PreDestroy
    void preDestroy() {
        System.out.println("HomeScreen@" + System.identityHashCode( this ) + " bean destroyed");
    }
}