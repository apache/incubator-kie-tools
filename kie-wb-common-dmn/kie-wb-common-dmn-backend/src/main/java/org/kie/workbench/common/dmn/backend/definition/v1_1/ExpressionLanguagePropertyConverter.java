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

import org.kie.workbench.common.dmn.api.property.dmn.ExpressionLanguage;
import org.kie.workbench.common.stunner.core.util.StringUtils;

public class ExpressionLanguagePropertyConverter {

    public static ExpressionLanguage wbFromDMN(final String language) {
        if (language == null) {
            return new ExpressionLanguage("");
        } else {
            return new ExpressionLanguage(language);
        }
    }

    public static String dmnFromWB(final ExpressionLanguage language) {
        if (language == null) {
            return null;
        } else if (StringUtils.isEmpty(language.getValue())) {
            return null;
        } else {
            return language.getValue();
        }
    }
}