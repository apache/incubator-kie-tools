/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.client.navbar;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;
import org.dashbuilder.client.navigation.widget.NavMenuBarWidget;
import org.dashbuilder.client.resources.i18n.AppConstants;
import org.dashbuilder.navigation.NavItem;
import org.dashbuilder.navigation.NavTree;
import org.dashbuilder.navigation.workbench.NavWorkbenchCtx;
import org.gwtbootstrap3.client.ui.Label;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Node;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.common.client.dom.UnorderedList;
import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.workbench.Header;
import org.uberfire.client.workbench.Workbench;
import org.uberfire.client.workbench.events.PerspectiveChange;
import org.uberfire.mvp.Command;

@Templated
@ApplicationScoped
public class TopMenuBar implements Header {

    @Inject
    @DataField
    Div navBar;

    @Inject
    @DataField
    Div navHeader;

    @Inject
    @DataField
    Div navTreeMenuBar;

    @Inject
    @DataField
    Span userNameSpan;

    @Inject
    @DataField
    UnorderedList roleList;

    NavMenuBarWidget menuBarWidget;
    User user;
    Command onItemSelectedCommand;
    Command onLogoutCommand;
    Workbench workbench;
    NavTree navTree;
    String currentPerspectiveId;

    public TopMenuBar() {
    }

    @Inject
    public TopMenuBar(NavMenuBarWidget menuBarWidget, User user, Workbench workbench) {
        this.menuBarWidget = menuBarWidget;
        this.user = user;
        this.workbench = workbench;
    }

    @PostConstruct
    private void init() {
        menuBarWidget.setSecure(true);
        currentPerspectiveId = workbench.getHomePerspectiveActivity().getIdentifier();

        setNavHeaderHtml("banner/banner.html");
        setUserName(user.getIdentifier());
        navTreeMenuBar.appendChild((Node) menuBarWidget.asWidget().getElement());

        clearRoles();
        for (Role role : user.getRoles()) {
            if (!role.getName().equals("IS_REMEMBER_ME")) {
                addRole(role.getName());
            }
        }
    }

    @Override
    public String getId() {
        return "TopMenuBar";
    }

    @Override
    public int getOrder() {
        return 2;
    }

    public NavItem getItemSelected() {
        return menuBarWidget.getItemSelected();
    }

    public void setOnItemSelectedCommand(Command command) {
        this.onItemSelectedCommand = command;
        menuBarWidget.setOnItemSelectedCommand(onItemSelectedCommand);
    }

    public void setOnLogoutCommand(Command command) {
        this.onLogoutCommand = command;
        if (onLogoutCommand != null) {
            addLogout();
        }
    }

    public void show(String navItemId) {
        // Show the target subtree from the global nav tree
        navTree = menuBarWidget.getNavigationManager().getNavTree().getItemAsTree(navItemId);
        menuBarWidget.setNavHeaderVisible(false);
        menuBarWidget.setOnStaleCommand(() -> show(navItemId));
        menuBarWidget.show(navTree);

        // Select the menu entry that points to the current perspective
        setSelectedItem(currentPerspectiveId);
    }

    public void setSelectedItem(String perspectiveId) {
        if (navTree != null) {
            List<NavItem> navItems = navTree.searchItems(NavWorkbenchCtx.perspective(perspectiveId));
            if (!navItems.isEmpty()) {
                menuBarWidget.setSelectedItem(navItems.get(0).getId());
            }
        }
    }

    public void clearSelectedItem() {
        menuBarWidget.clearSelectedItem();
    }

    // View logic

    public void setNavHeaderHtml(String htmlFile) {
        DOMUtil.removeAllChildren(navHeader);

        RequestBuilder rb = new RequestBuilder(RequestBuilder.GET, htmlFile);
        rb.setCallback(new RequestCallback() {

            public void onResponseReceived(Request request, Response response) {
                HTMLPanel html = new HTMLPanel(response.getText());
                navHeader.appendChild((Node) html.getElement());
            }

            public void onError(Request request, Throwable exception) {
                Label label = new Label(AppConstants.INSTANCE.logoBannerError());
                navHeader.appendChild((Node) label.getElement());
            }
        });

        try {
            rb.send();
        } catch ( RequestException re ) {
            Label label = new Label(AppConstants.INSTANCE.logoBannerError());
            navHeader.appendChild((Node) label.getElement());
        }
    }

    public void setUserName(String name) {
        userNameSpan.setTextContent(name);
    }

    public void clearRoles() {
        DOMUtil.removeAllChildren(roleList);
    }

    public void addRole(String role) {
        this.addEntry(role, false);
    }

    public void addLogout() {
        this.addEntry(AppConstants.INSTANCE.logOut(), true);
    }

    private void addEntry(String entry, boolean logout) {
        AnchorElement anchor = Document.get().createAnchorElement();
        anchor.setInnerText(entry);

        Event.sinkEvents(anchor, Event.ONCLICK);
        Event.setEventListener(anchor, event -> {
            if (Event.ONCLICK == event.getTypeInt()) {
                if (!logout) {
                    onRoleClicked(entry);
                } else {
                    onLogoutClicked();
                }
            }
        });

        LIElement li = Document.get().createLIElement();
        li.getStyle().setCursor(Style.Cursor.POINTER);
        li.appendChild(anchor);
        roleList.appendChild((Node) li);
    }

    // View actions

    public void onRoleClicked(String role) {
        // Do nothing
    }

    public void onLogoutClicked() {
        if (onLogoutCommand != null) {
            onLogoutCommand.execute();
        }
    }

    // Make sure the menu bar reacts to changes on the current perspective

    private void onCurrentPerspectiveChanged(@Observes final PerspectiveChange event) {
        currentPerspectiveId = event.getIdentifier();
        setSelectedItem(currentPerspectiveId);
    }
}
