package org.uberfire.client.workbench.panels.impl;

import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.workbench.widgets.split.WorkbenchSplitLayoutPanel;
import org.uberfire.workbench.model.PanelDefinition;

public class HorizontalSplitterPanelUnitTestWrapper extends  HorizontalSplitterPanel {

    private WorkbenchSplitLayoutPanel slpMock;

    public void setupMocks( WorkbenchSplitLayoutPanel slp,
                            SimpleLayoutPanel eastWidgetContainer,
                            SimpleLayoutPanel westWidgetContainer ){
       this.slpMock= slp;
       this.eastWidgetContainer = eastWidgetContainer;
       this.westWidgetContainer = westWidgetContainer;
    }

    WorkbenchSplitLayoutPanel getSlp() {
        return slpMock;
    }
    @Override
    int getChildSize( final PanelDefinition panel ) {
        return 10;
    }

    protected void initWidget(Widget widget) {
    }
}
