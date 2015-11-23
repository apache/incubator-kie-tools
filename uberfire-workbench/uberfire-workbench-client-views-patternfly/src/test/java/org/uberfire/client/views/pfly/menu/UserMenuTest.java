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

package org.uberfire.client.views.pfly.menu;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.security.authz.AuthorizationManager;

import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class UserMenuTest {

    @Mock
    private UserMenu.UserMenuView userMenuView;

    @Mock
    private User user;

    @Mock
    private AuthorizationManager authzManager;

    @InjectMocks
    private UserMenu userMenu;

    @Test
    public void testUserNameWithId(){
        final String userName = "mock";
        when( user.getIdentifier() ).thenReturn( userName );
        userMenu.setup();
        verify( userMenuView ).setUserName( userName );
    }

    @Test
    public void testUserNameUsingFirstAndLastName(){
        final String firstName = "Mock";
        final String lastName = "Test";
        when( user.getProperty( User.StandardUserProperties.FIRST_NAME ) ).thenReturn( firstName );
        when( user.getProperty( User.StandardUserProperties.LAST_NAME ) ).thenReturn( lastName );
        userMenu.setup();
        verify( userMenuView ).setUserName( firstName + " " + lastName );
    }

    @Test
    public void testUserNameUsingFirstName(){
        final String firstName = "Mock";
        when( user.getProperty( User.StandardUserProperties.FIRST_NAME ) ).thenReturn( firstName );
        userMenu.setup();
        verify( userMenuView ).setUserName( firstName );
    }

    @Test
    public void testUserNameUsingLastName(){
        final String lastName = "Test";
        when( user.getProperty( User.StandardUserProperties.LAST_NAME ) ).thenReturn( lastName );
        userMenu.setup();
        verify( userMenuView ).setUserName( lastName );
    }
}
