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

package org.kie.workbench.common.dmn.api.property.dmn;

import java.util.HashSet;

import org.junit.Test;
import org.kie.workbench.common.dmn.api.definition.model.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class QNameTest {

    private final static String NAMESPACE_URI = "namespace-uri";

    private final static String LOCAL_PART = "local-part";

    private final static String PREFIX = "prefix";

    private final static QName QNAME1 = new QName(NAMESPACE_URI, LOCAL_PART, PREFIX);

    private final static QName QNAME2 = new QName(NAMESPACE_URI, LOCAL_PART, PREFIX);

    private final static QName QNAME3 = new QName(NAMESPACE_URI, LOCAL_PART);

    @Test
    public void checkEquals() {
        assertEquals(QNAME1, QNAME2);
        assertEquals(QNAME1, QNAME3);
        assertEquals(QNAME2, QNAME3);
    }

    @Test
    public void checkHashCode() {
        assertEquals(QNAME1.hashCode(), QNAME2.hashCode());
        assertEquals(QNAME1.hashCode(), QNAME3.hashCode());
        assertEquals(QNAME2.hashCode(), QNAME3.hashCode());

        final HashSet<QName> qNames = new HashSet<>();

        qNames.add(QNAME1);

        assertTrue(qNames.contains(QNAME2));
        assertTrue(qNames.contains(QNAME3));
    }

    @Test
    public void testZeroArgumentConstructor() {
        final QName implicitQName = new QName();
        final QName explicitQName = new QName(QName.NULL_NS_URI,
                                              BuiltInType.UNDEFINED.getName(),
                                              DMNModelInstrumentedBase.Namespace.FEEL.getPrefix());

        assertEquals(explicitQName, implicitQName);
    }

    @Test
    public void testConstructorWithBuiltInTypeParameter() {
        final QName qname = new QName(BuiltInType.STRING);
        assertEquals(qname.getLocalPart(), BuiltInType.STRING.getName());
        assertEquals(qname.getNamespaceURI(), QName.NULL_NS_URI);
    }

    @Test
    public void testCopy() {
        final QName source = new QName(NAMESPACE_URI, LOCAL_PART, PREFIX);

        final QName target = source.copy();

        assertNotNull(target);
        assertEquals(NAMESPACE_URI, target.getNamespaceURI());
        assertEquals(LOCAL_PART, target.getLocalPart());
        assertEquals(PREFIX, target.getPrefix());
    }
}
