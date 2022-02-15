/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.dashbuilder.client;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.RootPanel;
import org.dashbuilder.client.cms.screen.explorer.NavigationExplorerScreen;
import org.dashbuilder.client.navbar.AppHeader;
import org.dashbuilder.client.navigation.NavTreeDefinitions;
import org.dashbuilder.client.navigation.NavigationManager;
import org.jboss.errai.ioc.client.api.EntryPoint;

/**
 * Entry-point for the Dashbuilder showcase
 */
@EntryPoint
public class ShowcaseEntryPoint {

	@Inject
	private NavigationManager navigationManager;

	@Inject
	NavigationExplorerScreen navigationExplorerScreen;

	@Inject
	private AppHeader appHeader;

	@PostConstruct
	public void startApp() {
		navigationManager.init(() -> {
			initNavBar();
			initNavigation();
			hideLoadingPopup();
		});
	}

	private void initNavBar() {
		// Show the top menu bar
		appHeader.setupMenu(NavTreeDefinitions.NAV_TREE_DEFAULT);
	}

	private void initNavigation() {
		// Set the dashbuilder's default nav tree
		navigationManager.setDefaultNavTree(NavTreeDefinitions.INITIAL_EMPTY);

		// Allow links to core perspectives only under the top menu's nav group
		navigationExplorerScreen.getNavTreeEditor()
				.setOnlyRuntimePerspectives(NavTreeDefinitions.DASHBOARDS_GROUP, true).applyToAllChildren();

		// Disable perspective context setup under the top menu nav's group
		navigationExplorerScreen.getNavTreeEditor()
				.setPerspectiveContextEnabled(NavTreeDefinitions.DASHBOARDS_GROUP, false).applyToAllChildren();
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
