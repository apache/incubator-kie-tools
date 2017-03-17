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

/**
 * IColor is a common interface for {@link Color} and {@link ColorName}.
 * 
 */
public interface IColor
{
    /**
     * Returns the Red component of the RGB color.
     * 
     * @return int between 0 and 255
     */
    public int getR();

    /**
     * Returns the Green component of the RGB color.
     * 
     * @return int between 0 and 255
     */
    public int getG();

    /**
     * Returns the Blue component of the RGB color.
     * 
     * @return int between 0 and 255
     */
    public int getB();

    /**
     * Returns the Alpha component (transparency) of the RGB color, between 0 and 1.
     * 
     * @return double between 0 and 1
     */
    public double getA();

    /**
     * Returns a CCS compliant color string that can be set as a color on
     * an HTML5 canvas.
     * 
     * @return String e.g. "red", "rgb(255,255,255)", "rgba(255,255,255,1)"
     */
    public String getColorString();
}
