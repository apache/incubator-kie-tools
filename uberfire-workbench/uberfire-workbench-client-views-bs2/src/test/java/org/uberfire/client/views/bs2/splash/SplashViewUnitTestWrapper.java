package org.uberfire.client.views.bs2.splash;

import static org.mockito.Mockito.*;

import org.uberfire.client.views.bs2.modal.Modal;

public class SplashViewUnitTestWrapper extends SplashViewImpl {

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
