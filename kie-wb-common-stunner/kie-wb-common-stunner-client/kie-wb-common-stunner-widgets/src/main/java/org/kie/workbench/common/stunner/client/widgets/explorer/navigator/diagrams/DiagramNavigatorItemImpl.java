/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.widgets.explorer.navigator.diagrams;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.stunner.client.widgets.explorer.navigator.NavigatorItem;
import org.kie.workbench.common.stunner.client.widgets.explorer.navigator.NavigatorItemView;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.lookup.diagram.DiagramRepresentation;
import org.uberfire.mvp.Command;

@Dependent
public class DiagramNavigatorItemImpl implements IsWidget,
                                                 DiagramNavigatorItem {

    private final ShapeManager shapeManager;
    private final NavigatorItemView<NavigatorItem> view;

    private String name;
    private Command callback;

    @Inject
    public DiagramNavigatorItemImpl(final ShapeManager shapeManager,
                                    final NavigatorItemView<NavigatorItem> view) {
        this.shapeManager = shapeManager;
        this.view = view;
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public void show(final DiagramRepresentation diagramRepresentation,
                     final int widthInPx,
                     final int heightInPx,
                     final Command callback) {
        this.callback = callback;
        this.name = diagramRepresentation.getName();
        view.setUUID(name);
        view.setItemTitle(diagramRepresentation.getTitle());

        //Set size before Uri/data as we cannot scale until the image is loaded and it's real size known
        view.setItemPxSize(widthInPx,
                           heightInPx);

        //Set Uri/data. Image's LoadHandler will set size requested above after image is loaded from Uri/data
        final String thumbData = diagramRepresentation.getThumbImageData();
        if (isEmpty(thumbData)) {
            final String defSetId = diagramRepresentation.getDefinitionSetId();
            final SafeUri thumbUri = shapeManager.getThumbnail(defSetId);
            view.setThumbUri(thumbUri);
        } else {
            view.setThumbData(thumbData);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public NavigatorItemView getView() {
        return view;
    }

    @Override
    public void onItemSelected() {
        if (null != callback) {
            callback.execute();
        }
    }

    private boolean isEmpty(final String s) {
        return s == null || s.trim().length() == 0;
    }

    @PreDestroy
    public void destroy() {
        name = null;
        callback = null;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DiagramNavigatorItemImpl)) {
            return false;
        }
        final DiagramNavigatorItemImpl that = (DiagramNavigatorItemImpl) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name == null ? 0 : ~~name.hashCode();
    }
}
