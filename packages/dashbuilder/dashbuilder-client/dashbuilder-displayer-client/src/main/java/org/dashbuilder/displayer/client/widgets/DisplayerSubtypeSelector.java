/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dashbuilder.displayer.client.widgets;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.displayer.DisplayerSubType;
import org.dashbuilder.displayer.DisplayerType;
import org.dashbuilder.displayer.client.RendererLibrary;
import org.dashbuilder.displayer.client.RendererManager;
import org.dashbuilder.displayer.client.events.DisplayerSubtypeSelectedEvent;
import org.uberfire.client.mvp.UberView;

@Dependent
public class DisplayerSubtypeSelector implements IsWidget {

    public interface View extends UberView<DisplayerSubtypeSelector> {

        void clear();

        void show(DisplayerType type, DisplayerSubType subtype);

        void select(DisplayerSubType subtype);

        void showDefault(DisplayerType type);
    }

    View view = null;
    RendererManager rendererManager;
    DisplayerSubType selectedSubtype;
    Event<DisplayerSubtypeSelectedEvent> selectEvent;

    @Inject
    public DisplayerSubtypeSelector(View view,
                                    RendererManager rendererManager,
                                    Event<DisplayerSubtypeSelectedEvent> selectEvent) {
        this.view = view;
        this.rendererManager = rendererManager;
        this.selectEvent = selectEvent;
        view.init(this);
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public DisplayerSubType getSelectedSubtype() {
        return selectedSubtype;
    }

    public void init(DisplayerType type, DisplayerSubType selectedSubType) {
        view.clear();

        RendererLibrary rendererLibrary = rendererManager.getRendererForType(type);
        if (rendererLibrary != null) {
            List<DisplayerSubType> supportedSubTypes = rendererLibrary.getSupportedSubtypes(type);
            if (supportedSubTypes != null && !supportedSubTypes.isEmpty()) {
                for (int i = 0; i < supportedSubTypes.size(); i++) {
                    DisplayerSubType subtype = supportedSubTypes.get(i);

                    // Double check the renderer library for invalid subtypes for this type
                    if (!type.getSubTypes().contains(subtype)) {
                        throw new RuntimeException("Wrong subtype (" + subtype + ") indicated for type " + type + " by renderer library " + rendererLibrary.getUUID());
                    }

                    boolean initiallySelected = selectedSubType != null ? subtype == selectedSubType : i == 0;
                    view.show(type, subtype);
                    if (initiallySelected) {
                        view.select(subtype);
                    }
                }
            } else {
                view.showDefault(type);
            }
        }
    }

    void onSelect(DisplayerSubType subtype) {
        selectedSubtype = subtype;
        selectEvent.fire(new DisplayerSubtypeSelectedEvent(selectedSubtype));
    }
}
