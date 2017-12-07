/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.security.management.client;

import java.util.function.Consumer;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.ext.security.management.api.Capability;
import org.uberfire.ext.security.management.api.exception.EntityNotFoundException;
import org.uberfire.ext.security.management.api.exception.GroupNotFoundException;
import org.uberfire.ext.security.management.api.exception.NoImplementationAvailableException;
import org.uberfire.ext.security.management.api.exception.RealmManagementNotAuthorizedException;
import org.uberfire.ext.security.management.api.exception.SecurityManagementException;
import org.uberfire.ext.security.management.api.exception.UnsupportedServiceCapabilityException;
import org.uberfire.ext.security.management.api.exception.UserAlreadyExistsException;
import org.uberfire.ext.security.management.api.exception.UserNotFoundException;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.contains;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class ClientSecurityExceptionMessageResolverTest {

    private ClientSecurityExceptionMessageResolver tested;

    @Before
    public void setup() {
        this.tested = new ClientSecurityExceptionMessageResolver();
        this.tested.registerMessageResolvers();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testConsumeSecurityExceptions() {
        final Consumer<String> c0 = mock(Consumer.class);
        tested.consumeExceptionMessage(new SecurityManagementException("custom"),
                                       c0);
        verify(c0,
               times(1)).accept(eq("custom"));
        final Consumer<String> c1 = mock(Consumer.class);
        tested.consumeExceptionMessage(new EntityNotFoundException("id1"),
                                       c1);
        verify(c1,
               times(1)).accept(contains("id1"));
        final Consumer<String> c2 = mock(Consumer.class);
        tested.consumeExceptionMessage(new UserNotFoundException("user1"),
                                       c2);
        verify(c2,
               times(1)).accept(contains("user1"));
        final Consumer<String> c3 = mock(Consumer.class);
        tested.consumeExceptionMessage(new GroupNotFoundException("group1"),
                                       c3);
        verify(c3,
               times(1)).accept(contains("group1"));
        final Consumer<String> c4 = mock(Consumer.class);
        tested.consumeExceptionMessage(new UnsupportedServiceCapabilityException(Capability.CAN_ADD_USER),
                                       c4);
        verify(c4,
               times(1)).accept(contains(Capability.CAN_ADD_USER.name()));
        final Consumer<String> c5 = mock(Consumer.class);
        tested.consumeExceptionMessage(new UserAlreadyExistsException("aUser"),
                                       c5);
        verify(c5,
               times(1)).accept(contains("aUser"));

        final Consumer<String> c6 = mock(Consumer.class);
        tested.consumeExceptionMessage(new RealmManagementNotAuthorizedException("aRealm"),
                                       c6);
        verify(c6,
               times(1)).accept(contains("aRealm"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSkipSecurityExceptions() {
        final Consumer<String> c1 = mock(Consumer.class);
        tested.consumeExceptionMessage(new NoImplementationAvailableException(),
                                       c1);
        verify(c1,
               never()).accept(anyString());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testConsumeExceptions() {
        final Consumer<String> c1 = mock(Consumer.class);
        tested.consumeExceptionMessage(new RuntimeException("anErrorMessage"),
                                       c1);
        verify(c1,
               times(1)).accept(eq("anErrorMessage"));
        final Consumer<String> c2 = mock(Consumer.class);
        tested.consumeExceptionMessage(new RuntimeException(new RuntimeException("rootMessage")),
                                       c2);
        verify(c2,
               times(1)).accept(eq("rootMessage"));
    }
}
