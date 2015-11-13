package org.kie.workbench.common.screens.explorer.client.widgets.branches;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.mock;

import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.DropDownMenu;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.explorer.client.widgets.branches.BranchSelectorViewImpl;
import org.mockito.Spy;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;

@RunWith(GwtMockitoTestRunner.class)
public class BranchSelectorViewTest {

    @Spy
    private BranchSelectorViewImpl view;

    @GwtMock
    private Button button;

    @GwtMock
    private DropDownMenu menu;

    @Test
    // This serves as a regression test for BZ1274349
    public void clear() {
        view.clear();
        verify( menu, times( 1 ) ).clear();
        verify( button, never() ).clear();
    }

    @Test
    public void addBranch() {
        doReturn( mock(AnchorListItem.class) ).when( view ).getAnchorListItem( any( String.class ) );

        view.addBranch( "branch1" );
        view.addBranch( "branch2" );
        verify( menu, times( 2 ) ).add( any( AnchorListItem.class ) );
    }
}
