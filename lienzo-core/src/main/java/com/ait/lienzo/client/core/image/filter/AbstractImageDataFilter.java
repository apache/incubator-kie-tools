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

package com.ait.lienzo.client.core.image.filter;

import com.ait.lienzo.shared.core.types.ImageFilterType;
import jsinterop.annotations.JsProperty;

public abstract class AbstractImageDataFilter<T extends AbstractImageDataFilter<T>> implements ImageDataFilter<T> {

    private final ImageFilterType m_type;

    @JsProperty
    private boolean active = true;

    protected AbstractImageDataFilter(final ImageFilterType type) {
        m_type = type;

        setActive(true);
    }

    protected AbstractImageDataFilter(final ImageFilterType type, final Object node) {
        m_type = type;

        if (null == node) {
            return;
        }
    }

    @Override
    public boolean isTransforming() {
        return false;
    }

    @Override
    public boolean isActive() {
        return this.active;
    }

    @Override
    public void setActive(final boolean active) {
        this.active = active;
    }

    @Override
    public final ImageFilterType getType() {
        return m_type;
    }

    @SuppressWarnings("unchecked")
    protected final T cast() {
        return (T) this;
    }
}
