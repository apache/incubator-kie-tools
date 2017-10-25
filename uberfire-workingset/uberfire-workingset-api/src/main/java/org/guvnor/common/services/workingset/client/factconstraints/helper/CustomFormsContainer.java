/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.common.services.workingset.client.factconstraints.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.guvnor.common.services.workingset.client.factconstraints.customform.CustomFormConfiguration;
import org.guvnor.common.services.workingset.client.factconstraints.customform.predefined.DefaultCustomFormImplementation;

public class CustomFormsContainer {

    //because a FactType.field can only have one customForm,
    //this map is: <"FactType.field", CustomForm>
    private Map<String, CustomFormConfiguration> customForms = new HashMap<String, CustomFormConfiguration>();

    public CustomFormsContainer(CustomFormConfiguration[] customFormsConfigs) {
        this(Arrays.asList(customFormsConfigs));
    }

    public CustomFormsContainer(Collection<CustomFormConfiguration> customFormsConfigs) {
        if (customFormsConfigs != null && !customFormsConfigs.isEmpty()) {
            for (CustomFormConfiguration c : customFormsConfigs) {
                putCustomForm(c);
            }
        }
    }

    //    public CustomFormsContainer() {
//
//    }
    public void removeCustomForm(CustomFormConfiguration cfc) {
        this.customForms.remove(this.createMapKey(cfc));
    }

    /**
     * If cfc.getCustomFormURL() is empty, the CustomFormConfiguration is removed.
     * @param cfc
     */
    public final void putCustomForm(CustomFormConfiguration cfc) {
        if (cfc.getCustomFormURL().trim().equals("")) {
            this.customForms.remove(this.createMapKey(cfc));
        } else {
            this.customForms.put(this.createMapKey(cfc),
                                 cfc);
        }
    }

    public CustomFormConfiguration getCustomForm(String factType,
                                                 String fieldName) {
        return this.customForms.get(this.createMapKey(factType,
                                                      fieldName));
    }

    public List<CustomFormConfiguration> getCustomForms() {
        return new ArrayList<CustomFormConfiguration>(this.customForms.values());
    }

    public boolean containsCustomFormFor(String factType,
                                         String fieldName) {
        return this.getCustomForm(factType,
                                  fieldName) != null;
    }

    private String createMapKey(String factType,
                                String fieldName) {
        return factType + "." + fieldName;
    }

    private String createMapKey(CustomFormConfiguration cfc) {
        return this.createMapKey(cfc.getFactType(),
                                 cfc.getFieldName());
    }

    public static CustomFormConfiguration getEmptyCustomFormConfiguration() {
        return new DefaultCustomFormImplementation();
    }
}
