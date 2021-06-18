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

import com.ait.lienzo.client.core.Attribute;
import com.ait.lienzo.client.core.shape.json.AbstractFactory;
import com.ait.lienzo.client.core.shape.json.validators.ValidationContext;
import com.ait.lienzo.client.core.shape.json.validators.ValidationException;
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

    protected AbstractImageDataFilter(final ImageFilterType type, final Object node, final ValidationContext ctx) throws ValidationException {
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

    protected static abstract class ImageDataFilterFactory<T extends ImageDataFilter<T>> extends AbstractFactory<T> {

        protected ImageDataFilterFactory(final ImageFilterType type) {
            super(type.getValue());

            addAttribute(Attribute.ACTIVE, true);
        }
    }
}
