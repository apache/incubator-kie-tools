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

/**
 * A class that allows for easy creation of a Sharpen Image Filter.
 */
public class SharpenImageDataFilter extends AbstractConvolveImageDataFilter<SharpenImageDataFilter> {

    public SharpenImageDataFilter() {
        this(SharpenType.HARD);
    }

    public SharpenImageDataFilter(double... matrix) {
        super(ImageFilterType.SharpenImageDataFilterType, matrix);
    }

    public SharpenImageDataFilter(SharpenType matrix) {
        this(matrix.getMatrix());
    }

    protected SharpenImageDataFilter(Object node) {
        super(ImageFilterType.SharpenImageDataFilterType, node);
    }

    public enum SharpenType {
        HARD(0.0, -1, 0, -1, 5, -1, 0, -1, 0),
        SOFT(0, -0.2, 0, -0.2, 1.8, -0.2, 0, -0.2, 0);

        private final double[] m_matrix;

        SharpenType(double... matrix) {
            m_matrix = matrix;
        }

        public final double[] getMatrix() {
            double copy[] = new double[m_matrix.length];

            for (int i = 0; i < m_matrix.length; i++) {
                copy[i] = m_matrix[i];
            }
            return copy;
        }
    }
}
