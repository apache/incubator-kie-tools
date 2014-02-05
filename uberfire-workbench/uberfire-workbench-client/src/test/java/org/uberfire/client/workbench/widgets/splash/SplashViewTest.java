package org.uberfire.client.workbench.widgets.splash;

import com.github.gwtbootstrap.client.ui.event.HideHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.client.workbench.widgets.common.Modal;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class SplashViewTest {

    private SplashViewUnitTestWrapper splashViewUnitTestWrapper;

    @Before
    public void setup() {
        splashViewUnitTestWrapper = new SplashViewUnitTestWrapper();
    }


    @Test
      public void setContentDelegationTest() {
        Modal modal = splashViewUnitTestWrapper.getModal();
        IsWidget mock = mock( IsWidget.class );
        int height = 1;
        splashViewUnitTestWrapper.setContent( mock, height );

        verify( modal ).add( mock );
        verify( modal ).add( splashViewUnitTestWrapper.footer );
        verify( modal ).setBodyHeigth( height );
    }

    @Test
    public void showDelegationTest() {
        Modal modal = splashViewUnitTestWrapper.getModal();
        splashViewUnitTestWrapper.show();

        verify( modal ).show();
        verify( modal ).addHideHandler( any( HideHandler.class ) );
    }

    @Test
    public void hideDelegationTest() {
        Modal modal = splashViewUnitTestWrapper.getModal();
        splashViewUnitTestWrapper.hide();

        verify( modal ).hide();
    }



}
