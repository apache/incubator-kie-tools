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
package org.uberfire.security.impl.authz;

import java.util.HashMap;
import java.util.Map;

import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.security.authz.AuthorizationResult;
import org.uberfire.security.authz.Permission;

public class DefaultAuthzResultCache {

    final Map<Permission, Map<String, AuthorizationResult>> internal = new HashMap<>();

    public AuthorizationResult get(final User user,
                                   final Permission permission) {
        Map<String, AuthorizationResult> result = internal.get(permission);
        if (result == null) {
            return null;
        }
        return result.get(user.getIdentifier());
    }

    public void put(final User user,
                    final Permission permission,
                    final AuthorizationResult authzResult) {
        if (!internal.containsKey(permission)) {
            internal.put(permission,
                         new HashMap<>());
        }
        final Map<String, AuthorizationResult> result = internal.get(permission);
        AuthorizationResult knowValue = result.get(user.getIdentifier());
        if (!(result.containsKey(user.getIdentifier()) && authzResult.equals(knowValue))) {
            result.put(user.getIdentifier(),
                       authzResult);
        }
    }

    public int size(User user) {
        int count = 0;
        for (Map<String, AuthorizationResult> userCache : internal.values()) {
            if (userCache.containsKey(user.getIdentifier())) {
                count++;
            }
        }
        return count;
    }

    public void clear() {
        internal.clear();
    }

    public void invalidate(final User user) {
        if (user == null || user.getIdentifier() == null || user.getIdentifier().isEmpty()) {
            return;
        }
        for (Map<String, AuthorizationResult> entry : internal.values()) {
            entry.remove(user.getIdentifier());
        }
    }
}
