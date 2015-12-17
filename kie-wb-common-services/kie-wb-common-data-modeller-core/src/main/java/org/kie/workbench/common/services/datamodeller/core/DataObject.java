/**
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.services.datamodeller.core;

import java.util.List;

public interface DataObject extends JavaClass {

    List<ObjectProperty> getProperties();

    ObjectProperty addProperty(String name, String className);

    ObjectProperty addProperty(String name, String className, Visibility visibility, boolean isStatic, boolean isFinal);

    ObjectProperty addProperty(String name, String className, boolean multiple);

    ObjectProperty addProperty(String name, String className, boolean multiple, Visibility visibility, boolean isStatic, boolean isFinal);

    ObjectProperty addProperty(String name, String className, boolean multiple, String bag);

    ObjectProperty addProperty(String name, String className, boolean multiple, String bag, Visibility visibility, boolean isStatic, boolean isFinal);

    ObjectProperty addProperty( ObjectProperty property );

    boolean hasProperty( String name );

    ObjectProperty removeProperty( String name );

    ObjectProperty getProperty( String name );

    //TODO added just for refactoring purposes, check where to move this
    ObjectProperty getUnManagedProperty( String propertyName );

    //TODO added just for refactoring purposes, check where to move this
    List<ObjectProperty> getUnmanagedProperties();

}
