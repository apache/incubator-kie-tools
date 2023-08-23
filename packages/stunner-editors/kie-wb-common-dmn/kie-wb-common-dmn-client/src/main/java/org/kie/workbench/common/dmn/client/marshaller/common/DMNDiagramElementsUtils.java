/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.dmn.client.marshaller.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import jsinterop.base.Js;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DRGDiagramUtils;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dc.JSIBounds;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.di.JSIDiagramElement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDefinitions;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmndi12.JSIDMNDI;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmndi12.JSIDMNDiagram;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmndi12.JSIDMNEdge;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmndi12.JSIDMNShape;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.JsUtils;

import static org.kie.workbench.common.dmn.client.docks.navigator.drds.DRGDiagramUtils.DRG;
import static org.kie.workbench.common.dmn.client.docks.navigator.drds.DRGDiagramUtils.isDRG;
import static org.kie.workbench.common.dmn.client.marshaller.common.IdUtils.uniqueId;
import static org.kie.workbench.common.dmn.client.marshaller.common.JsInteropUtils.forEach;
import static org.kie.workbench.common.dmn.client.marshaller.common.JsInteropUtils.jsCopy;
import static org.kie.workbench.common.stunner.core.util.StringUtils.isEmpty;

public class DMNDiagramElementsUtils {

    public void ensureDRGElementExists(final JSITDefinitions dmnDefinitions) {

        final List<JSIDMNDiagram> dmnDiagrams = getValidDMNDiagrams(dmnDefinitions);
        final Optional<JSIDMNDiagram> drgDiagramElement = findDRGDiagramElement(dmnDiagrams);

        if (drgDiagramElement.isPresent()) {
            return;
        }

        if (dmnDiagrams.size() == 1) {
            renameDiagramElement(dmnDiagrams);
            return;
        }

        final JSIDMNDiagram drg = generateDRGElement(dmnDefinitions);
        dmnDefinitions.getDMNDI().addDMNDiagram(drg);
    }

    private List<JSIDMNDiagram> getValidDMNDiagrams(final JSITDefinitions dmnDefinitions) {
        final List<JSIDMNDiagram> dmnDiagrams = getDmnDiagram(dmnDefinitions);

        forEach(dmnDiagrams, dmnDiagram -> {
            if (isEmpty(dmnDiagram.getId())) {
                dmnDiagram.setId(uniqueId());
            }
        });

        return dmnDiagrams;
    }

    private JSIDMNDiagram generateDRGElement(final JSITDefinitions dmnDefinitions) {
        final JSIDMNDiagram drg = DRGDiagramUtils.newJSIDRGInstance();
        final double[] globalOriginX = {0};

        forEach(getDmnDiagram(dmnDefinitions), dmnDiagram -> {
            final List<JSIDiagramElement> elements = dmnDiagram.getDMNDiagramElement();
            final double[] diagramOriginX = {globalOriginX[0]};

            forEach(elements, element -> {
                final JSIDiagramElement copy = Js.uncheckedCast(jsCopy(element));

                if (JSIDMNShape.instanceOf(copy)) {

                    final JSIDMNShape shape = Js.uncheckedCast(copy);
                    final JSIBounds bounds = shape.getBounds();
                    final double currentMax = bounds.getX() + bounds.getWidth();

                    shape.setId(uniqueId());
                    bounds.setX(diagramOriginX[0] + bounds.getX());

                    if (currentMax > globalOriginX[0]) {
                        globalOriginX[0] = currentMax;
                    }
                }

                if (JSIDMNEdge.instanceOf(copy)) {
                    final JSIDMNEdge shape = Js.uncheckedCast(copy);
                    shape.setId(uniqueId());
                    shape.setOtherAttributes(null);
                    forEach(shape.getWaypoint(), jsiPoint -> jsiPoint.setX(diagramOriginX[0] + jsiPoint.getX()));
                }

                drg.addDMNDiagramElement(Js.uncheckedCast(JsUtils.getWrappedElement(copy)));
            });
        });

        return drg;
    }

    private void renameDiagramElement(final List<JSIDMNDiagram> dmnDiagramElements) {
        final JSIDMNDiagram dmnDiagramElement = Js.uncheckedCast(dmnDiagramElements.get(0));
        dmnDiagramElement.setName(DRG);
    }

    private Optional<JSIDMNDiagram> findDRGDiagramElement(final List<JSIDMNDiagram> dmnDiagramElements) {

        final List<JSIDMNDiagram> eligibleAsDRGs = findEligibleDRGs(dmnDiagramElements);
        final Optional<JSIDMNDiagram> drg = eligibleAsDRGs.stream().findFirst();

        if (drg.isPresent() && eligibleAsDRGs.size() > 1) {
            renameOtherEligibleDRGs(eligibleAsDRGs, Js.uncheckedCast(drg.get()));
        }

        return drg;
    }

    private void renameOtherEligibleDRGs(final List<JSIDMNDiagram> eligibleAsDRGs,
                                         final JSIDMNDiagram drg) {

        final String drgId = drg.getId();
        final AtomicInteger index = new AtomicInteger();

        forEach(eligibleAsDRGs, eligibleAsDRG -> {
            final String currentId = eligibleAsDRG.getId();

            if (!Objects.equals(currentId, drgId)) {
                eligibleAsDRG.setName(eligibleAsDRG.getName() + "-" + index.incrementAndGet());
            }
        });
    }

    private List<JSIDMNDiagram> findEligibleDRGs(final List<JSIDMNDiagram> dmnDiagramElements) {

        final List<JSIDMNDiagram> eligibleDRGs = new ArrayList<>();

        forEach(dmnDiagramElements, dmnDiagramElement -> {
            if (isDRG(dmnDiagramElement)) {
                eligibleDRGs.add(dmnDiagramElement);
            }
        });

        return eligibleDRGs;
    }

    private List<JSIDMNDiagram> getDmnDiagram(final JSITDefinitions dmnDefinitions) {
        if (dmnDefinitions.getDMNDI() == null) {
            dmnDefinitions.setDMNDI(JSIDMNDI.newInstance());
        }
        return dmnDefinitions.getDMNDI().getDMNDiagram();
    }
}
