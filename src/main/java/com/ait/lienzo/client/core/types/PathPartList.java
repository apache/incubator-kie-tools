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

package com.ait.lienzo.client.core.types;

import com.google.gwt.core.client.JsArray;

public final class PathPartList
{
    private final PathPartListJSO m_jso = PathPartListJSO.make();

    public PathPartList()
    {
    }

    public final void push(PathPartEntryJSO part)
    {
        m_jso.push(part);
    }

    public final PathPartEntryJSO get(int i)
    {
        return m_jso.get(i);
    }

    public final int size()
    {
        return m_jso.length();
    }

    public final void clear()
    {
        m_jso.clear();
    }
    
    public final PathPartListJSO getJSO()
    {
        return m_jso;
    }

    public static final class PathPartListJSO extends JsArray<PathPartEntryJSO>
    {
        public static final PathPartListJSO make()
        {
            return JsArray.createArray().cast();
        }

        protected PathPartListJSO()
        {
        }

        public final native void clear()
        /*-{
            this.length = length;
        }-*/;
    }
}
