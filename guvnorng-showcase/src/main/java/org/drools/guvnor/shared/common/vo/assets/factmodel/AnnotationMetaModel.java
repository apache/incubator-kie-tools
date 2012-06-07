/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.guvnor.shared.common.vo.assets.factmodel;

import java.util.HashMap;
import java.util.Map;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class AnnotationMetaModel {

    private static final long  serialVersionUID = 510l;
    public String              name;
    public Map<String, String> values           = new HashMap<String, String>();

    public AnnotationMetaModel() {
    }

    public AnnotationMetaModel(String name,
                               Map<String, String> values) {
        this.name = name;
        this.values = values;
    }

    public Map<String, String> getValues() {
        return values;
    }

}
