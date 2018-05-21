/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.widgets.client.menu;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.IsWidget;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.mvp.PerspectiveManager;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.ForcedPlaceRequest;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.MenuPosition;
import org.uberfire.workbench.model.menu.impl.BaseMenuCustom;

/**
 * A custom MenuBuilder that creates a button that, when clicked, resets Perspective definitions for the active User
 */
@ApplicationScoped
public class ResetPerspectivesMenuBuilder implements MenuFactory.CustomMenuBuilder {

    @Inject
    private PerspectiveManager perspectiveManager;

    @Inject
    private PlaceManager placeManager;

    private AnchorListItem link = new AnchorListItem();

    public ResetPerspectivesMenuBuilder() {
        link.setIcon(IconType.MEDKIT);

        link.getWidget(0).setStyleName("nav-item-iconic"); // Fix for IE11

        link.setTitle(CommonConstants.INSTANCE.ResetPerspectivesTooltip());
        link.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (Window.confirm(CommonConstants.INSTANCE.PromptResetPerspectives())) {
                    final PerspectiveActivity currentPerspective = perspectiveManager.getCurrentPerspective();
                    perspectiveManager.removePerspectiveStates(new Command() {
                        @Override
                        public void execute() {
                            if (currentPerspective != null) {
                                //Use ForcedPlaceRequest to force re-loading of the current Perspective
                                final PlaceRequest pr = new ForcedPlaceRequest(currentPerspective.getIdentifier(),
                                                                               currentPerspective.getPlace().getParameters());
                                placeManager.goTo(pr);
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public void push(final MenuFactory.CustomMenuBuilder element) {
        //Do nothing
    }

    @Override
    public MenuItem build() {
        return new BaseMenuCustom<IsWidget>() {
            @Override
            public IsWidget build() {
                return link;
            }

            @Override
            public MenuPosition getPosition() {
                return MenuPosition.RIGHT;
            }
        };
    }
}
