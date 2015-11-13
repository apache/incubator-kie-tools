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
package org.uberfire.client.resources;

import com.google.gwt.resources.client.CssResource;

/**
 * GWT managed CSS for Workbench
 */
public interface WorkbenchCss
        extends
        CssResource {

    @ClassName( "uf-drop-target-highlight" )
    String dropTargetHighlight();

    @ClassName( "uf-drop-target-compass" )
    String dropTargetCompass();

    String notification();

    String toolbar();

    String statusBar();

    @ClassName("uf-activeNavTabs")
    String activeNavTabs();

    String tabCloseButton();

    @ClassName("uf-listbar")
    String listbar();

    @ClassName("show-context")
    String showContext();

    @ClassName("uf-split-layout-panel")
    String splitLayoutPanel();

    @ClassName("uf-split-layout-panel-hdragger")
    String splitLayoutPanelHDragger();

    @ClassName("uf-split-layout-panel-vdragger")
    String splitLayoutPanelVDragger();

    @ClassName("uf-modal")
    String modal();
}
