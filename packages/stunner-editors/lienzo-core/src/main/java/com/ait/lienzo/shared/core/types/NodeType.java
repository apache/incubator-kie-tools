/*
   Copyright (c) 2017 Ahome' Innovation Technologies. All rights reserved.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package com.ait.lienzo.shared.core.types;

import com.ait.lienzo.tools.common.api.types.IStringValued;

/**
 * NodeType is an extensible enumeration of top-level node types used in the Lienzo toolkit.
 */
public class NodeType implements IStringValued {

    public static final NodeType SCENE = new NodeType("Scene");

    public static final NodeType LAYER = new NodeType("Layer");

    public static final NodeType GROUP = new NodeType("Group");

    public static final NodeType SHAPE = new NodeType("Shape");

    public static final NodeType PROXY = new NodeType("Proxy");

    public static final NodeType VIEWPORT = new NodeType("Viewport");

    public static final NodeType GRID_LAYER = new NodeType("GridLayer");

    private final String m_value;

    protected NodeType(final String value) {
        m_value = value;
    }

    @Override
    public final String getValue() {
        return m_value;
    }

    @Override
    public final String toString() {
        return m_value;
    }

    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof NodeType)) {
            return false;
        }
        if (this == other) {
            return true;
        }
        return ((NodeType) other).getValue().equals(getValue());
    }

    @Override
    public int hashCode() {
        return getValue().hashCode();
    }

    public NodeType copy() {
        return new NodeType(m_value);
    }
}
