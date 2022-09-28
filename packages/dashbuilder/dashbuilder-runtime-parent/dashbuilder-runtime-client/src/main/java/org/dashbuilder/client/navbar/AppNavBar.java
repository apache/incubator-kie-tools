/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package org.dashbuilder.client.navbar;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.user.client.ui.HeaderPanel;
import org.dashbuilder.client.RuntimeClientLoader;
import org.uberfire.client.workbench.WorkbenchLayout;

@ApplicationScoped
public class AppNavBar {

    @Inject
    WorkbenchLayout wbLayout;

    @Inject
    RuntimeClientLoader loader;

    public void setHide(boolean hide) {
        var _hide = loader.isHideNavBar() || hide;
        var header = (HeaderPanel) wbLayout.getRoot();
        var headerParent =
                header.getHeaderWidget()
                        .asWidget()
                        .getElement()
                        .getParentElement();
        headerParent.getStyle()
                .setDisplay(_hide ? Display.NONE : Display.BLOCK);
        // workaround for header still showing a white space
        headerParent.getStyle()
                .setProperty("min-height", _hide ? "0px" : "20px");
    }

}
