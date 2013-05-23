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

package org.kie.workbench.projecteditor.client.messages;

import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import org.kie.workbench.projecteditor.client.resources.i18n.ProjectEditorConstants;
import org.uberfire.client.annotations.DefaultPosition;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.Position;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@WorkbenchScreen(identifier = "org.kie.guvnor.Problems")
public class ProblemsScreen
        implements ProblemsScreenView.Presenter {

    private final PlaceManager placeManager;
    private final ProblemsScreenView view;
    private final ProblemsService problemsService;

    @Inject
    public ProblemsScreen(ProblemsScreenView view,
                          PlaceManager placeManager,
                          ProblemsService problemsService) {
        this.view = view;
        this.placeManager = placeManager;
        this.problemsService = problemsService;

        view.setPresenter(this);
    }

    @DefaultPosition
    public Position getDefaultPosition() {
        return Position.SOUTH;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return ProjectEditorConstants.INSTANCE.Problems();
    }

    @WorkbenchPartView
    public Widget asWidget() {
        return view.asWidget();
    }
}
