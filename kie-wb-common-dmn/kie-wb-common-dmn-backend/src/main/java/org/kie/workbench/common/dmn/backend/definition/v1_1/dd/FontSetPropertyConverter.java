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

package org.kie.workbench.common.dmn.backend.definition.v1_1.dd;

import org.kie.dmn.model.api.dmndi.DMNStyle;
import org.kie.workbench.common.dmn.api.property.font.FontSet;

public class FontSetPropertyConverter {

    public static FontSet wbFromDMN(final DMNStyle dmn) {
        final FontSet result = new FontSet();
        if (null != dmn.getFontFamily()) {
            result.getFontFamily().setValue(dmn.getFontFamily());
        }
        if (null != dmn.getFontSize()) {
            result.getFontSize().setValue(dmn.getFontSize());
        }
        if (null != dmn.getFontColor()) {
            result.getFontColour().setValue(ColorUtils.wbFromDMN(dmn.getFontColor()));
        }
        return result;
    }

    public static DMNStyle dmnFromWB(final FontSet wb) {
        final DMNStyle result = new org.kie.dmn.model.v1_2.dmndi.DMNStyle();
        if (null != wb.getFontFamily().getValue()) {
            result.setFontFamily(wb.getFontFamily().getValue());
        }
        if (null != wb.getFontSize().getValue()) {
            result.setFontSize(wb.getFontSize().getValue());
        }
        if (null != wb.getFontColour().getValue()) {
            result.setFontColor(ColorUtils.dmnFromWB(wb.getFontColour().getValue()));
        }
        return result;
    }
}