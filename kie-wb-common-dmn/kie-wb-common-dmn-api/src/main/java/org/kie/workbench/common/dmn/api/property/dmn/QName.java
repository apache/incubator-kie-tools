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
package org.kie.workbench.common.dmn.api.property.dmn;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.workbench.common.dmn.api.definition.v1_1.DMNModelInstrumentedBase.Namespace;
import org.kie.workbench.common.dmn.api.property.DMNProperty;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
public class QName implements DMNProperty {

    public static final String NULL_NS_URI = "";

    public static final String DEFAULT_NS_PREFIX = "";

    private String namespaceURI;

    private String localPart;

    private String prefix;

    public QName() {
        this(NULL_NS_URI,
             BuiltInType.STRING.getName(),
             Namespace.FEEL.getPrefix());
    }

    public QName(final String namespaceURI,
                 final String localPart) {
        this(namespaceURI,
             localPart,
             DEFAULT_NS_PREFIX);
    }

    public QName(final String namespaceURI,
                 final String localPart,
                 final String prefix) {
        if (namespaceURI == null) {
            this.namespaceURI = NULL_NS_URI;
        } else {
            this.namespaceURI = namespaceURI;
        }
        this.localPart = PortablePreconditions.checkNotNull("localPart", localPart);
        this.prefix = PortablePreconditions.checkNotNull("prefix", prefix);
    }

    public String getNamespaceURI() {
        return namespaceURI;
    }

    public String getLocalPart() {
        return localPart;
    }

    public String getPrefix() {
        return prefix;
    }

    /**
     * See {@link javax.xml.namespace.QName#toString()}
     */
    @Override
    public String toString() {
        if (namespaceURI.equals(QName.NULL_NS_URI)) {
            return localPart;
        } else {
            return "{" + namespaceURI + "}" + localPart;
        }
    }

    /**
     * See {@link javax.xml.namespace.QName#equals(Object)}
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final QName qName = (QName) o;

        if (!namespaceURI.equals(qName.namespaceURI)) {
            return false;
        }
        return localPart.equals(qName.localPart);
    }

    /**
     * See {@link javax.xml.namespace.QName#hashCode()}
     */
    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(namespaceURI.hashCode(),
                                         localPart.hashCode());
    }
}
