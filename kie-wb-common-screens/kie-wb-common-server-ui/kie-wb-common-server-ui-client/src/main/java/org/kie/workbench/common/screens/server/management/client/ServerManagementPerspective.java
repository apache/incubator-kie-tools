package org.kie.workbench.common.screens.server.management.client;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.FlowPanel;
import org.uberfire.client.annotations.WorkbenchPanel;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.util.Layouts;

@ApplicationScoped
@WorkbenchPerspective(identifier = "ServerManagementPerspective")
public class ServerManagementPerspective extends FlowPanel {

    @Inject
    @WorkbenchPanel(parts = "ServerManagementBrowser")
    FlowPanel browser;

    @PostConstruct
    void doLayout() {
        Layouts.setToFillParent( browser );
        add( browser );
    }
}
