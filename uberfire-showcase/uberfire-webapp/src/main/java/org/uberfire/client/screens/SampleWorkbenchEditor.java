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

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.IsWidget;
import org.gwtbootstrap3.client.ui.html.Paragraph;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.views.pfly.multipage.PageImpl;
import org.uberfire.client.workbench.type.AnyResourceType;
import org.uberfire.client.workbench.widgets.multipage.MultiPageEditor;

@Dependent
@WorkbenchEditor( identifier = "SampleWorkbenchEditor", supportedTypes = AnyResourceType.class )
public class SampleWorkbenchEditor {

    @Inject
    private MultiPageEditor pageEditor;

    @PostConstruct
    public void init() {
        pageEditor.addPage( newPage( "Source" ) );
        pageEditor.addPage( newPage( "Overview" ) );
        pageEditor.addPage( newPage( "Metadata" ) );
        pageEditor.addPage( newPage( "Preview" ) );
        pageEditor.addPage( newPage( "Details" ) );
    }

    private static PageImpl newPage( final String name ) {
        final Paragraph paragraph = new Paragraph( name + " Tab" );
        paragraph.getElement().getStyle().setMargin( 15, Style.Unit.PX );
        return new PageImpl( paragraph, name );
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Sample Workbench Editor";
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return pageEditor.getView();
    }
}
