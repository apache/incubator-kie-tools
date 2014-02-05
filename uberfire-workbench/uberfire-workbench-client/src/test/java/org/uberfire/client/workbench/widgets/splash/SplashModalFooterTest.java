package org.uberfire.client.workbench.widgets.splash;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.mvp.ParameterizedCommand;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class SplashModalFooterTest {

    private SplashModalFooter splashModalFooter;
    @GwtMock
    private ParameterizedCommand<Boolean> closeCommand;

    @Before
    public void setup() {
        splashModalFooter = new SplashModalFooter( closeCommand );
    }

    @Test
    public void onOKButtonClickTest() {
        ClickEvent e = mock(ClickEvent.class);
        splashModalFooter.onOKButtonClick( e );
        verify(closeCommand).execute( !(splashModalFooter.dontShowAgain.getValue()) );

    }



}
