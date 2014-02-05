package org.uberfire.client.workbench.widgets.tab;

import com.github.gwtbootstrap.client.ui.DropdownTab;
import com.github.gwtbootstrap.client.ui.Tab;
import com.github.gwtbootstrap.client.ui.TabLink;
import com.github.gwtbootstrap.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.client.workbench.widgets.dnd.WorkbenchDragAndDropManager;

import static org.mockito.Mockito.*;

public class UberTabPanelUnitTestWrapper extends UberTabPanel {

    TabPanel tabPanelSpy;
    private boolean lastTabIsDropdownTab;

    Tab createTab( final WorkbenchPartPresenter.View view,
                   final boolean isActive ) {
        Tab mock = mock( Tab.class );
        when( mock.asTabLink() ).thenReturn( mock( TabLink.class ) );
        return mock;
    }

    public void setupMocks( WorkbenchDragAndDropManager dndManager ) {
        this.dndManager = dndManager;
        this.tabPanelSpy = spy(tabPanel);
        this.tabPanel = tabPanelSpy;
        this.lastTabIsDropdownTab = false;
    }

    @Override
    Widget getLastTab() {
        return mock( DropdownTab.class );
    }

    @Override
    boolean isFirstWidget() {
        return false;
    }

    public void setLastTabIsDropdownTab(boolean lastTabIsDropdownTab ) {
        this.lastTabIsDropdownTab = lastTabIsDropdownTab;
    }

    @Override
    boolean lastTabIsDropdownTab( Widget lastTab ) {
        return lastTabIsDropdownTab;
    }

    @Override
    Tab cloneTab( final TabLink tabLink,
                  final boolean fromDropdown,
                  final boolean toDropdown ) {
        return mock(Tab.class);
    }

    @Override
    DropdownTab cloneDropdown( final DropdownTab original,
                               final int excludedIndex ) {
        return mock(DropdownTab.class);
    }
}
