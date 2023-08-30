/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.dmn.client.marshaller.converters;

import java.util.Map;
import java.util.Objects;

import javax.xml.namespace.QName;

import org.kie.workbench.common.dmn.api.definition.model.ConstraintType;
import org.kie.workbench.common.dmn.api.definition.model.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.definition.model.OutputClauseUnaryTests;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Text;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITUnaryTests;
import org.kie.workbench.common.stunner.core.util.StringUtils;

public class OutputClauseUnaryTestsPropertyConverter {

    /**
     * Returns a non-null instance of OutputClauseUnaryTestsPropertyConverter. The Properties Panel needs
     * non-null objects to bind therefore a concrete object must always be returned.
     * @param dmn
     * @return
     */
    public static OutputClauseUnaryTests wbFromDMN(final JSITUnaryTests dmn) {
        if (Objects.isNull(dmn)) {
            return new OutputClauseUnaryTests();
        }
        final Id id = IdPropertyConverter.wbFromDMN(dmn.getId());
        final QName key = new QName(DMNModelInstrumentedBase.Namespace.KIE.getUri(),
                                    ConstraintType.CONSTRAINT_KEY,
                                    DMNModelInstrumentedBase.Namespace.KIE.getPrefix());
        final Map<QName, String> otherAttributes = JSITUnaryTests.getOtherAttributesMap(dmn);
        final String constraintString = otherAttributes.getOrDefault(key, "");
        final ConstraintType constraint = ConstraintType.fromString(constraintString);
        final OutputClauseUnaryTests result = new OutputClauseUnaryTests(id,
                                                                         new Text(dmn.getText()),
                                                                         constraint);
        return result;
    }

    /**
     * Returns a JSITLiteralExpression of null if the text of the OutputClauseLiteralExpression is empty.
     * @param wb
     * @return
     */
    public static JSITUnaryTests dmnFromWB(final OutputClauseUnaryTests wb) {
        if (Objects.isNull(wb)) {
            return null;
        } else if (Objects.isNull(wb.getText())) {
            return null;
        } else if (StringUtils.isEmpty(wb.getText().getValue())) {
            return null;
        }

        return UnaryTestsPropertyConverter.dmnFromWB(wb);
    }
}
