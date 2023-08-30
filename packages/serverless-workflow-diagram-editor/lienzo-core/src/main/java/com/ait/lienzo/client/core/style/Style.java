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


package com.ait.lienzo.client.core.style;

public class Style {

    public enum BorderStyle {
        NONE {
            public String getCssName() {
                return "none";
            }
        },
        DOTTED {
            public String getCssName() {
                return "dotted";
            }
        },
        DASHED {
            public String getCssName() {
                return "dashed";
            }
        },
        HIDDEN {
            public String getCssName() {
                return "hidden";
            }
        },
        SOLID {
            public String getCssName() {
                return "solid";
            }
        };

        public abstract String getCssName();
    }

    public enum Cursor {
        DEFAULT {
            public String getCssName() {
                return "default";
            }
        },
        AUTO {
            public String getCssName() {
                return "auto";
            }
        },
        CROSSHAIR {
            public String getCssName() {
                return "crosshair";
            }
        },
        POINTER {
            public String getCssName() {
                return "pointer";
            }
        },
        MOVE {
            public String getCssName() {
                return "move";
            }
        },
        E_RESIZE {
            public String getCssName() {
                return "e-resize";
            }
        },
        NE_RESIZE {
            public String getCssName() {
                return "ne-resize";
            }
        },
        NW_RESIZE {
            public String getCssName() {
                return "nw-resize";
            }
        },
        N_RESIZE {
            public String getCssName() {
                return "n-resize";
            }
        },
        SE_RESIZE {
            public String getCssName() {
                return "se-resize";
            }
        },
        SW_RESIZE {
            public String getCssName() {
                return "sw-resize";
            }
        },
        S_RESIZE {
            public String getCssName() {
                return "s-resize";
            }
        },
        W_RESIZE {
            public String getCssName() {
                return "w-resize";
            }
        },
        TEXT {
            public String getCssName() {
                return "text";
            }
        },
        WAIT {
            public String getCssName() {
                return "wait";
            }
        },
        HELP {
            public String getCssName() {
                return "help";
            }
        },
        COL_RESIZE {
            public String getCssName() {
                return "col-resize";
            }
        },
        ROW_RESIZE {
            public String getCssName() {
                return "row-resize";
            }
        },
        NOT_ALLOWED {
            public String getCssName() {
                return "not-allowed";
            }
        },
        ZOOM_IN {
            public String getCssName() {
                return "zoom-in";
            }
        },
        ZOOM_OUT {
            public String getCssName() {
                return "zoom-out";
            }
        },
        GRAB {
            public String getCssName() {
                return "grab";
            }
        },
        GRABBING {
            public String getCssName() {
                return "grabbing";
            }
        };

        public abstract String getCssName();
    }

    public enum Display {
        NONE {
            public String getCssName() {
                return "none";
            }
        },
        BLOCK {
            public String getCssName() {
                return "block";
            }
        },
        INLINE {
            public String getCssName() {
                return "inline";
            }
        },
        INLINE_BLOCK {
            public String getCssName() {
                return "inline-block";
            }
        },
        INLINE_TABLE {
            public String getCssName() {
                return "inline-table";
            }
        },
        LIST_ITEM {
            public String getCssName() {
                return "list-item";
            }
        },
        RUN_IN {
            public String getCssName() {
                return "run-in";
            }
        },
        TABLE {
            public String getCssName() {
                return "table";
            }
        },
        TABLE_CAPTION {
            public String getCssName() {
                return "table-caption";
            }
        },
        TABLE_COLUMN_GROUP {
            public String getCssName() {
                return "table-column-group";
            }
        },
        TABLE_HEADER_GROUP {
            public String getCssName() {
                return "table-header-group";
            }
        },
        TABLE_FOOTER_GROUP {
            public String getCssName() {
                return "table-footer-group";
            }
        },
        TABLE_ROW_GROUP {
            public String getCssName() {
                return "table-row-group";
            }
        },
        TABLE_CELL {
            public String getCssName() {
                return "table-cell";
            }
        },
        TABLE_COLUMN {
            public String getCssName() {
                return "table-column";
            }
        },
        TABLE_ROW {
            public String getCssName() {
                return "table-row";
            }
        },
        INITIAL {
            public String getCssName() {
                return "initial";
            }
        },
        FLEX {
            public String getCssName() {
                return "flex";
            }
        },
        INLINE_FLEX {
            public String getCssName() {
                return "inline-flex";
            }
        };

        public abstract String getCssName();
    }

    public enum OutlineStyle {
        NONE {
            public String getCssName() {
                return "none";
            }
        },
        DASHED {
            public String getCssName() {
                return "dashed";
            }
        },
        DOTTED {
            public String getCssName() {
                return "dotted";
            }
        },
        DOUBLE {
            public String getCssName() {
                return "double";
            }
        },
        GROOVE {
            public String getCssName() {
                return "groove";
            }
        },
        INSET {
            public String getCssName() {
                return "inset";
            }
        },
        OUTSET {
            public String getCssName() {
                return "outset";
            }
        },
        RIDGE {
            public String getCssName() {
                return "ridge";
            }
        },
        SOLID {
            public String getCssName() {
                return "solid";
            }
        };

        public abstract String getCssName();
    }

    public enum Overflow {
        VISIBLE {
            public String getCssName() {
                return "visible";
            }
        },
        HIDDEN {
            public String getCssName() {
                return "hidden";
            }
        },
        SCROLL {
            public String getCssName() {
                return "scroll";
            }
        },
        AUTO {
            public String getCssName() {
                return "auto";
            }
        };

        public abstract String getCssName();
    }

    public enum Position {
        STATIC {
            public String getCssName() {
                return "static";
            }
        },
        RELATIVE {
            public String getCssName() {
                return "relative";
            }
        },
        ABSOLUTE {
            public String getCssName() {
                return "absolute";
            }
        },
        FIXED {
            public String getCssName() {
                return "fixed";
            }
        };

        public abstract String getCssName();
    }

    public enum Unit {
        PX {
            public String getType() {
                return "px";
            }
        },
        PCT {
            public String getType() {
                return "%";
            }
        },
        EM {
            public String getType() {
                return "em";
            }
        },
        EX {
            public String getType() {
                return "ex";
            }
        },
        PT {
            public String getType() {
                return "pt";
            }
        },
        PC {
            public String getType() {
                return "pc";
            }
        },
        IN {
            public String getType() {
                return "in";
            }
        },
        CM {
            public String getType() {
                return "cm";
            }
        },
        MM {
            public String getType() {
                return "mm";
            }
        };

        public abstract String getType();
    }

    public enum Visibility {
        VISIBLE {
            public String getCssName() {
                return "visible";
            }
        },
        HIDDEN {
            public String getCssName() {
                return "hidden";
            }
        };

        public abstract String getCssName();
    }
}
