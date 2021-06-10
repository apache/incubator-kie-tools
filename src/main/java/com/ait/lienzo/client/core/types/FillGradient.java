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

package com.ait.lienzo.client.core.types;

import jsinterop.annotations.JsType;

/**
 * Common interface for {@link LinearGradient}, {@link RadialGradient}
 * and {@link PatternGradient}
 */
public interface FillGradient
{
    String getType();

    LinearGradient asLinearGradient();

    RadialGradient asRadialGradient();

    PatternGradient asPatternGradient();

    @JsType
    class GradientJSO
    {
        public String type;

        protected GradientJSO()
        {
        }
    }
}
