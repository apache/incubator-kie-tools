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

package org.kie.workbench.common.services.datamodeller.driver;

import org.jboss.forge.roaster.model.JavaType;

/**
 * Used for defining nested classes to be loaded by the DataModelerService. Any CDI beans available at deployment time will be used,
 * and a nested class is loaded if any single {@link NestedClassFilter} accepts it.
 */
@FunctionalInterface
public interface NestedClassFilter {

    /**
     * Check if the given nested class is accepted by this filter.
     * @param javaType A Java type (nested class) that could be loaded.
     * @return True if this nested class should be accepted.
     */
    boolean accept( JavaType<?> javaType );
}
