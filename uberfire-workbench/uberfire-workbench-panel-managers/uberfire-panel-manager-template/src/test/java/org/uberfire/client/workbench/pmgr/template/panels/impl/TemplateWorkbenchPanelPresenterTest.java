package org.uberfire.client.workbench.pmgr.template.panels.impl;

import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.client.workbench.PanelManager;
import org.uberfire.client.workbench.pmgr.template.TemplatePanelDefinitionImpl;
import org.uberfire.client.workbench.pmgr.template.panels.impl.TemplateWorkbenchPanelPresenter;
import org.uberfire.client.workbench.pmgr.template.panels.impl.TemplateWorkbenchPanelView;
import org.uberfire.workbench.model.Position;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class TemplateWorkbenchPanelPresenterTest {

    @Test
    public void addPanelTest() {
        TemplateWorkbenchPanelView view = mock( TemplateWorkbenchPanelView.class );
        Widget widget = mock( Widget.class );
        when( view.asWidget() ).thenReturn( widget );
        PanelManager panelManager = mock( PanelManager.class );

        TemplateWorkbenchPanelPresenter template = new TemplateWorkbenchPanelPresenter( view, panelManager, null, null );
        TemplatePanelDefinitionImpl panel = mock( TemplatePanelDefinitionImpl.class );
        template.addPanel( panel, view, Position.EAST );
        verify( panel ).setPerspective( widget );
    }

}
