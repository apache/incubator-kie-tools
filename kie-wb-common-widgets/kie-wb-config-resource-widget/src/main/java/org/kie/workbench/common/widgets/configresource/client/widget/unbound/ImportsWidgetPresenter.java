/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.widgets.configresource.client.widget.unbound;

import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.common.services.project.model.ProjectImports;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

public class ImportsWidgetPresenter
        implements ImportsWidgetView.Presenter,
                   IsWidget {

    private ImportsWidgetView view;

    public ImportsWidgetPresenter() {
    }

    @Inject
    public ImportsWidgetPresenter(final ImportsWidgetView view) {
        this.view = view;
        view.init(this);
    }

    @Override
    public void setContent(final ProjectImports importTypes,
                           final boolean isReadOnly) {
        checkNotNull("importTypes",
                     importTypes);

        view.setContent(importTypes.getImports().getImports(),
                        isReadOnly);
        view.updateRenderedColumns();
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }
}
