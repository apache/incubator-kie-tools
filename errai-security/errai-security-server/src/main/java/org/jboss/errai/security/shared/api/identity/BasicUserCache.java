/*
 * Copyright (C) 2014 Red Hat, Inc. and/or its affiliates.
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

package org.jboss.errai.security.shared.api.identity;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.errai.common.client.api.Assert;
import org.slf4j.Logger;

/**
 * @author Max Barkley <mbarkley@redhat.com>
 */
@Singleton
public class BasicUserCache {

    @Inject
    private Logger logger;
    
    private boolean valid = false;

    private User activeUser = User.ANONYMOUS;

    public User getUser() {
        return activeUser;
    }

    public void setUser(User user) {
        Assert.notNull("User should not be null. Use User.ANONYMOUS instead.", user);
        setActiveUser(user, true);
    }

    @Produces @Dependent
    private User produceActiveUser() {
        return activeUser;
    }

    private void setActiveUser(User user, boolean localStorage) {
        logger.debug("Setting active user: " + String.valueOf(user));
        valid = true;
        activeUser = user;
    }

    public boolean isValid() {
        return valid;
    }

    public void invalidateCache() {
        logger.debug("Invalidating cache.");
        valid = false;
        activeUser = User.ANONYMOUS;
    }

    public boolean hasUser() {
        return !User.ANONYMOUS.equals(activeUser);
    }

}
