/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

import java.util.Optional;

import org.kie.dmn.model.api.InformationItem;
import org.kie.dmn.model.api.NamedElement;
import org.kie.dmn.model.v1_2.TInformationItem;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.v1_1.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.definition.v1_1.InformationItemPrimary;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;

public class InformationItemPrimaryPropertyConverter {

    private static final String DEFAULT_NAME = "";

    public static InformationItemPrimary wbFromDMN(final InformationItem dmn,
                                                   final org.kie.dmn.model.api.DMNModelInstrumentedBase parent) {
        if (dmn == null) {
            return null;
        }
        final Id id = new Id(dmn.getId());
        final Name name = new Name(getParentName(parent));
        final QName typeRef = QNamePropertyConverter.wbFromDMN(dmn.getTypeRef(), dmn);

        return new InformationItemPrimary(id,
                                          name,
                                          typeRef);
    }

    public static TInformationItem dmnFromWB(final InformationItemPrimary wb,
                                             final DMNModelInstrumentedBase parent) {
        if (wb == null) {
            return null;
        }
        final TInformationItem result = new TInformationItem();
        final QName typeRef = wb.getTypeRef();

        result.setId(wb.getId().getValue());
        result.setName(getParentName(parent));

        QNamePropertyConverter.setDMNfromWB(typeRef, result::setTypeRef);

        return result;
    }

    static String getParentName(final org.kie.dmn.model.api.DMNModelInstrumentedBase parent) {
        if (parent instanceof NamedElement) {
            final NamedElement namedElement = (NamedElement) parent;
            final Optional<String> name = Optional.ofNullable(namedElement.getName());
            return name.orElse(DEFAULT_NAME);
        }
        return DEFAULT_NAME;
    }

    static String getParentName(final DMNModelInstrumentedBase parent) {
        if (parent instanceof HasName) {
            final HasName hasName = (HasName) parent;
            final Optional<Name> name = Optional.ofNullable(hasName.getName());
            return name.map(Name::getValue).orElse(DEFAULT_NAME);
        }
        return DEFAULT_NAME;
    }
}
