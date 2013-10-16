/*
 * Copyright 2012 JBoss Inc
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
package org.kie.workbench.common.services.security.impl;

import org.kie.workbench.common.services.security.KieWorkbenchACL;
import org.kie.workbench.common.services.security.KieWorkbenchFeature;
import org.kie.workbench.common.services.security.KieWorkbenchFeatureRegistry;
import org.kie.workbench.common.services.security.KieWorkbenchPolicy;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@ApplicationScoped
public class KieWorkbenchACLImpl implements KieWorkbenchACL {

    public static final String PREFIX_DESCR = "feature.";
    public static final String PREFIX_CHILDREN = "group.";
    public static final String PREFIX_ROLES = "roles.";


    @Inject
    private KieWorkbenchFeatureRegistry featureRegistry;

    protected Map<String,Set<String>> grantedFeatures = new HashMap<String,Set<String>>();

    public KieWorkbenchFeatureRegistry getFeatureRegistry() {
        return featureRegistry;
    }

    public void setFeatureRegistry(KieWorkbenchFeatureRegistry featureRegistry) {
        this.featureRegistry = featureRegistry;
    }

    @Override
    public void grantAccess(String role, String... featureIds) {
        for (String featureId : featureIds) {
            getGrantedRoles(featureId).add(role);

            KieWorkbenchFeature feature = featureRegistry.getFeature(featureId);
            if (feature != null && feature.getChildren() != null) {
                for (KieWorkbenchFeature child : feature.getChildren()) {
                    grantAccess(role, child.getId());
                }
            }
        }
    }

    @Override
    public void denyAccess(String role, String... featureIds) {
        for (String featureId : featureIds) {
            getGrantedRoles(featureId).remove(role);

            KieWorkbenchFeature feature = featureRegistry.getFeature(featureId);
            if (feature != null && feature.getChildren() != null) {
                for (KieWorkbenchFeature child : feature.getChildren()) {
                    denyAccess(role, child.getId());
                }
            }
        }
    }

    @Override
    public Set<String> getGrantedRoles(String featureId) {
        Set<String> roles = grantedFeatures.get(featureId);
        if (roles == null) grantedFeatures.put(featureId, roles = new HashSet<String>());
        return roles;
    }

    @Override
    public void activatePolicy(KieWorkbenchPolicy policy) {
        if (policy == null) return;

        Map<String,String> toDeny = new HashMap<String,String>();
        for (String entry : policy.keySet()) {
            String featureId = getFeatureId(entry);
            if (featureRegistry.getFeature(featureId) == null) {
                buildFeature(featureId, policy, toDeny);
            }
        }
        for (String featureId : toDeny.keySet()) {
            denyAccess(toDeny.get(featureId), featureId);
        }
    }

    protected KieWorkbenchFeature buildFeature(String featureId, KieWorkbenchPolicy policy, Map<String,String> toDeny) {
        String descr = getDescription(featureId, policy);
        String[] roles = getRoles(featureId, policy);
        String[] children = getChildren(featureId, policy);

        // Register the feature
        KieWorkbenchFeature result = featureRegistry.registerFeature(featureId, descr);

        // For group features its children must be fetched and initialized first.
        if (children != null) {
            Set<String> noChildren = new HashSet<String>();
            for (int i = 0; i < children.length; i++) {
                String child = children[i].trim();
                if (child.startsWith("!")) {
                    child = child.substring(1);
                    noChildren.add(child);
                } else {
                    result.addChildren(buildFeature(child, policy, toDeny));
                }
            }
            for (String child : noChildren) {
                result.removeChildren(buildFeature(child, policy, toDeny));
            }
        }
        // For role constrained features access must be granted/denied.
        if (roles != null) {
            for (int i = 0; i < roles.length; i++) {
                String role = roles[i].trim();
                if (role.startsWith("!")) {
                    role = role.substring(1);
                    toDeny.put(result.getId(), role);
                } else {
                    grantAccess(role, result.getId());
                }
            }
        }
        return result;
    }

    protected String getFeatureId(String entry) {
        String prefix = getPrefix(entry);
        return entry.substring(prefix.length());
    }

    protected String getDescription(String featureId, Map<String,String> policy) {
        return policy.get(PREFIX_DESCR + featureId);
    }

    protected String[] getChildren(String featureId, Map<String,String> policy) {
        String value = policy.get(PREFIX_CHILDREN + featureId);
        if (value == null) return null;
        return value.split(",");
    }

    protected String[] getRoles(String featureId, Map<String,String> policy) {
        String value = policy.get(PREFIX_ROLES + featureId);
        if (value == null) return null;
        return value.split(",");
    }

    protected String getPrefix(String entry) {
        if (entry.startsWith(PREFIX_DESCR)) return PREFIX_DESCR;
        if (entry.startsWith(PREFIX_CHILDREN)) return PREFIX_CHILDREN;
        if (entry.startsWith(PREFIX_ROLES)) return PREFIX_ROLES;
        return "";
    }

}
