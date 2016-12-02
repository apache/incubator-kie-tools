/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.workbench.screens.guided.dtable.client.widget.analysis;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.drools.workbench.models.datamodel.oracle.ModelField;
import org.uberfire.commons.validation.PortablePreconditions;
import org.drools.workbench.services.verifier.plugin.client.api.FactTypes;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracleImpl;

public class FieldTypeProducer {

    private AsyncPackageDataModelOracle oracle;

    public FieldTypeProducer( final AsyncPackageDataModelOracle oracle ) {
        this.oracle = PortablePreconditions.checkNotNull( "oracle",
                                                          oracle );
    }

    public FactTypes getFactTypes() {
        final FactTypes factTypes = new FactTypes();

        final Map<String, ModelField[]> filteredFactTypes = ( (AsyncPackageDataModelOracleImpl) oracle ).getFilteredFactTypes();

        for ( final String factTypeName : filteredFactTypes.keySet() ) {

            final Set<FactTypes.Field> fields = new HashSet<>();

            for ( final ModelField modelField : filteredFactTypes.get( factTypeName ) ) {
                fields.add( new FactTypes.Field( modelField.getName(),
                                                 modelField.getType() ) );
            }

            factTypes.add( new FactTypes.FactType( factTypeName,
                                                   fields ) );
        }

        return factTypes;
    }
}
