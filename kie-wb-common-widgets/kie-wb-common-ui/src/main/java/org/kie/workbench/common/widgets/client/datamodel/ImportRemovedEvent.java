/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.widgets.client.datamodel;

import org.appformer.project.datamodel.imports.Import;
import org.uberfire.commons.validation.PortablePreconditions;

/**
 * An event signalling removal of an import
 */
public class ImportRemovedEvent {

    private final Import item;
    private final AsyncPackageDataModelOracle dmo;

    public ImportRemovedEvent( final AsyncPackageDataModelOracle dmo,
                               final Import item ) {
        this.dmo = PortablePreconditions.checkNotNull( "dmo",
                                                       dmo );
        this.item = PortablePreconditions.checkNotNull( "item",
                                                        item );
    }

    public Import getImport() {
        return this.item;
    }

    public AsyncPackageDataModelOracle getDataModelOracle() {
        return this.dmo;
    }

}
