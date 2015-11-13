/*
 * Copyright 2015 JBoss, by Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.client.views.bs2.splash;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.client.views.bs2.modal.Modal;

import com.github.gwtbootstrap.client.ui.event.HideHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwtmockito.GwtMockitoTestRunner;

@RunWith(GwtMockitoTestRunner.class)
public class SplashViewTest {

    private SplashViewUnitTestWrapper splashViewUnitTestWrapper;

    @Before
    public void setup() {
        splashViewUnitTestWrapper = new SplashViewUnitTestWrapper();
    }


    @Test
      public void setContentDelegationTest() {
        Modal modalPresenter = splashViewUnitTestWrapper.getModal();
        IsWidget mock = mock( IsWidget.class );
        int height = 1;
        splashViewUnitTestWrapper.setContent( mock, height );

        verify( modalPresenter ).add( mock );
        verify( modalPresenter ).add( splashViewUnitTestWrapper.footer );
        verify( modalPresenter ).setBodyHeigth( height );
    }

    @Test
    public void showDelegationTest() {
        Modal modalPresenter = splashViewUnitTestWrapper.getModal();
        splashViewUnitTestWrapper.show();

        verify( modalPresenter ).show();
        verify( modalPresenter ).addHideHandler( any( HideHandler.class ) );
    }

    @Test
    public void hideDelegationTest() {
        Modal modalPresenter = splashViewUnitTestWrapper.getModal();
        splashViewUnitTestWrapper.hide();

        verify( modalPresenter ).hide();
    }



}
