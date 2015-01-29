/*
   Copyright (c) 2014,2015 Ahome' Innovation Technologies. All rights reserved.

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

package com.ait.lienzo.client.core.event;

import com.ait.lienzo.client.core.Attribute;
import com.ait.lienzo.client.core.AttributeMapper;
import com.ait.lienzo.client.core.shape.Node;
import com.google.gwt.event.shared.GwtEvent;

public class NodeAttributeChangedEvent extends GwtEvent<NodeAttributeChangedHandler>
{
    private static final Type<NodeAttributeChangedHandler> TYPE = new Type<NodeAttributeChangedHandler>();

    private final String                                   m_name;

    private final Node<?>                                  m_node;

    public static Type<NodeAttributeChangedHandler> getType()
    {
        return TYPE;
    }

    public NodeAttributeChangedEvent(Node<?> node, String name)
    {
        m_node = node;

        m_name = name;
    }
    
    public Node<?> getNode()
    {
        return m_node;
    }
    
    public String getName()
    {
        return m_name;
    }
    
    public Attribute getAttribute()
    {
        return AttributeMapper.get().find(getName());
    }

    @Override
    public final Type<NodeAttributeChangedHandler> getAssociatedType()
    {
        return TYPE;
    }

    @Override
    protected void dispatch(NodeAttributeChangedHandler handler)
    {
        handler.onNodeAttributeChanged(this);
    }
}
