package org.uberfire.client.workbench;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.workbench.widgets.dnd.WorkbenchDragAndDropManager;
import org.uberfire.client.workbench.widgets.dnd.WorkbenchPickupDragController;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.impl.PanelDefinitionImpl;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import static org.uberfire.workbench.model.PanelType.ROOT_STATIC;

/**
 * The default layout implementation.
 *
 * @author Heiko Braun
 * @date 05/06/14
 */
@ApplicationScoped
public class WorkbenchLayoutImpl implements WorkbenchLayout {

    private final FlowPanel headers = new FlowPanel();
    private final FlowPanel footers = new FlowPanel();
    private final FlowPanel footer = new FlowPanel();
    private final SimplePanel workbench = new SimplePanel();
    private final FlowPanel container = new FlowPanel();

    @Inject
    private WorkbenchDragAndDropManager dndManager;

    @Inject
    private WorkbenchPickupDragController dragController;

    @Inject
    private PanelManager panelManager;

    private AbsolutePanel workbenchContainer;

    private Composite rootWidget;

    public WorkbenchLayoutImpl()
    {
        // the top level workbench widget
        rootWidget = new Composite() {

            {{
                initWidget(container);
            }}

        };

    }

    @Override
    public <T> void addMargin(Class<T> marginType, IsWidget widget) {
        if(Header.class == marginType)
        {
            headers.add(widget);
        }
        else if (Footer.class == marginType)
        {
            footers.add(widget);
        }
        else
        {
            throw new IllegalArgumentException("Unsupported margin type: "+ marginType);
        }
    }

    @Override
    public IsWidget getRoot() {
        return rootWidget;
    }

    public HasWidgets getPerspectiveContainer() {
        return workbench;
    }

    @Override
    public void onBootstrap() {

        container.add(headers);

        // drag and drop boundary
        workbenchContainer = dragController.getBoundaryPanel();
        workbenchContainer.add(workbench);
        container.add(workbenchContainer);

        container.add(footers);

        // clear reset
        workbench.clear();
        dndManager.unregisterDropControllers();

        // connection between workbench layout and panel manager
        final PanelDefinition root = new PanelDefinitionImpl( ROOT_STATIC );
        panelManager.setRoot( root );

        // set default top perspective widget
        workbench.setWidget(panelManager.getPanelView(root));

    }

    @Override
    public void onResize() {
        final int width = Window.getClientWidth();
        final int height = Window.getClientHeight();
        doResizeWorkbenchContainer( width, height );
    }

    @Override
    public void resizeTo(int width, int height) {
        doResizeWorkbenchContainer( width, height );
    }

    private void doResizeWorkbenchContainer( final int width,
                                             final int height ) {
        final int headersHeight = headers.asWidget().getOffsetHeight();
        final int footersHeight = footers.asWidget().getOffsetHeight();
        final int availableHeight;

        availableHeight = height - headersHeight - footersHeight;

        workbenchContainer.setPixelSize(width, availableHeight);
        workbench.setPixelSize( width, availableHeight );

        final Widget w = workbench.getWidget();
        if ( w != null ) {
            if ( w instanceof RequiresResize ) {
                ( (RequiresResize) w ).onResize();
            }
        }
    }
}
