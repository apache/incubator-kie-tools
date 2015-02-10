/*
 * Copyright 2015 JBoss Inc
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
package org.uberfire.ext.wires.bpmn.client.rules.impl;

import org.uberfire.ext.wires.bpmn.api.model.Role;

public class NotPermittedRole implements Role {

    private static final String name = "notPermittedRole";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof NotPermittedRole ) ) {
            return false;
        }

        NotPermittedRole that = (NotPermittedRole) o;

        if ( !name.equals( that.name ) ) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return "NotPermittedRole{" +
                "name='" + name + '\'' +
                '}';
    }

}
