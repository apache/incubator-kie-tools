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

package org.kie.workbench.common.services.datamodeller.driver.impl.annotations;

import org.kie.workbench.common.services.datamodeller.core.AnnotationValuePairDefinition;
import org.kie.workbench.common.services.datamodeller.core.impl.AbstractAnnotationDefinition;
import org.kie.workbench.common.services.datamodeller.core.impl.AnnotationValuePairDefinitionImpl;

@Deprecated
public class DurationAnnotationDefinition extends AbstractAnnotationDefinition {

    public DurationAnnotationDefinition() {
        super(org.kie.api.definition.type.Duration.class.getName(), true, false);
        addValuePair( new AnnotationValuePairDefinitionImpl( "value", String.class.getName(), AnnotationValuePairDefinition.ValuePairType.STRING) );
    }

    public static DurationAnnotationDefinition getInstance() {
        return new DurationAnnotationDefinition();
    }
}
