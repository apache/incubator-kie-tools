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

package com.ait.lienzo.client.core.animation;

/**
 * IAnimateCallback has several methods that are invoked by an Animation during it's lifetime:
 * <ul>
 * <li>onStart - invoked when the animation starts
 * <li>onFrame - invoked once for every frame
 * <li>onClose - invoked when the animation ends
 * </ul>
 * 
 * Each callback method take several parameters:
 * <ul>
 * <li>{@link IAnimationHandle} - allows you to cancel the animation
 * </ul>
 * 
 * @see IAnimationHandle
 */
public interface IAnimationCallback
{
    public void onStart(IAnimation animation, IAnimationHandle handle);

    public void onFrame(IAnimation animation, IAnimationHandle handle);

    public void onClose(IAnimation animation, IAnimationHandle handle);
}