/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.model;

import java.util.List;

public class Assignee {

    private String name;

    private String customName;

    public Assignee() {
    }

    public Assignee(String name) {
        this.name = name;
    }

    public Assignee(final AssigneeRow row) {
        this.name = row.getName();
        this.customName = row.getCustomName();
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getCustomName() {
        return customName;
    }

    public void setCustomName(final String customName) {
        this.customName = customName;
    }

    public String toString() {
        if (customName != null && !customName.isEmpty()) {
            return customName;
        } else if (name != null && !name.isEmpty()) {
            return name;
        } else {
            return null;
        }
    }

    /**
     * Deserializes an assignee
     * @param s
     * @return
     */
    public static Assignee deserialize(final String s,
                                       final List<String> names) {
        Assignee a = new Assignee();
        if (names != null && names.contains(s)) {
            a.setName(s);
        } else {
            a.setCustomName(s);
        }
        return a;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Assignee)) {
            return false;
        }
        Assignee assignee = (Assignee) o;
        if (getName() != null ? !getName().equals(assignee.getName()) : assignee.getName() != null) {
            return false;
        }
        return getCustomName() != null ? getCustomName().equals(assignee.getCustomName()) : assignee.getCustomName() == null;
    }

    @Override
    public int hashCode() {
        int result = getName() != null ? getName().hashCode() : 0;
        result = 31 * result + (getCustomName() != null ? getCustomName().hashCode() : 0);
        return result;
    }
}
