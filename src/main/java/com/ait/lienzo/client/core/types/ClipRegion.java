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

package com.ait.lienzo.client.core.types;

import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;

public final class ClipRegion
{
    private final double m_minx;

    private final double m_miny;

    private final double m_maxx;

    private final double m_maxy;

    public ClipRegion(final double minx, final double miny, final double maxx, final double maxy)
    {
        m_minx = minx;

        m_miny = miny;

        m_maxx = maxx;

        m_maxy = maxy;
    }

    public final double getMinX()
    {
        return m_minx;
    }

    public final double getMinY()
    {
        return m_miny;
    }

    public final double getMaxX()
    {
        return m_maxx;
    }

    public final double getMaxY()
    {
        return m_maxy;
    }

    public final String toJSONString()
    {
        JSONObject object = new JSONObject();

        object.put("minX", new JSONNumber(getMinX()));

        object.put("minY", new JSONNumber(getMinY()));

        object.put("maxX", new JSONNumber(getMaxX()));

        object.put("maxY", new JSONNumber(getMaxY()));

        return object.toString();
    }

    @Override
    public String toString()
    {
        return toJSONString();
    }

    @Override
    public boolean equals(Object other)
    {
        if ((other == null) || (false == (other instanceof ClipRegion)))
        {
            return false;
        }
        if (this == other)
        {
            return true;
        }
        ClipRegion that = ((ClipRegion) other);

        return ((that.getMinX() == getMinX()) && (that.getMinY() == getMinY()) && (that.getMaxX() == getMaxX()) && (that.getMaxY() == getMaxY()));
    }

    @Override
    public int hashCode()
    {
        return toJSONString().hashCode();
    }
}
