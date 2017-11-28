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
package org.dashbuilder.navigation;

import java.util.Collection;

import org.dashbuilder.navigation.impl.NavItemContextImpl;

/**
 * An interface for manipulating a {@link NavItem#getContext()} as it was a collection of attribute/value pairs.
 */
public interface NavItemContext {

    static NavItemContext create() {
        return new NavItemContextImpl();
    }

    static NavItemContext get(String ctx) {
        return new NavItemContextImpl(ctx);
    }

    static NavItemContext get(NavItem navItem) {
        return new NavItemContextImpl(navItem.getContext());
    }

    void init(String ctx);

    Collection<String> getPropertyIds();

    String getProperty(String id);

    String removeProperty(String id);

    NavItemContext setProperty(String id, String value);

    /**
     * Check if this context contains all property-value pairs of some other context.
     *
     * @param cxt
     * @return true iff all the properties of ctx are also present in this context and have the same value.
     */
    boolean includesPropertiesOf(NavItemContext cxt);
}
