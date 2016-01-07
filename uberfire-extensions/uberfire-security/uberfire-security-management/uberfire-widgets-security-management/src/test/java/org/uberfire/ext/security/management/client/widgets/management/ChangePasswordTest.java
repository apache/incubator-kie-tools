/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.security.management.client.widgets.management;

import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorError;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.ext.security.management.client.widgets.management.events.ChangePasswordEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.OnErrorEvent;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ChangePasswordTest extends AbstractSecurityManagementTest {

    @Mock EventSourceMock<ChangePasswordEvent> changePasswordEvent;
    @Mock EventSourceMock<OnErrorEvent> onErrorEvent;
    @Mock ChangePassword.View view;
    private ChangePassword presenter;

    @Before
    public void setup() {
        super.setup();
        presenter = new ChangePassword(userSystemManager, 
                workbenchNotification, onErrorEvent, changePasswordEvent, view);
        assertEquals(view.asWidget(), presenter.asWidget());
    }

    @Test
    public void testClear() throws Exception {
        presenter.clear();
        verify(view, times(1)).clear();
        verify(view, times(0)).show(anyString());
        verify(view, times(0)).hide();
        assertNull(presenter.username);
        assertNull(presenter.callback);
    }
    
    @Test
    public void testShowError() throws Exception {
        String error = "error1";
        presenter.showError(error);
        verify(view, times(0)).clear();
        verify(view, times(0)).show(anyString());
        verify(view, times(0)).hide();
        verify(onErrorEvent, times(1)).fire(any(OnErrorEvent.class));
    }
    
    @Test
    public void testShow() throws Exception {
        presenter.show("user1");
        assertEquals(presenter.username, "user1");
        verify(view, times(1)).clear();
        verify(view, times(1)).show(anyString());
    }

    @Test
    public void testPasswordValidator() throws Exception {
        List<EditorError> errors = presenter.passwordValidator.validate(mock(Editor.class), "password1");
        assertTrue(errors.isEmpty());
        errors = presenter.passwordValidator.validate(mock(Editor.class), "");
        assertFalse(errors.isEmpty());
    }
    
    @Test
    public void testValidatePasswordMatch() throws Exception {
        assertTrue(presenter.validatePasswordsMatch("password1", "password1"));
        assertFalse(presenter.validatePasswordsMatch("password1", "password2"));
    }

    @Test
    public void testUpdatePassword() throws Exception {
        final String newPassw = "new-password";
        Command callback = mock(Command.class);
        ChangePassword.ChangePasswordCallback changePasswordCallback = mock(ChangePassword.ChangePasswordCallback.class);
        presenter.username = "user";
        presenter.callback = changePasswordCallback;
        presenter.onUpdatePassword(newPassw, callback);
        verify(userManagerService, times(1)).changePassword(presenter.username, newPassw);
        verify( changePasswordEvent, times( 1 ) ).fire(any(ChangePasswordEvent.class));
        verify( workbenchNotification, times( 1 ) ).fire(any(NotificationEvent.class));
        verify( callback, times( 1 ) ).execute();
        verify( changePasswordCallback, times( 1 ) ).onPasswordUpdated();
        verify(view, times(1)).hide();
    }
    
}
