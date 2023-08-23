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

package org.kie.workbench.common.dmn.client.editors.expressions.util;

import java.util.function.UnaryOperator;

import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasTypeRef;
import org.kie.workbench.common.dmn.api.definition.HasVariable;
import org.kie.workbench.common.dmn.api.definition.model.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.api.editors.types.BuiltInTypeUtils;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;

public class TypeRefUtils {

    public static <E extends Expression> HasTypeRef getTypeRefOfExpression(final E expression,
                                                                           final HasExpression hasExpression) {
        HasTypeRef hasTypeRef = expression;
        final DMNModelInstrumentedBase base = hasExpression.asDMNModelInstrumentedBase();
        if (base instanceof HasVariable) {
            final HasVariable hasVariable = (HasVariable) base;
            hasTypeRef = hasVariable.getVariable();
        }

        return hasTypeRef;
    }

    public static <E extends Expression> QName getQNameOfExpression(final HasExpression hasExpression,
                                                                    final E expression,
                                                                    final String dataType,
                                                                    final UnaryOperator<QName> qNameNormalizer) {
        if (dataType != null && !dataType.isEmpty()) {
            return qNameNormalizer.apply(makeQName(dataType));
        } else {
            return getTypeRefOfExpression(expression, hasExpression).getTypeRef();
        }
    }

    private static QName makeQName(String dataType) {
        return BuiltInTypeUtils.isBuiltInType(dataType) ?
                BuiltInTypeUtils.findBuiltInTypeByName(dataType).orElse(BuiltInType.UNDEFINED).asQName() :
                new QName(QName.NULL_NS_URI, dataType);
    }
}
