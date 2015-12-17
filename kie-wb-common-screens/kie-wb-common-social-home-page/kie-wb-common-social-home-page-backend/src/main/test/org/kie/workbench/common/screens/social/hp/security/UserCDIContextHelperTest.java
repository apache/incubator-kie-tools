/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.screens.social.hp.security;

import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.enterprise.context.ContextNotActiveException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith( MockitoJUnitRunner.class )
public class UserCDIContextHelperTest {

    @Mock
    User identify;

    @InjectMocks
    UserCDIContextHelper helper;

    @Test
    public void getUserTest() {
        assertEquals( identify, helper.getUser() );
    }

    @Test
    public void thereIsALoggedUserIsScope() {
        assertTrue( helper.thereIsALoggedUserInScope() );
    }

    @Test
    public void thereIsntALoggedUserIsScope() {
        when( identify.getIdentifier() ).thenThrow( ContextNotActiveException.class );

        assertFalse( helper.thereIsALoggedUserInScope() );
    }

    @Test
    public void thereIsntALoggedUserIsScopeWithoutCDI() {
        UserCDIContextHelper helperWithoutCdi = new UserCDIContextHelper();

        assertFalse( helperWithoutCdi.thereIsALoggedUserInScope() );
    }

}