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

package org.kie.workbench.common.services.shared.preferences.config;

import java.util.ArrayList;
import java.util.List;

import org.guvnor.common.services.shared.preferences.GuvnorPreferenceScopes;

public class WorkbenchPreferenceScopes {

    public static final String GLOBAL = GuvnorPreferenceScopes.GLOBAL;
    public static final String USER = GuvnorPreferenceScopes.USER;
    public static final String PROJECT = GuvnorPreferenceScopes.PROJECT;
    public static final String SPACE = GuvnorPreferenceScopes.SPACE;

    /**
     * Must include all scopes that are defined separately for each user.
     * For instance, when a user changes a project preference, it will change only for that user.
     * @return All scopes that are defined separately for each user. It will never be null.
     */
    public static List<String> getUserScopedScopes() {
        List<String> userScopedScopes = new ArrayList<>();

        userScopedScopes.add(PROJECT);
        userScopedScopes.add(SPACE);

        return userScopedScopes;
    }
}
