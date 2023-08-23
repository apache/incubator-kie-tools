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

package org.kie.workbench.common.dmn.api.definition.model;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.dmn.api.definition.HasTypeRef;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.api.property.dmn.QNameHolder;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

public class InformationItemTest {

    private static final String ITEM_ID = "ITEM_ID";
    private static final String DESCRIPTION = "DESCRIPTION";
    private static final String INFORMATION_ITEM_NAME = "INFORMATION_ITEM_NAME";
    private InformationItem informationItem;

    @Before
    public void setup() {
        this.informationItem = new InformationItem();
    }

    @Test
    public void testTypeRefHolderWrapsTypeRef() {
        final QName typeRef = informationItem.getTypeRef();
        final QNameHolder typeRefHolder = informationItem.getTypeRefHolder();
        assertEquals(typeRef, typeRefHolder.getValue());
    }

    @Test
    public void testTypeRefHolderWrapsTypeRefAfterSettingTypeRef() {
        final QName typeRef = new QName();
        informationItem.setTypeRef(typeRef);
        assertEquals(typeRef, informationItem.getTypeRef());

        final QNameHolder typeRefHolder = informationItem.getTypeRefHolder();
        assertEquals(typeRef, typeRefHolder.getValue());
    }

    @Test
    public void testTypeRefHolderWrapsTYpeRefAfterSettingTypeRefHolder() {
        final QName typeRef = informationItem.getTypeRef();
        informationItem.setTypeRefHolder(new QNameHolder());
        assertEquals(typeRef, informationItem.getTypeRefHolder().getValue());
    }

    @Test
    public void testGetHasTypeRefs() {

        final List<HasTypeRef> actualHasTypeRefs = informationItem.getHasTypeRefs();
        final List<HasTypeRef> expectedHasTypeRefs = singletonList(informationItem);

        assertEquals(expectedHasTypeRefs, actualHasTypeRefs);
    }

    @Test
    public void testCopy() {
        final InformationItem source = new InformationItem(new Id(ITEM_ID), new Description(DESCRIPTION), new Name(INFORMATION_ITEM_NAME), BuiltInType.BOOLEAN.asQName());

        final InformationItem target = source.copy();

        assertNotNull(target);
        assertNotEquals(ITEM_ID, target.getId().getValue());
        assertEquals(DESCRIPTION, target.getDescription().getValue());
        assertEquals(INFORMATION_ITEM_NAME, target.getName().getValue());
        assertEquals(BuiltInType.BOOLEAN.asQName(), target.getTypeRef());
    }

    @Test
    public void testExactCopy() {
        final InformationItem source = new InformationItem(new Id(ITEM_ID), new Description(DESCRIPTION), new Name(INFORMATION_ITEM_NAME), BuiltInType.BOOLEAN.asQName());

        final InformationItem target = source.exactCopy();

        assertNotNull(target);
        assertEquals(ITEM_ID, target.getId().getValue());
        assertEquals(DESCRIPTION, target.getDescription().getValue());
        assertEquals(INFORMATION_ITEM_NAME, target.getName().getValue());
        assertEquals(BuiltInType.BOOLEAN.asQName(), target.getTypeRef());
    }
}
