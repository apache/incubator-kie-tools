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

package com.ait.lienzo.client.core.util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import com.ait.lienzo.client.core.style.Style.Cursor;

public final class CursorMap {

    private static final CursorMap INSTANCE = new CursorMap();

    private final HashMap<String, Cursor> m_cursors = new HashMap<>();

    public static final CursorMap get() {
        return INSTANCE;
    }

    private CursorMap() {
        for (Cursor cursor : Cursor.values()) {
            m_cursors.put(cursor.getCssName(), cursor);
        }
    }

    public final Collection<String> keys() {
        return Collections.unmodifiableSet(m_cursors.keySet());
    }

    public final Collection<Cursor> values() {
        return Collections.unmodifiableCollection(m_cursors.values());
    }

    public final Cursor lookup(final String key) {
        return m_cursors.get(key);
    }
}
