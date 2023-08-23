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

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.kie.workbench.common.stunner.core.util.StringUtils;

public class QNamePropertyConverter {

    public static QName wbFromDMN(final String qNameAsString) {
        if (StringUtils.isEmpty(qNameAsString)) {
            return BuiltInType.UNDEFINED.asQName();
        }
        final javax.xml.namespace.QName qName = javax.xml.namespace.QName.valueOf(qNameAsString);

        return new QName(qName.getNamespaceURI(),
                         qName.getLocalPart(),
                         qName.getPrefix());
    }

    /*
     * Handles setting QName as appropriate back on DMN node
     */
    public static void setDMNfromWB(final QName qname,
                                    final Consumer<String> setter) {
        if (Objects.nonNull(qname)) {
            final Optional<javax.xml.namespace.QName> dmnTypeRef = dmnFromWB(qname);
            dmnTypeRef.ifPresent(typeRef -> setter.accept(typeRef.toString()));
        }
    }

    public static Optional<javax.xml.namespace.QName> dmnFromWB(final QName wb) {
        if (Objects.nonNull(wb)) {
            if (Objects.equals(wb, BuiltInType.UNDEFINED.asQName())) {
                return Optional.empty();
            }
            return Optional.of(new javax.xml.namespace.QName(wb.getNamespaceURI(),
                                                             wb.getLocalPart(),
                                                             wb.getPrefix()));
        } else {
            return Optional.empty();
        }
    }
}
