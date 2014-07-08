package org.uberfire.client.workbench.widgets.listbar;

import java.util.LinkedHashSet;

import com.github.gwtbootstrap.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import org.uberfire.client.workbench.PanelManager;
import org.uberfire.client.workbench.panels.WorkbenchPanelPresenter;
import org.uberfire.commons.data.Pair;
import org.uberfire.workbench.model.PartDefinition;

public class ListBarWidgetUnitTestWrapper extends ListBarWidget {

    public ListBarWidgetUnitTestWrapper() {
    }

    ;

    public ListBarWidgetUnitTestWrapper setupMocks( FlowPanel menuArea,
                                                    Button closeButton,
                                                    Pair<PartDefinition, FlowPanel> currentPart,
                                                    WorkbenchPanelPresenter presenter,
                                                    PanelManager panelManager,
                                                    FocusPanel container,
                                                    Button contextDisplay,
                                                    FlowPanel contextMenu,
                                                    SimplePanel title,
                                                    FlowPanel content,
                                                    LinkedHashSet<PartDefinition> parts
                                                  ) {
        this.menuArea = menuArea;
        this.closeButton = closeButton;
        this.currentPart = currentPart;
        this.presenter = presenter;
        this.panelManager = panelManager;
        this.container = container;
        this.contextDisplay = contextDisplay;
        this.contextMenu = contextMenu;
        this.title = title;
        this.content = content;
        this.parts = parts;
        return this;
    }

    public boolean isMultiPart() {
        return isMultiPart;
    }

    public boolean isDndEnabled() {
        return isDndEnabled;
    }

    boolean isPropertyListbarContextDisable() {
        return true;
    }

    boolean isCustomListNull(){
        return partChooserList==null;
    }

}
