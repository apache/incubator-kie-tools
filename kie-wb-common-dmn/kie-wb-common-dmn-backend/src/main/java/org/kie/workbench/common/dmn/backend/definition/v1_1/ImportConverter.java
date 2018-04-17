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
package org.kie.workbench.common.dmn.backend.definition.v1_1;

import java.util.HashMap;
import java.util.Map;

import org.kie.workbench.common.dmn.api.definition.v1_1.Import;
import org.kie.workbench.common.dmn.api.property.dmn.LocationURI;
import org.kie.workbench.common.dmn.api.property.dmn.QName;

public final class ImportConverter {

  public static Import nodeFromDMN(org.kie.dmn.model.v1_1.Import source) {
    LocationURI locationURI = new LocationURI(source.getLocationURI());
    Import result = new Import(source.getNamespace(), locationURI, source.getImportType());
    Map<QName, String> additionalAttributes = new HashMap<>();
    for (Map.Entry<javax.xml.namespace.QName, String> entry : source.getAdditionalAttributes().entrySet()) {
      additionalAttributes.put(QNamePropertyConverter.wbFromDMN(entry.getKey()), entry.getValue());
    }
    result.setAdditionalAttributes(additionalAttributes);
    return result;
  }

  public static org.kie.dmn.model.v1_1.Import dmnFromNode(Import source) {
    org.kie.dmn.model.v1_1.Import result = new org.kie.dmn.model.v1_1.Import();
    result.setImportType(source.getImportType());
    result.setLocationURI(source.getLocationURI().getValue());
    result.setNamespace(source.getNamespace());
    Map<javax.xml.namespace.QName, String> additionalAttributes = new HashMap<>();
    for (Map.Entry<QName, String> entry : source.getAdditionalAttributes().entrySet()) {
      additionalAttributes.put(QNamePropertyConverter.dmnFromWB(entry.getKey()).get(), entry.getValue());
    }
    result.setAdditionalAttributes(additionalAttributes);
    return result;
  }
}
