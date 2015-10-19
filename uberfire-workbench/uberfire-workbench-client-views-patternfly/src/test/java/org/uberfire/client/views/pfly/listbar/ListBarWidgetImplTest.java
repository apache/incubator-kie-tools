package org.uberfire.client.views.pfly.listbar;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.client.workbench.PanelManager;
import org.uberfire.commons.data.Pair;
import org.uberfire.workbench.model.PartDefinition;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith( GwtMockitoTestRunner.class )
public class ListBarWidgetImplTest {

    ListBarWidgetImpl listBar;

    @Before
    public void setUp() throws Exception {
        listBar = new ListBarWidgetImpl() {
            @Override
            void setupContextMenu() {
            }
        };
    }

    @Test
    public void onSelectPartOnPartHiddenEventIsFired() {

        final PartDefinition selectedPart = mock( PartDefinition.class );
        final PartDefinition currentPart = mock( PartDefinition.class );

        listBar.panelManager = mock( PanelManager.class );
        listBar.partContentView.put( selectedPart, new FlowPanel() );
        listBar.parts.add( selectedPart );
        listBar.currentPart = Pair.newPair( currentPart, new FlowPanel() );
        listBar.partContentView.put( currentPart, new FlowPanel() );
        listBar.titleDropDown = mock( PartListDropdown.class );

        listBar.selectPart( selectedPart );

        verify( listBar.panelManager ).onPartHidden( currentPart );

    }


}