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

public enum CompositeOperation implements EnumWithValue
{
    /**
     * A (B is ignored). Display the source image instead of the destination
     * image.
     */
    COPY("copy"),

    /**
     * B atop A. Same as source-atop but using the destination image instead of
     * the source image and vice versa.
     */
    DESTINATION_ATOP("destination-atop"),

    /**
     * B in A. Same as source-in but using the destination image instead of the
     * source image and vice versa.
     */
    DESTINATION_IN("destination-in"),

    /**
     * B out A. Same as source-out but using the destination image instead of the
     * source image and vice versa.
     */
    DESTINATION_OUT("destination-out"),

    /**
     * B over A. Same as source-over but using the destination image instead of
     * the source image and vice versa.
     */
    DESTINATION_OVER("destination-over"),

    /**
     * A plus B. Display the sum of the source image and destination image, with
     * color values approaching 1 as a limit.
     */
    LIGHTER("lighter"),

    /**
     * A atop B. Display the source image wherever both images are opaque. Display
     * the destination image wherever the destination image is opaque but the
     * source image is transparent. Display transparency elsewhere.
     */
    SOURCE_ATOP("source-atop"),

    /**
     * A in B. Display the source image wherever both the source image and
     * destination image are opaque. Display transparency elsewhere.
     */
    SOURCE_IN("source-in"),

    /**
     * A out B. Display the source image wherever the source image is opaque and
     * the destination image is transparent. Display transparency elsewhere.
     */
    SOURCE_OUT("source-out"),

    /**
     * A over B. Display the source image wherever the source image is opaque.
     * Display the destination image elsewhere.
     */
    SOURCE_OVER("source-over"),

    /**
     * A xor B. Exclusive OR of the source image and destination image.
     */
    XOR("xor");

    private final String m_value;

    private static final EnumStringMap<CompositeOperation> LOOKUP_MAP = Statics.build(CompositeOperation.values());

    private CompositeOperation(final String value)
    {
        m_value = value;
    }

    @Override
    public final String getValue()
    {
        return m_value;
    }

    @Override
    public final String toString()
    {
        return m_value;
    }

    public static final CompositeOperation lookup(final String key)
    {
        return Statics.lookup(key, LOOKUP_MAP, SOURCE_OVER);
    }

    public static final List<String> getKeys()
    {
        return Statics.getKeys(CompositeOperation.values());
    }

    public static final List<CompositeOperation> getValues()
    {
        return Statics.getValues(CompositeOperation.values());
    }
}
