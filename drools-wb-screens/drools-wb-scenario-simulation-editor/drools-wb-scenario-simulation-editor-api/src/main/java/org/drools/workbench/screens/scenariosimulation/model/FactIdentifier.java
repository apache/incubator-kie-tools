/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.scenariosimulation.model;

import java.util.Objects;

import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * A fact is identified by its name and the canonical name of its class
 */
@Portable
public class FactIdentifier {

    private String name;
    private String className;

    public static FactIdentifier INDEX = create("Index", Integer.class.getCanonicalName());
    public static FactIdentifier DESCRIPTION = create("Description", String.class.getCanonicalName());

    public FactIdentifier() {
    }

    public FactIdentifier(String name, String className) {
        this.name = name;
        this.className = className;
    }

    public String getName() {
        return name;
    }

    public String getClassName() {
        return className;
    }

    public static FactIdentifier create(String name, String className) {
        return new FactIdentifier(name, className);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FactIdentifier that = (FactIdentifier) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(className, that.className);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, className);
    }
}
