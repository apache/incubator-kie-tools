/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;

import org.uberfire.client.ShowcaseEntryPoint.DumpLayout;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.util.Layouts;

import org.gwtbootstrap3.client.ui.Label;
import com.google.gwt.user.client.ui.IsWidget;

@Dependent
@WorkbenchScreen(identifier = "HelloWorldScreen")
public class HelloWorldScreen {

    private static final String ORIGINAL_TEXT = "Hello UberFire!";

    private final Label label = new Label( ORIGINAL_TEXT );

    @WorkbenchPartTitle
    public String getTitle() {
        return "Greetings";
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return label;
    }

    public void dumpLayout( @Observes DumpLayout dl ) {
        System.out.println( "Dumping HelloWorldScreen hierarchy:" );
        System.out.println( Layouts.getContainmentHierarchy( label ) );
    }
}