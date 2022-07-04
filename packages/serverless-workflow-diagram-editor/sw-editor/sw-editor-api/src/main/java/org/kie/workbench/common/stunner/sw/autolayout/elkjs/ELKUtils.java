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

package org.kie.workbench.common.stunner.sw.autolayout.elkjs;

import elemental2.core.Global;
import elemental2.promise.Promise;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

public class ELKUtils {

    //General
    public static final boolean MERGE_EDGES = false;

    //TOPDOWN canvas layout
    public static final double CANVAS_TOPDOWN_BETWEEN_LAYERS_SPACING = 85d;
    public static final double CANVAS_TOPDOWN_BASE_SPACING = 70d;
    public static final double CANVAS_TOPDOWN_TOP_PADDING = 50d;
    public static final double CANVAS_TOPDOWN_LEFT_PADDING = 70d;
    public static final double CANVAS_TOPDOWN_BOTTOM_PADDING = 0d;
    public static final double CANVAS_TOPDOWN_RIGHT_PADDING = 0d;

    //LEFTTORIGHT canvas layout
    public static final double CANVAS_LEFTTORIGHT_BETWEEN_LAYERS_SPACING = 70d;
    public static final double CANVAS_LEFTTORIGHT_BASE_SPACING = 70d;
    public static final double CANVAS_LEFTTORIGHT_TOP_PADDING = 50d;
    public static final double CANVAS_LEFTTORIGHT_LEFT_PADDING = 70d;
    public static final double CANVAS_LEFTTORIGHT_BOTTOM_PADDING = 0d;
    public static final double CANVAS_LEFTTORIGHT_RIGHT_PADDING = 0d;

    //TOPDOWN container layout
    public static final double CONTAINER_TOPDOWN_BETWEEN_LAYERS_SPACING = 70d;
    public static final double CONTAINER_TOPDOWN_BASE_SPACING = 70d;
    public static final double CONTAINER_TOPDOWN_TOP_PADDING = 10d;
    public static final double CONTAINER_TOPDOWN_LEFT_PADDING = 10d;
    public static final double CONTAINER_TOPDOWN_BOTTOM_PADDING = 10d;
    public static final double CONTAINER_TOPDOWN_RIGHT_PADDING = 10d;

    //LEFTTORIGHT container layout
    public static final double CONTAINER_LEFTTORIGHT_BETWEEN_LAYERS_SPACING = 100d;
    public static final double CONTAINER_LEFTTORIGHT_BASE_SPACING = 15d;
    public static final double CONTAINER_LEFTTORIGHT_TOP_PADDING = 25d;
    public static final double CONTAINER_LEFTTORIGHT_LEFT_PADDING = 25d;
    public static final double CONTAINER_LEFTTORIGHT_BOTTOM_PADDING = 25d;
    public static final double CONTAINER_LEFTTORIGHT_RIGHT_PADDING = 25d;

    public static Promise<Object> processGraph(final Object graph) {
        ELK elk = new ELK();
        try {
            return elk.layout(graph);
        } catch (Exception e) {
            throw new UnsupportedOperationException(e.getMessage());
        } finally {
            elk = null;
        }
    }

    public static ELKNode parse(final Object graph) {
        JsPropertyMap<?> parsed = Js.cast(graph);
        return new ELKNode(Js.cast(parsed.get("id")),
                           Js.cast(parsed.get("layoutOptions")),
                           Js.cast(parsed.get("children")),
                           Js.cast(parsed.get("edges")));
    }

    public static Object getCanvasTopDownLayoutOptionsObject() {
        return buildLayoutOptions(ELKLayoutProperties.algorithmType.LAYERED.getValue(),
                                  ELKLayoutProperties.directionType.DOWN.getValue(),
                                  ELKLayoutProperties.edgeRoutingType.ORTHOGONAL.getValue(),
                                  ELKLayoutProperties.fixedAlignmentType.BALANCED.getValue(),
                                  ELKLayoutProperties.layeringStrategyType.INTERACTIVE.getValue(),
                                  ELKLayoutProperties.nodePlacementStrategyType.BRANDES_KOEPF.getValue(),
                                  ELKLayoutProperties.directionCongruencyType.ROTATION.getValue(),
                                  CANVAS_TOPDOWN_BETWEEN_LAYERS_SPACING,
                                  CANVAS_TOPDOWN_BASE_SPACING,
                                  CANVAS_TOPDOWN_TOP_PADDING,
                                  CANVAS_TOPDOWN_LEFT_PADDING,
                                  CANVAS_TOPDOWN_BOTTOM_PADDING,
                                  CANVAS_TOPDOWN_RIGHT_PADDING,
                                  MERGE_EDGES);
    }

