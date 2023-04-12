/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.definition.jsadapter;

import javax.enterprise.context.ApplicationScoped;

import elemental2.core.Reflect;
import org.kie.workbench.common.stunner.core.definition.adapter.PropertyAdapter;
import org.kie.workbench.common.stunner.core.i18n.StunnerTranslationService;

import static org.kie.workbench.common.stunner.core.definition.jsadapter.JsDefinitionAdapter.getJsDefinitionId;

@ApplicationScoped
public class JsPropertyAdapter implements PropertyAdapter<JsDefinitionProperty, Object> {

    private StunnerTranslationService translationService;

    @Override
    public String getId(JsDefinitionProperty property) {
        String defId = getJsDefinitionId(property.getPojo());
        return defId + "." + property.getField();
    }

    @Override
    public String getCaption(JsDefinitionProperty property) {
        Object pojo = property.getPojo();
        String id = getJsDefinitionId(pojo);
        String field = property.getField();
        return translationService.getValue(id + StunnerTranslationService.I18N_SEPARATOR + "property" + StunnerTranslationService.I18N_SEPARATOR + field);
    }

    @Override
    public Object getValue(JsDefinitionProperty property) {
        return Reflect.get(property.getPojo(), property.getField());
    }

    @Override
    public void setValue(JsDefinitionProperty property, Object value) {
        Reflect.set(property.getPojo(), property.getField(), value);
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public boolean accepts(Class<?> type) {
        return JsDefinitionProperty.class.equals(type);
    }

    public void setTranslationService(StunnerTranslationService translationService) {
        this.translationService = translationService;
    }
}