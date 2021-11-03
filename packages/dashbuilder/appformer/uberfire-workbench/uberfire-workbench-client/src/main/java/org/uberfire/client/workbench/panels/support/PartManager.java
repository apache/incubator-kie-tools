/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.client.workbench.panels.support;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.workbench.panels.impl.LayoutPanelView;
import org.uberfire.commons.data.Pair;
import org.uberfire.workbench.model.PartDefinition;

/**
 * Manages part instances on behalf of {@link LayoutPanelView}.
 * It's needed to retain each parts state across perspective changes.
 */
@ApplicationScoped
public class PartManager {

    private final Map<PartDefinition, Widget> widgets = new HashMap<PartDefinition, Widget>();
    private Pair<PartDefinition, Widget> activePart;

    public Pair<PartDefinition, Widget> getActivePart() {
        return activePart;
    }

    public boolean hasActivePart() {
        return activePart != null;
    }

    public void registerPart(PartDefinition partDef,
                             Widget w) {
        if (widgets.containsKey(partDef)) {
            throw new IllegalArgumentException("Part already registered: " + partDef.getPlace().getIdentifier());
        }

        widgets.put(partDef,
                    w);
    }

    public void removePart(PartDefinition partDef) {
        /*
        TODO (hbraun): revisit panel managers with single parts
        if(partDef.equals(activePart.getK1()))
            throw new IllegalArgumentException("Cannot remove active part: "+ partDef.getPlace().getIdentifier());
            */
        if (partDef.equals(activePart.getK1())) {
            activePart = null;
        }

        widgets.remove(partDef);
    }

    public void clearParts() {
        widgets.clear();
        activePart = null;
    }

    public Collection<PartDefinition> getParts() {
        return Collections.unmodifiableSet(widgets.keySet());
    }

    public boolean hasPart(PartDefinition partDef) {
        return widgets.containsKey(partDef);
    }

    public Widget selectPart(PartDefinition partDef) {
        if (!hasPart(partDef)) {
            throw new IllegalArgumentException("Unknown part: " + partDef.getPlace().getIdentifier());
        }

        final Widget w = widgets.get(partDef);
        activePart = new Pair<PartDefinition, Widget>(partDef,
                                                      w);

        return activePart.getK2();
    }
}
