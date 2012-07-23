package org.drools.guvnor.client.editors.jbpm;

import com.google.gwt.user.client.ui.*;
import org.drools.guvnor.client.annotations.Identifier;
import org.drools.guvnor.client.annotations.WorkbenchPartTitle;
import org.drools.guvnor.client.annotations.WorkbenchPartView;
import org.drools.guvnor.client.annotations.WorkbenchScreen;
import org.jboss.bpm.console.client.model.PluginInfo;
import org.jboss.bpm.console.client.model.ServerStatus;

import javax.enterprise.context.Dependent;

@Dependent
@WorkbenchScreen(identifier = "Server Status")
@Identifier("Server Status")
public class ServerStatusView implements LazyPanel, IsWidget {

    private boolean initialized;

    VerticalPanel layoutPanel;
    HorizontalPanel pluginPanel;

    @WorkbenchPartTitle
    public String getTitle() {
        return "Monitoring perspective";
    }

    @WorkbenchPartView
    public Widget asWidget() {
        layoutPanel = new VerticalPanel();

        HeaderLabel console = new HeaderLabel("Console Info");
        layoutPanel.add(console);

        VerticalPanel layout1 = new VerticalPanel();
        layout1.add(new HTML("Version:"));
        layout1.add(new HTML(Version.VERSION));

        layoutPanel.add(layout1);

        HeaderLabel server = new HeaderLabel("Server Info");
        layoutPanel.add(server);

        HorizontalPanel layout2 = new HorizontalPanel();
        VerticalPanel row1 = new VerticalPanel();
        row1.add(new HTML("Host:"));

        pluginPanel = new HorizontalPanel();
        VerticalPanel row2 = new VerticalPanel();
        row2.add(new Label("Plugins:"));
        row2.add(pluginPanel);
        layout2.add(row1);
        layout2.add(row2);

        layoutPanel.add(layout2);

        update(ServerPlugins.getStatus());

        return layoutPanel;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void initialize() {
        if (!initialized) {
            update(ServerPlugins.getStatus());
            initialized = true;
        }
    }

    private void update(ServerStatus status) {
        pluginPanel.clear();

        Grid g = new Grid(status.getPlugins().size(), 2);
        g.setWidth("100%");

        for (int row = 0; row < status.getPlugins().size(); ++row) {
            PluginInfo p = status.getPlugins().get(row);
            String type = p.getType().substring(
                    p.getType().lastIndexOf(".") + 1, p.getType().length()
            );

            g.setText(row, 0, type);

            final Image img = p.isAvailable() ?
                    new Image("images/icons/confirm_small.png") :
                    new Image("images/icons/deny_small.png");

            g.setWidget(row, 1, img);
        }

        pluginPanel.add(g);
    }

}
