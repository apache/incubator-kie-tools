/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.backend.definition.v1_1;

import javax.xml.namespace.QName;

import org.kie.workbench.common.dmn.api.definition.v1_1.ConstraintType;
import org.kie.workbench.common.dmn.api.definition.v1_1.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.definition.v1_1.IsUnaryTests;
import org.kie.workbench.common.dmn.api.definition.v1_1.UnaryTests;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.ExpressionLanguage;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Text;

public class UnaryTestsPropertyConverter {

    public static UnaryTests wbFromDMN(final org.kie.dmn.model.api.UnaryTests dmn) {
        if (dmn == null) {
            return null;
        }
        final Id id = new Id(dmn.getId());
        final Description description = DescriptionPropertyConverter.wbFromDMN(dmn.getDescription());
        final ExpressionLanguage expressionLanguage = ExpressionLanguagePropertyConverter.wbFromDMN(dmn.getExpressionLanguage());
        ConstraintType constraintTypeField = null;
        final QName key = new QName(DMNModelInstrumentedBase.Namespace.KIE.getUri(),
                                    ConstraintType.CONSTRAINT_KEY,
                                    DMNModelInstrumentedBase.Namespace.KIE.getPrefix());
        if (dmn.getAdditionalAttributes().containsKey(key)) {
            constraintTypeField = ConstraintTypeFieldPropertyConverter.wbFromDMN(dmn.getAdditionalAttributes().get(key));
        }
        final UnaryTests result = new UnaryTests(id,
                                                 description,
                                                 new Text(dmn.getText()),
                                                 expressionLanguage,
                                                 constraintTypeField);
        return result;
    }

    public static org.kie.dmn.model.api.UnaryTests dmnFromWB(final IsUnaryTests wb) {
        if (wb == null) {
            return null;
        }
        final org.kie.dmn.model.api.UnaryTests result = new org.kie.dmn.model.v1_2.TUnaryTests();
        result.setId(wb.getId().getValue());
        result.setText(wb.getText().getValue());

        final ConstraintType constraint = wb.getConstraintType();

        if (constraint != null) {
            final QName key = new QName(DMNModelInstrumentedBase.Namespace.KIE.getUri(),
                                        ConstraintType.CONSTRAINT_KEY,
                                        DMNModelInstrumentedBase.Namespace.KIE.getPrefix());
            result.getAdditionalAttributes().put(key, constraint.value());
        }

        return result;
    }
}
