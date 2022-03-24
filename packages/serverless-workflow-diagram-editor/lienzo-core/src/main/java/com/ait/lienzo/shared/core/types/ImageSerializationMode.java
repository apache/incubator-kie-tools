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

import java.util.List;

/**
 * Serialization mode of a Picture or Sprite.
 */
public enum ImageSerializationMode implements EnumWithValue {
    URL("url"),
    DATA_URL("data-url");

    private final String m_value;

    private static final EnumStringMap<ImageSerializationMode> LOOKUP_MAP = Statics.build(ImageSerializationMode.values());

    private ImageSerializationMode(final String value) {
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

    public static final ImageSerializationMode lookup(final String key) {
        return Statics.lookup(key, LOOKUP_MAP, URL);
    }

    public static final List<String> getKeys() {
        return Statics.getKeys(ImageSerializationMode.values());
    }

    public static final List<ImageSerializationMode> getValues() {
        return Statics.getValues(ImageSerializationMode.values());
    }
}
