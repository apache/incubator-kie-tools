/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.client.canvas;

public class TransformImpl implements Transform {

    private final double[] translate;
    private final double[] scale;

    TransformImpl( final double[] translate, final double[] scale ) {
        this.translate = translate;
        this.scale = scale;
    }

    @Override
    public double[] getTranslate() {
        return translate;
    }

    @Override
    public double[] getScale() {
        return scale;
    }

    @Override
    public double[] transform( final double x,
                               final double y ) {
        return new double[] {
                ( x * scale[0] ) + translate[0],
                ( y * scale[1] ) + translate[1]

        };
    }
}
