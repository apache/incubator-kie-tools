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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.kie.workbench.common.services.security.KieWorkbenchFeature;

public class KieWorkbenchFeatureImpl implements KieWorkbenchFeature {

    protected String id;
    protected String description;
    protected List<KieWorkbenchFeature> children = new ArrayList<KieWorkbenchFeature>();

    public KieWorkbenchFeatureImpl() {
    }

    public KieWorkbenchFeatureImpl(String id, String description) {
        this();
        this.id = id;
        this.description = description;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public List<KieWorkbenchFeature> getChildren() {
        return children;
    }

    @Override
    public void addChildren(KieWorkbenchFeature f) {
        children.add(f);
    }

    @Override
    public void removeChildren(KieWorkbenchFeature feature) {
        if (children == null || children.isEmpty()) return;

        Iterator<KieWorkbenchFeature> it = children.iterator();
        while (it.hasNext()) {
            KieWorkbenchFeature f = it.next();
            if (f.equals(feature)) it.remove();

            f.removeChildren(feature);
        }
    }

    @Override
    public void clearChildren() {
        children.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        KieWorkbenchFeatureImpl that = (KieWorkbenchFeatureImpl) o;

        if (!id.equals(that.id)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "KieWorkbenchFeatureImpl{" +
                "id='" + id + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean implies(KieWorkbenchFeature feature) {
        if (this.equals(feature)) return true;
        if (this.getChildren() == null) return false;

        for (KieWorkbenchFeature child : this.getChildren()) {
            if (child.implies(feature)) {
                return true;
            }
        }
        return false;
    }
}
