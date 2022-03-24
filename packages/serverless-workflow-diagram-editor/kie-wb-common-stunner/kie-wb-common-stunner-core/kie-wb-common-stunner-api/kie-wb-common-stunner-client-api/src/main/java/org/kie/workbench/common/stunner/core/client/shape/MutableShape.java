/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.client.shape;

import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;

/**
 * An extension of the Shape type that can be updated at runtime as per some bean.
 * <p/>
 * Once the bean instance, which is being represented by this shape, is being
 * updated, this shape can change its view attributes in order to display whatever new property values
 * display.
 * @param <W> The bean type.
 * @param <V> The Shape View type.
 */
public interface MutableShape<W, V extends ShapeView> extends Shape<V> {

    /**
     * Update shape view a atrributes as the bean's state.
     * @param element The bean.
     * @param mutationContext The mutation context.
     */
    void applyProperties(final W element,
                         final MutationContext mutationContext);
}
