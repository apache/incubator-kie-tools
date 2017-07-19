/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.stunner.bpmn.backend.legacy;

import java.util.Comparator;
import java.util.List;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.Lane;
import org.eclipse.bpmn2.LaneSet;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.bpmn2.di.BPMNEdge;
import org.eclipse.bpmn2.di.BPMNShape;
import org.eclipse.dd.di.DiagramElement;

public final class DIZorderComparator implements Comparator<DiagramElement> {

    @Override
    public int compare(DiagramElement a,
                       DiagramElement b) {
        boolean aShape = a instanceof BPMNShape;
        boolean bShape = b instanceof BPMNShape;
        boolean aEdge = a instanceof BPMNEdge;
        boolean bEdge = b instanceof BPMNEdge;
        if (aShape && bEdge) {
            return -1;
        } else if (aEdge && bShape) {
            return 1;
        }
        if (aShape && bShape) {
            return compareShape((BPMNShape) a,
                                (BPMNShape) b);
        }
        return 0;
    }

    private int compareShape(BPMNShape a,
                             BPMNShape b) {
        BaseElement aElem = a.getBpmnElement();
        BaseElement bElem = b.getBpmnElement();
        boolean aIsSecondTier = aElem instanceof Lane || aElem instanceof SubProcess;
        boolean bIsSecondTier = bElem instanceof Lane || bElem instanceof SubProcess;
        if (aIsSecondTier && bIsSecondTier) {
            if (isParent(aElem,
                         bElem)) {
                return -1;
            } else if (isParent(bElem,
                                aElem)) {
                return 1;
            }
            return 0;
        } else if (aIsSecondTier && !bIsSecondTier) {
            return -1;
        } else if (!aIsSecondTier && bIsSecondTier) {
            return 1;
        }
        return 0;
    }

    private boolean isParent(BaseElement parent,
                             BaseElement child) {
        if (child instanceof FlowNode) {
            if (((FlowNode) child).getLanes().contains(parent)) {
                return true;
            } else if (parent instanceof Lane) {
                return isChildParent(parent,
                                     child);
            }
        } else if (parent instanceof Lane) {
            if (child instanceof Lane) {
                LaneSet childLaneSet = ((Lane) parent).getChildLaneSet();
                if (childLaneSet == null) {
                    return false;
                }
                if (((Lane) parent).getChildLaneSet().getLanes().contains(child)) {
                    return true;
                }
                return isChildParent(parent,
                                     child);
            }
        }
        return false;
    }

    private boolean isChildParent(BaseElement parent,
                                  BaseElement child) {
        LaneSet childLaneSet = ((Lane) parent).getChildLaneSet();
        if (childLaneSet == null) {
            return false;
        }
        List<Lane> lanes = childLaneSet.getLanes();
        for (Lane lane : lanes) {
            if (isParent(lane,
                         child)) {
                return true;
            }
        }
        return false;
    }
}
