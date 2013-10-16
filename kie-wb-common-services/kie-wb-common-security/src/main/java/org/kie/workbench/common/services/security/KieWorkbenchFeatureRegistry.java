/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.kie.workbench.common.services.security;

import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;

import org.kie.workbench.common.services.security.impl.KieWorkbenchFeatureImpl;

@ApplicationScoped
public class KieWorkbenchFeatureRegistry {

    protected Map<String,KieWorkbenchFeature> featureRegistry = new HashMap<String, KieWorkbenchFeature>();

    public void registerFeature(KieWorkbenchFeature f) {
        featureRegistry.put(f.getId(), f);
    }

    public KieWorkbenchFeature getFeature(String id) {
        return featureRegistry.get(id);
    }

    public KieWorkbenchFeature registerFeature(String featureId, String descr) {
        KieWorkbenchFeature feature = new KieWorkbenchFeatureImpl(featureId, descr);
        featureRegistry.put(featureId, feature);
        return feature;
    }
}
