/*
 * Copyright (c) 2017 Ahome' Innovation Technologies. All rights reserved.
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

package com.ait.lienzo.test.stub.overlays;

import com.ait.lienzo.test.annotation.StubClass;
import com.ait.tooling.common.api.java.util.UUID;
import com.ait.tooling.nativetools.client.collection.MetaData;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.shared.HandlerManager;

@StubClass("com.ait.lienzo.client.core.shape.Node$OptionalNodeFields")
public class OptionalNodeFields extends JavaScriptObject
{
    private String         m_uuid;

    private MetaData       m_meta;

    private Object         m_user;

    private HandlerManager m_hand;

    private int            m_anim;

    public static final OptionalNodeFields make()
    {
        return new OptionalNodeFields();
    }

    protected OptionalNodeFields()
    {
    }

    public String uuid()
    {
        if (null == m_uuid)
        {
            m_uuid = UUID.uuid();
        }
        return m_uuid;
    }

    public boolean hasMetaData()
    {
        return (null != m_meta);
    }

    public MetaData getMetaData()
    {
        if (null == m_meta)
        {
            setMetaData(new MetaData());
        }
        return m_meta;
    }

    public MetaData setMetaData(final MetaData meta)
    {
        m_meta = meta;

        return m_meta;
    }

    public Object getUserData()
    {
        return m_user;
    }

    public void setUserData(Object data)
    {
        m_user = data;
    }

    public HandlerManager getHandlerManager()
    {
        if (null == m_hand)
        {
            setHandlerManager(new HandlerManager(this));
        }
        return m_hand;
    }

    public void setHandlerManager(HandlerManager hand)
    {
        m_hand = hand;
    }

    public boolean isAnimating()
    {
        return (m_anim > 0);
    }

    public void doAnimating()
    {
        m_anim++;
    }

    public void unAnimating()
    {
        if (isAnimating())
        {
            m_anim--;
        }
    }
}
