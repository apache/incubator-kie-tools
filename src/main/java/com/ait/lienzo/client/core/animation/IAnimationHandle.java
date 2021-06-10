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

import com.ait.lienzo.client.core.shape.Node;

/**
 * IAnimationHandle can be used to terminate an animation via the cancel method. 
 * It provides additional methods to indicate it is running ({@link #isRunning()}) and to start the animation ({@link #run()}) 
 * <p>
 * Note that the animate methods of Shapes and Group automatically call run for you.
 * 
 * @see AnimationManager
 */
public interface IAnimationHandle
{
    IAnimationHandle run();

    IAnimationHandle stop();

    Node<?> getNode();

    boolean isRunning();
}
