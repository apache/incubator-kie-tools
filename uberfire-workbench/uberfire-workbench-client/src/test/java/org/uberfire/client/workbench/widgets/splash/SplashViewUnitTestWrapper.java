package org.uberfire.client.workbench.widgets.splash;


import org.uberfire.client.workbench.widgets.common.Modal;

import static org.mockito.Mockito.*;

public class SplashViewUnitTestWrapper extends SplashView {

    private Modal mock;

    @Override
    Modal getModal() {
        if ( mock == null ) {
            mock = mock( Modal.class );
        }
        return mock;
    }

    void cleanup() {
    }

}
