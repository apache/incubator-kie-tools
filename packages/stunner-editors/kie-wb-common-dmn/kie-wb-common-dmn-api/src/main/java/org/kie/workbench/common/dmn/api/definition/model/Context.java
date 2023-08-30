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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.dmn.api.definition.HasTypeRef;
import org.kie.workbench.common.dmn.api.definition.model.common.DomainObjectSearcherHelper;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.stunner.core.domainobject.DomainObject;
import org.kie.workbench.common.stunner.core.util.HashUtil;

import static org.kie.workbench.common.dmn.api.definition.model.common.HasTypeRefHelper.getFlatHasTypeRefs;

@Portable
public class Context extends Expression {

    private static final int STATIC_COLUMNS = 3;

    private List<ContextEntry> contextEntry;

    public Context() {
        this(new Id(),
             new Description(),
             new QName());
    }

    public Context(final Id id,
                   final Description description,
                   final QName typeRef) {
        super(id,
              description,
              typeRef);
    }

    @Override
    public Context copy() {
        final Context clonedContext = new Context();
        clonedContext.description = Optional.ofNullable(description).map(Description::copy).orElse(null);
        clonedContext.typeRef = Optional.ofNullable(typeRef).map(QName::copy).orElse(null);
        clonedContext.componentWidths = new ArrayList<>(componentWidths);
        clonedContext.contextEntry = Optional.ofNullable(contextEntry)
                .map(contextEntryList -> contextEntryList.stream().map(ContextEntry::copy).collect(Collectors.toList()))
                .orElse(null);
        return clonedContext;
    }

    @Override
    public Context exactCopy() {
        final Context exactelyClonedContext = new Context();
        exactelyClonedContext.id = Optional.ofNullable(id).map(Id::copy).orElse(null);
        exactelyClonedContext.description = Optional.ofNullable(description).map(Description::copy).orElse(null);
        exactelyClonedContext.typeRef = Optional.ofNullable(typeRef).map(QName::copy).orElse(null);
        exactelyClonedContext.componentWidths = new ArrayList<>(componentWidths);
        exactelyClonedContext.contextEntry = Optional.ofNullable(contextEntry)
                .map(contextEntryList -> contextEntryList.stream().map(ContextEntry::exactCopy).collect(Collectors.toList()))
                .orElse(null);
        return exactelyClonedContext;
    }

    @Override
    public Optional<DomainObject> findDomainObject(final String uuid) {
        return DomainObjectSearcherHelper.find(getContextEntry(), uuid);
    }

    public List<ContextEntry> getContextEntry() {
        if (contextEntry == null) {
            contextEntry = new ArrayList<>();
        }
        return this.contextEntry;
    }

    @Override
    public List<HasTypeRef> getHasTypeRefs() {

        final List<HasTypeRef> hasTypeRefs = super.getHasTypeRefs();

        hasTypeRefs.addAll(getFlatHasTypeRefs(getContextEntry()));

        return hasTypeRefs;
    }

    @Override
    public int getRequiredComponentWidthCount() {
        return STATIC_COLUMNS;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Context)) {
            return false;
        }

        final Context that = (Context) o;

        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }
        if (description != null ? !description.equals(that.description) : that.description != null) {
            return false;
        }
        if (typeRef != null ? !typeRef.equals(that.typeRef) : that.typeRef != null) {
            return false;
        }
        if (componentWidths != null ? !componentWidths.equals(that.componentWidths) : that.componentWidths != null) {
            return false;
        }
        return contextEntry != null ? contextEntry.equals(that.contextEntry) : that.contextEntry == null;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(id != null ? id.hashCode() : 0,
                                         description != null ? description.hashCode() : 0,
                                         typeRef != null ? typeRef.hashCode() : 0,
                                         componentWidths != null ? componentWidths.hashCode() : 0,
                                         contextEntry != null ? contextEntry.hashCode() : 0);
    }
}
