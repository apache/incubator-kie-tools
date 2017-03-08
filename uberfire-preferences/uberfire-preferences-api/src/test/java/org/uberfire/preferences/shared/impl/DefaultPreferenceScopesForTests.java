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

import java.util.Arrays;
import java.util.List;

import org.uberfire.preferences.shared.PreferenceScope;

public class DefaultPreferenceScopesForTests {

    public static final String allUsersScopeType = DefaultScopes.ALL_USERS.type();
    public static final String entireApplicationScopeType = DefaultScopes.ENTIRE_APPLICATION.type();
    public static final String componentScopeType = DefaultScopes.COMPONENT.type();
    public static final String userScopeType = DefaultScopes.USER.type();

    public static final String allUsersScopeKey = allUsersScopeType;
    public static final String entireApplicationScopeKey = entireApplicationScopeType;
    public static final String componentScopeKey = "my-component";
    public static final String userScopeKey = "my-user";

    public static final PreferenceScopeImpl allUsersScope = new PreferenceScopeImpl(allUsersScopeType,
                                                                                    allUsersScopeKey,
                                                                                    null);
    public static final PreferenceScopeImpl entireApplicationScope = new PreferenceScopeImpl(entireApplicationScopeType,
                                                                                             entireApplicationScopeKey,
                                                                                             null);
    public static final PreferenceScopeImpl componentScope = new PreferenceScopeImpl(componentScopeType,
                                                                                     componentScopeKey,
                                                                                     null);
    public static final PreferenceScopeImpl userScope = new PreferenceScopeImpl(userScopeType,
                                                                                userScopeKey,
                                                                                null);

    public static final PreferenceScopeImpl userComponentScope = new PreferenceScopeImpl(userScopeType,
                                                                                         userScopeKey,
                                                                                         componentScope);
    public static final PreferenceScopeImpl userEntireApplicationScope = new PreferenceScopeImpl(userScopeType,
                                                                                                 userScopeKey,
                                                                                                 entireApplicationScope);
    public static final PreferenceScopeImpl allUsersComponentScope = new PreferenceScopeImpl(allUsersScopeType,
                                                                                             allUsersScopeKey,
                                                                                             componentScope);
    public static final PreferenceScopeImpl allUsersEntireApplicationScope = new PreferenceScopeImpl(allUsersScopeType,
                                                                                                     allUsersScopeKey,
                                                                                                     entireApplicationScope);

    public static final List<PreferenceScope> defaultOrder = Arrays.asList(userComponentScope,
                                                                           userEntireApplicationScope,
                                                                           allUsersComponentScope,
                                                                           allUsersEntireApplicationScope);
}
