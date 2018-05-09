/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.services.backend.pom;

import java.util.Objects;

public class PluginPresence {

    private boolean isPresent;
    private int position;

    public PluginPresence(boolean isPresent, int position) {
        this.isPresent = isPresent;
        this.position = position;
    }

    public boolean isPresent() {
        return isPresent;
    }

    public int getPosition() {
        return position;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PluginPresence{");
        sb.append("isPresent=").append(isPresent);
        sb.append(", position=").append(position);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (this == o) {
            return true;
        }
        if (!(o instanceof PluginPresence)) {
            return false;
        }
        PluginPresence that = (PluginPresence) o;
        return isPresent == that.isPresent &&
                position == that.position;
    }

    @Override
    public int hashCode() {

        return Objects.hash(isPresent, position);
    }
}
