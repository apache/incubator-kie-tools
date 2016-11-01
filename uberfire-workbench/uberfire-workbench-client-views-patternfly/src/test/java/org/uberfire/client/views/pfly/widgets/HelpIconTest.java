package org.uberfire.client.views.pfly.widgets;

import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.gwtbootstrap3.client.ui.Icon;
import org.gwtbootstrap3.client.ui.Popover;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class HelpIconTest {

    @Mock
    private Icon icon;

    @Mock
    private SimplePanel panel;

    @Mock
    private Popover popover;

    private HelpIcon helpIcon;

    @Before
    public void setUp() {
        this.helpIcon = new HelpIcon( icon, panel, popover );
    }

    @Test
    public void setHelpContent() {
        helpIcon.setHelpContent( "testContent" );

        verify( popover ).setContent( "testContent" );
    }

    @Test
    public void setHelpTitle() {
        helpIcon.setHelpTitle( "testTitle" );

        verify( popover ).setTitle( "testTitle" );
    }

}
