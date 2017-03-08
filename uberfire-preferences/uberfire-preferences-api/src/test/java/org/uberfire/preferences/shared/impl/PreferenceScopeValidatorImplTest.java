/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.preferences.shared.impl;

import org.junit.Before;
import org.junit.Test;
import org.uberfire.mocks.SessionInfoMock;
import org.uberfire.preferences.shared.PreferenceScopeFactory;
import org.uberfire.preferences.shared.impl.exception.InvalidPreferenceScopeException;

import static org.junit.Assert.*;
import static org.uberfire.preferences.shared.impl.DefaultPreferenceScopesForTests.userEntireApplicationScope;

public class PreferenceScopeValidatorImplTest {

    private PreferenceScopeValidatorImpl validator;

    @Before
    public void setup() {
        final SessionInfoMock sessionInfo = new SessionInfoMock("my-user");
        final DefaultPreferenceScopeTypes scopeTypes = new DefaultPreferenceScopeTypes(new UsernameProviderMock(sessionInfo));
        final PreferenceScopeFactory scopeFactory = new PreferenceScopeFactoryImpl(scopeTypes);
        final DefaultPreferenceScopeResolutionStrategy scopeResolutionStrategy = new DefaultPreferenceScopeResolutionStrategy(scopeFactory,
                                                                                                                              null);

        validator = new PreferenceScopeValidatorImpl(scopeTypes,
                                                     scopeResolutionStrategy);
    }

    @Test(expected = InvalidPreferenceScopeException.class)
    public void validateNullScopeTest() {
        validator.validate(null);
    }

    @Test(expected = InvalidPreferenceScopeException.class)
    public void validateScopeWithInvalidTypeTest() {
        validator.validate(new PreferenceScopeImpl("invalidType",
                                                   null,
                                                   null));
    }

    @Test(expected = InvalidPreferenceScopeException.class)
    public void validateScopeWithTypeThatRequiresKeyWithoutKeyTest() {
        validator.validate(new PreferenceScopeImpl(DefaultScopes.COMPONENT.type(),
                                                   null,
                                                   null));
    }

    @Test(expected = InvalidPreferenceScopeException.class)
    public void validateScopeWithValidScopeButNotInTheOrderTest() {
        validator.validate(new PreferenceScopeImpl(DefaultScopes.USER.type(),
                                                   null,
                                                   null));
    }

    @Test
    public void validateValidScopeTest() {
        validator.validate(userEntireApplicationScope);
    }

    @Test
    public void isEmptyTest() {
        assertTrue(validator.isEmpty(null));
        assertTrue(validator.isEmpty(""));
        assertTrue(validator.isEmpty("  "));
        assertFalse(validator.isEmpty("anyString"));
    }
}
