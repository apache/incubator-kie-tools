/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import org.junit.Test;
import org.kie.dmn.model.api.ImportedValues;
import org.kie.dmn.model.api.LiteralExpression;
import org.kie.dmn.model.v1_2.TImportedValues;
import org.kie.dmn.model.v1_2.TLiteralExpression;
import org.kie.workbench.common.dmn.api.definition.v1_1.IsLiteralExpression;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.workbench.common.dmn.api.definition.v1_1.DMNModelInstrumentedBase.Namespace.KIE;

public abstract class BaseLiteralExpressionPropertyConverterTest<T extends IsLiteralExpression> {

    private static final String UUID = "uuid";

    private static final String TEXT = "text";

    private static final String DESCRIPTION = "description";

    private static final String LOCAL = "local";

    private static final String IMPORTED_ELEMENT = "imported-element";

    @Test
    public void testWBFromDMN() {
        final LiteralExpression dmn = new TLiteralExpression();
        final QName typeRef = new QName(KIE.getUri(), LOCAL, KIE.getPrefix());
        final ImportedValues importedValues = new TImportedValues();
        importedValues.setImportedElement(IMPORTED_ELEMENT);
        dmn.setId(UUID);
        dmn.setDescription(DESCRIPTION);
        dmn.setTypeRef(typeRef);
        dmn.setText(TEXT);
        dmn.setImportedValues(importedValues);

        final T wb = convertWBFromDMN(dmn);

        assertThat(wb.getId().getValue()).isEqualTo(UUID);
        assertThat(wb.getDescription().getValue()).isEqualTo(DESCRIPTION);
        assertThat(wb.getTypeRef().getNamespaceURI()).isEqualTo(KIE.getUri());
        assertThat(wb.getTypeRef().getLocalPart()).isEqualTo(LOCAL);
        assertThat(wb.getTypeRef().getPrefix()).isEqualTo(KIE.getPrefix());
        assertThat(wb.getText().getValue()).isEqualTo(TEXT);
        assertThat(wb.getImportedValues().getImportedElement()).isEqualTo(IMPORTED_ELEMENT);

        assertThat(wb.getImportedValues().getParent()).isEqualTo(wb);
    }

    protected abstract T convertWBFromDMN(final LiteralExpression dmn);
}
