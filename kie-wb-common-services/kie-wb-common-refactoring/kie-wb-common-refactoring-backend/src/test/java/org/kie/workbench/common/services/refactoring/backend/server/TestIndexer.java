/*
 * Copyright 2014 JBoss, by Red Hat, Inc
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
package org.kie.workbench.common.services.refactoring.backend.server;

import org.uberfire.io.IOService;
import org.uberfire.metadata.engine.Indexer;
import org.uberfire.workbench.type.ResourceTypeDefinition;

public interface TestIndexer<T extends ResourceTypeDefinition> extends Indexer {

    /**
     * Mock CDI injection of IOService
     * @param ioService
     */
    void setIOService( final IOService ioService );

    /**
     * Mock CDI injection of ResourceTypeDefinition
     * @param type
     */
    void setResourceTypeDefinition( final T type );

}
