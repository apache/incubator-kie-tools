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

package org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model.dd;

import java.util.Objects;

import org.kie.workbench.common.dmn.api.property.font.FontSet;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmndi12.JSIDMNStyle;

public class FontSetPropertyConverter {

    public static FontSet wbFromDMN(final JSIDMNStyle dmn) {
        final FontSet result = new FontSet();
        if (Objects.nonNull(dmn.getFontFamily())) {
            result.getFontFamily().setValue(dmn.getFontFamily());
        }
        if (dmn.getFontSize() > 0) {
            result.getFontSize().setValue(dmn.getFontSize());
        }
        if (Objects.nonNull(dmn.getFontColor())) {
            result.getFontColour().setValue(ColorUtils.wbFromDMN(dmn.getFontColor()));
        }
        return result;
    }

    public static JSIDMNStyle dmnFromWB(final FontSet wb) {
        final JSIDMNStyle result = new JSIDMNStyle();
        if (Objects.nonNull(wb.getFontFamily().getValue())) {
            result.setFontFamily(wb.getFontFamily().getValue());
        }
        if (Objects.nonNull(wb.getFontSize().getValue())) {
            result.setFontSize(wb.getFontSize().getValue());
        }
        if (Objects.nonNull(wb.getFontColour().getValue())) {
            result.setFontColor(ColorUtils.dmnFromWB(wb.getFontColour().getValue()));
        }
        return result;
    }
}