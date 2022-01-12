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

package org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.impl.numeric.integers;

import java.math.BigInteger;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.CellEditionHandler;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.TableEntry;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.impl.numeric.AbstractNumericEditableColumnGenerator;
import org.kie.workbench.common.forms.dynamic.client.resources.i18n.FormRenderingConstants;

@Dependent
public class BigIntegerEditableColumnGenerator extends AbstractNumericEditableColumnGenerator<BigInteger> {

    @Inject
    public BigIntegerEditableColumnGenerator(TranslationService translationService) {
        super(translationService);
    }

    @Override
    public String[] getTypes() {
        return new String[]{BigInteger.class.getName()};
    }

    @Override
    public boolean doValidate(String flatValue,
                                 TableEntry<BigInteger> model,
                                 CellEditionHandler<BigInteger> cellEditionHandler) {
        if (flatValue != null && !flatValue.isEmpty()) {
            try {
                doConvert(flatValue);
            } catch (Exception ex) {
                cellEditionHandler.showValidationError(translationService.getTranslation(FormRenderingConstants.InvalidInteger));
                return false;
            }
        }
        return true;
    }

    @Override
    public BigInteger doConvert(String flatValue) {
        return new BigInteger(flatValue);
    }
}
