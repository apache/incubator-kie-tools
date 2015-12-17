/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.shared.kmodule;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public enum AssertBehaviorOption {

    EQUALITY("equality"), IDENTITY("identity");

    private final String string;

    AssertBehaviorOption(String mode) {
        this.string = mode;
    }

    @Override
    public String toString() {
        return string;
    }

    public static AssertBehaviorOption determineAssertBehaviorMode(String equalsBehavior) {
        if (EQUALITY.getMode().equalsIgnoreCase(equalsBehavior)) {
            return EQUALITY;
        } else if (IDENTITY.getMode().equalsIgnoreCase(equalsBehavior)) {
            return IDENTITY;
        }
        throw new IllegalArgumentException("Illegal enum value '" + equalsBehavior + "' for AssertBehaviourOption");
    }

    public String getMode() {
        return string;
    }
}
