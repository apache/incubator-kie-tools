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

import org.jboss.forge.roaster.model.Method;

/**
 * Used for defining methods to be loaded by the DataModelerService. Any CDI beans available at deployment time will be used,
 * and a method is loaded if any single {@link MethodFilter} accepts it.
 */
@FunctionalInterface
public interface MethodFilter {

    /**
     * Check if the given method is accepted by this filter.
     * @param method A Java method that could be loaded.
     * @return True if this method should be accepted.
     */
    boolean accept( Method<?, ?> method );
}
