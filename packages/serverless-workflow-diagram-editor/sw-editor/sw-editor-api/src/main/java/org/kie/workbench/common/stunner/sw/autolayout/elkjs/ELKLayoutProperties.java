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

public class ELKLayoutProperties {

    public enum algorithmType {
        BOX {
            public String getValue() {
                return "\"algorithm\": \"box\"";
            }
        },
        DISCO {
            public String getValue() {
                return "\"algorithm\": \"disco\"";
            }
        },
        FIXED {
            public String getValue() {
                return "\"algorithm\": \"fixed\"";
            }
        },
        FORCE {
            public String getValue() {
                return "\"algorithm\": \"force\"";
            }
        },
        LAYERED {
            public String getValue() {
                return "\"algorithm\": \"layered\"";
            }
        },
        MRTREE {
            public String getValue() {
                return "\"algorithm\": \"mrtree\"";
            }
        },
        RADIAL {
            public String getValue() {
                return "\"algorithm\": \"radial\"";
            }
        },
        RANDOM {
            public String getValue() {
                return "\"algorithm\": \"random\"";
            }
        },
        RECTPACKING {
            public String getValue() {
                return "\"algorithm\": \"rectpacking\"";
            }
        },
        SPORE_COMPACTION {
            public String getValue() {
                return "\"algorithm\": \"sporeCompaction\"";
            }
        },
        SPORE_OVERLAP {
            public String getValue() {
                return "\"algorithm\": \"sporeOverlap\"";
            }
        },
        STRESS {
            public String getValue() {
                return "\"algorithm\": \"stress\"";
            }
        };

        public abstract String getValue();
    }

    public enum directionType {
        UP {
            public String getValue() {
                return "\"elk.direction\": \"UP\"";
            }
        },
        DOWN {
            public String getValue() {
                return "\"elk.direction\": \"DOWN\"";
            }
        },
        LEFT {
            public String getValue() {
                return "\"elk.direction\": \"LEFT\"";
            }
        },
        RIGHT {
            public String getValue() {
                return "\"elk.direction\": \"RIGHT\"";
            }
        },
        UNDEFINED {
            public String getValue() {
                return "\"elk.direction\": \"UNDEFINED\"";
            }
        };

        public abstract String getValue();
    }

    public enum edgeRoutingType {
        ORTHOGONAL {
            public String getValue() {
                return "\"elk.edgeRouting\": \"ORTHOGONAL\"";
            }
        },
        POLYLINE {
            public String getValue() {
                return "\"elk.edgeRouting\": \"POLYLINE\"";
            }
        },
        SPLINES {
            public String getValue() {
                return "\"elk.edgeRouting\": \"SPLINES\"";
            }
        },
        UNDEFINED {
            public String getValue() {
                return "\"elk.edgeRouting\": \"UNDEFINED\"";
            }
        };

        public abstract String getValue();
    }

    public enum fixedAlignmentType {
        BALANCED {
            public String getValue() {
                return "\"nodePlacement.bk.fixedAlignment\": \"BALANCED\"";
            }
        },
        LEFTDOWN {
            public String getValue() {
                return "\"nodePlacement.bk.fixedAlignment\": \"LEFTDOWN\"";
            }
        },
        LEFTUP {
            public String getValue() {
                return "\"nodePlacement.bk.fixedAlignment\": \"LEFTUP\"";
            }
        },
        NONE {
            public String getValue() {
                return "\"nodePlacement.bk.fixedAlignment\": \"NONE\"";
            }
        },
        RIGHTDOWN {
            public String getValue() {
                return "\"nodePlacement.bk.fixedAlignment\": \"RIGHTDOWN\"";
            }
        },
        RIGHTUP {
            public String getValue() {
                return "\"nodePlacement.bk.fixedAlignment\": \"RIGHTUP\"";
            }
        };

        public abstract String getValue();
    }

    public enum layeringStrategyType {
        NETWORK_SIMPLEX {
            public String getValue() {
                return "\"elk.layered.layering.strategy\": \"NETWORK_SIMPLEX\"";
            }
        },
        LONGEST_PATH {
            public String getValue() {
                return "\"elk.layered.layering.strategy\": \"LONGEST_PATH\"";
            }
        },
        COFFMAN_GRAHAM {
            public String getValue() {
                return "\"elk.layered.layering.strategy\": \"COFFMAN_GRAHAM\"";
            }
        },
        INTERACTIVE {
            public String getValue() {
                return "\"elk.layered.layering.strategy\": \"INTERACTIVE\"";
            }
        },
        STRETCH_WIDTH {
            public String getValue() {
                return "\"elk.layered.layering.strategy\": \"STRETCH_WIDTH\"";
            }
        },
        MIN_WIDTH {
            public String getValue() {
                return "\"elk.layered.layering.strategy\": \"MIN_WIDTH\"";
            }
        };

        public abstract String getValue();
    }

    public enum nodePlacementStrategyType {
        SIMPLE {
            public String getValue() {
                return "\"elk.layered.nodePlacement.strategy\": \"SIMPLE\"";
            }
        },
        INTERACTIVE {
            public String getValue() {
                return "\"elk.layered.nodePlacement.strategy\": \"INTERACTIVE\"";
            }
        },
        LINEAR_SEGMENTS {
            public String getValue() {
                return "\"elk.layered.nodePlacement.strategy\": \"LINEAR_SEGMENTS\"";
            }
        },
        BRANDES_KOEPF {
            public String getValue() {
                return "\"elk.layered.nodePlacement.strategy\": \"BRANDES_KOEPF\"";
            }
        },
        NETWORK_SIMPLEX {
            public String getValue() {
                return "\"elk.layered.nodePlacement.strategy\": \"NETWORK_SIMPLEX\"";
            }
        };

        public abstract String getValue();
    }

    public enum directionCongruencyType {
        ROTATION {
            public String getValue() {
                return "\"directionCongruency\":\"ROTATION\"";
            }
        },
        READING_DIRECTION {
            public String getValue() {
                return "\"directionCongruency\":\"READING_DIRECTION\"";
            }
        };

        public abstract String getValue();
    }

    public static String getNodeNodeBetweenLayersSpacing(final double nodeHorizontalSpacing) {
        return "\"spacing.nodeNodeBetweenLayers\": " + nodeHorizontalSpacing;
    }

    public static String getBaseValueSpacing(final double nodeVerticalSpacing) {
        return "\"spacing.baseValue\": " + nodeVerticalSpacing;
    }

    public static String getMergeEdges(final boolean mergeEdges) {
        return "\"mergeEdges\":" + mergeEdges;
    }

    public static String getPadding(final double topPadding,
                                    final double leftPadding,
                                    final double bottomPadding,
                                    final double rightPadding) {
        return "\"elk.padding\": "
                + "\"[top=" + topPadding
                + ",left=" + leftPadding
                + ",bottom=" + bottomPadding
                + ",right=" + rightPadding
                + "]\"";
    }
}