    public static Object getCanvasLeftToRightDownLayoutOptionsObject() {
        return buildLayoutOptions(ELKLayoutProperties.algorithmType.LAYERED.getValue(),
                                  ELKLayoutProperties.directionType.RIGHT.getValue(),
                                  ELKLayoutProperties.edgeRoutingType.ORTHOGONAL.getValue(),
                                  ELKLayoutProperties.fixedAlignmentType.BALANCED.getValue(),
                                  ELKLayoutProperties.layeringStrategyType.INTERACTIVE.getValue(),
                                  ELKLayoutProperties.nodePlacementStrategyType.NETWORK_SIMPLEX.getValue(),
                                  ELKLayoutProperties.directionCongruencyType.READING_DIRECTION.getValue(),
                                  CANVAS_LEFTTORIGHT_BETWEEN_LAYERS_SPACING,
                                  CANVAS_LEFTTORIGHT_BASE_SPACING,
                                  CANVAS_LEFTTORIGHT_TOP_PADDING,
                                  CANVAS_LEFTTORIGHT_LEFT_PADDING,
                                  CANVAS_LEFTTORIGHT_BOTTOM_PADDING,
                                  CANVAS_LEFTTORIGHT_RIGHT_PADDING,
                                  MERGE_EDGES);
    }

    public static Object getContainerTopDownLayoutOptionsObject() {
        return buildLayoutOptions(ELKLayoutProperties.algorithmType.LAYERED.getValue(),
                                  ELKLayoutProperties.directionType.DOWN.getValue(),
                                  ELKLayoutProperties.edgeRoutingType.ORTHOGONAL.getValue(),
                                  ELKLayoutProperties.fixedAlignmentType.BALANCED.getValue(),
                                  ELKLayoutProperties.layeringStrategyType.INTERACTIVE.getValue(),
                                  ELKLayoutProperties.nodePlacementStrategyType.NETWORK_SIMPLEX.getValue(),
                                  ELKLayoutProperties.directionCongruencyType.READING_DIRECTION.getValue(),
                                  CONTAINER_TOPDOWN_BETWEEN_LAYERS_SPACING,
                                  CONTAINER_TOPDOWN_BASE_SPACING,
                                  CONTAINER_TOPDOWN_TOP_PADDING,
                                  CONTAINER_TOPDOWN_LEFT_PADDING,
                                  CONTAINER_TOPDOWN_BOTTOM_PADDING,
                                  CONTAINER_TOPDOWN_RIGHT_PADDING,
                                  MERGE_EDGES);
    }

    public static Object getContainerLeftToRightDownLayoutOptionsObject() {
        return buildLayoutOptions(ELKLayoutProperties.algorithmType.LAYERED.getValue(),
                                  ELKLayoutProperties.directionType.RIGHT.getValue(),
                                  ELKLayoutProperties.edgeRoutingType.ORTHOGONAL.getValue(),
                                  ELKLayoutProperties.fixedAlignmentType.BALANCED.getValue(),
                                  ELKLayoutProperties.layeringStrategyType.NETWORK_SIMPLEX.getValue(),
                                  ELKLayoutProperties.nodePlacementStrategyType.BRANDES_KOEPF.getValue(),
                                  ELKLayoutProperties.directionCongruencyType.READING_DIRECTION.getValue(),
                                  CONTAINER_LEFTTORIGHT_BETWEEN_LAYERS_SPACING,
                                  CONTAINER_LEFTTORIGHT_BASE_SPACING,
                                  CONTAINER_LEFTTORIGHT_TOP_PADDING,
                                  CONTAINER_LEFTTORIGHT_LEFT_PADDING,
                                  CONTAINER_LEFTTORIGHT_BOTTOM_PADDING,
                                  CONTAINER_LEFTTORIGHT_RIGHT_PADDING,
                                  MERGE_EDGES);
    }

    private static Object buildLayoutOptions(final String algorithm,
                                             final String direction,
                                             final String edgeRouting,
                                             final String fixedAlignment,
                                             final String layeringStrategy,
                                             final String nodePlacementStrategy,
                                             final String directionCongruency,
                                             final double nodeNodeBetweenLayersSpacing,
                                             final double baseValueSpacing,
                                             final double topPadding,
                                             final double leftPadding,
                                             final double bottomPadding,
                                             final double rightPadding,
                                             final boolean mergeEdges) {

        return Global.JSON.parse("{"
                                         + algorithm + ", "
                                         + direction + ", "
                                         + edgeRouting + ", "
                                         + fixedAlignment + ", "
                                         + layeringStrategy + ", "
                                         + nodePlacementStrategy + ", "
                                         + directionCongruency + ", "
                                         + ELKLayoutProperties.getBaseValueSpacing(baseValueSpacing) + ", "
                                         + ELKLayoutProperties.getNodeNodeBetweenLayersSpacing(nodeNodeBetweenLayersSpacing) + ", "
                                         + ELKLayoutProperties.getMergeEdges(mergeEdges) + ", "
                                         + ELKLayoutProperties.getPadding(topPadding,
                                                                          leftPadding,
                                                                          bottomPadding,
                                                                          rightPadding)
                                         + "}");
    }
}
