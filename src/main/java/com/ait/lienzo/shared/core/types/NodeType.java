/*
   Copyright (c) 2014 Ahome' Innovation Technologies. All rights reserved.

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

/**
 * NodeType is an extensible enumeration of top-level node types used in the Lienzo toolkit.
 */
public class NodeType
{
    public static final NodeType SCENE      = new NodeType("Scene", false);

    public static final NodeType LAYER      = new NodeType("Layer", false);

    public static final NodeType GROUP      = new NodeType("Group", true);

    public static final NodeType SHAPE      = new NodeType("Shape", true);

    public static final NodeType VIEWPORT   = new NodeType("Viewport", false);

    public static final NodeType GRID_LAYER = new NodeType("GridLayer", false);

    private final String         m_valu;

    private final boolean        m_prim;

    protected NodeType(String valu, boolean prim)
    {
        m_valu = valu;

        m_prim = prim;
    }

    @Override
    public final String toString()
    {
        return m_valu;
    }

    public final String getValue()
    {
        return m_valu;
    }

    public final boolean isPrimitive()
    {
        return m_prim;
    }
}
