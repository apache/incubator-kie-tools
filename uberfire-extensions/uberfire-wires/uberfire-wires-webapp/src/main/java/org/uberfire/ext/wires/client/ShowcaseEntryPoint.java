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
package org.uberfire.ext.wires.client;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.RootPanel;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.uberfire.client.mvp.ActivityManager;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.widgets.menu.megamenu.WorkbenchMegaMenuPresenter;
import org.uberfire.ext.preferences.client.admin.AdminPagePerspective;
import org.uberfire.ext.preferences.client.admin.page.AdminPage;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.impl.ConditionalPlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.menu.MenuPosition;
import org.uberfire.workbench.model.menu.Menus;

import static org.uberfire.workbench.model.menu.MenuFactory.newTopLevelMenu;

/**
 * GWT's Entry-point for Wires
 */
@EntryPoint
public class ShowcaseEntryPoint {

    @Inject
    private PlaceManager placeManager;

    @Inject
    private WorkbenchMegaMenuPresenter menubar;

    @Inject
    private AdminPage adminPage;

    @Inject
    private ActivityManager activityManager;

    public static native void redirect(String url)/*-{
        $wnd.location = url;
    }-*/;

    @PostConstruct
    public void startApp() {
        setupMenu();
        setupSettings();
        hideLoadingPopup();
    }

    private void setupSettings() {
        adminPage.addScreen("root",
                            "Wires Admin Tools");
        adminPage.setDefaultScreen("root");

        adminPage.addTool("root",
                          "Apps",
                          "fa-map",
                          "General",
                          () -> placeManager.goTo(new DefaultPlaceRequest("AppsPerspective")));

        adminPage.addPreference("root",
                                "MyPreference",
                                "My Preferences",
                                "fa-gear",
                                "Preferences");

        adminPage.addPreference("root",
                                "MySharedPreference",
                                "Shared Preferences",
                                "fa-share-alt",
                                "Preferences");
    }

    private void setupMenu() {
        final Menus menus = newTopLevelMenu("Wires ScratchPad").respondsWith(new Command() {
            @Override
            public void execute() {
                placeManager.goTo(new DefaultPlaceRequest("WiresScratchPadPerspective"));
            }
        }).endMenu().newTopLevelMenu("Wires Trees").respondsWith(new Command() {
            @Override
            public void execute() {
                placeManager.goTo(new DefaultPlaceRequest("WiresTreesPerspective"));
            }
        }).endMenu().newTopLevelMenu("Wires Grids").respondsWith(new Command() {
            @Override
            public void execute() {
                placeManager.goTo(new DefaultPlaceRequest("WiresGridsDemoPerspective"));
            }
        }).endMenu().newTopLevelMenu("Wires BPMN").respondsWith(new Command() {
            @Override
            public void execute() {
                placeManager.goTo(new DefaultPlaceRequest("BpmnPerspective"));
            }
        }).endMenu().newTopLevelMenu("Bayesian Networks").respondsWith(new Command() {
            @Override
            public void execute() {
                placeManager.goTo(new DefaultPlaceRequest("WiresBayesianPerspective"));
            }
        }).endMenu().newTopLevelMenu("Extensions").respondsWith(new Command() {
            @Override
            public void execute() {
                placeManager.goTo(new DefaultPlaceRequest("PlugInAuthoringPerspective"));
            }
        }).endMenu().newTopLevelMenu("Apps").respondsWith(new Command() {
            @Override
            public void execute() {
                placeManager.goTo(new DefaultPlaceRequest("AppsPerspective"));
            }
        }).endMenu().newTopLevelMenu("Social").respondsWith(new Command() {
            @Override
            public void execute() {
                placeManager.goTo(new ConditionalPlaceRequest("SocialPerspective"));
            }
        }).endMenu()
                .newTopLevelMenu("Widgets").respondsWith(new Command() {
                    @Override
                    public void execute() {
                        placeManager.goTo(new ConditionalPlaceRequest("UFWidgets").when(p -> true).orElse(new DefaultPlaceRequest("AppsPerspective")));
                    }
                }).endMenu().newTopLevelMenu("Admin").respondsWith(new Command() {
                    @Override
                    public void execute() {
                        placeManager.goTo(new DefaultPlaceRequest(AdminPagePerspective.IDENTIFIER));
                    }
                }).endMenu().newTopLevelMenu("Logout").position(MenuPosition.RIGHT).respondsWith(new Command() {
                    @Override
                    public void execute() {
                        redirect(GWT.getModuleBaseURL() + "uf_logout");
                    }
                }).endMenu().build();
        menubar.addMenus(menus);
    }

    // Fade out the "Loading application" pop-up
    private void hideLoadingPopup() {
        final Element e = RootPanel.get("loading").getElement();

        new Animation() {

            @Override
            protected void onUpdate(double progress) {
                e.getStyle().setOpacity(1.0 - progress);
            }

            @Override
            protected void onComplete() {
                e.getStyle().setVisibility(Style.Visibility.HIDDEN);
            }
        }.run(500);
    }
}