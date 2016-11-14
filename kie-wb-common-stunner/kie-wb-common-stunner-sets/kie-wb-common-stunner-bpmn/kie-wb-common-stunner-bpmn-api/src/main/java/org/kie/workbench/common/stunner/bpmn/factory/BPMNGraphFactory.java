/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.bpmn.factory;

import org.kie.workbench.common.stunner.core.factory.graph.GraphFactory;

/**
 * Custom BPMN factory for graphs.
 * The implementations can initialize the graph in whatever state you need.
 */
public interface BPMNGraphFactory extends GraphFactory {

    double GRAPH_DEFAULT_WIDTH = 1400d;
    double GRAPH_DEFAULT_HEIGHT = 600d;


}
