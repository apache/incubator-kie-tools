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
 * AnimationCallback implements the methods of {@link IAnimationCallback} 
 * that do nothing by default.
 */
public class AnimationCallback implements IAnimationCallback
{
    @Override
    public void onStart(IAnimation animation, IAnimationHandle handle)
    {
        // Override if needed to use
    }

    @Override
    public void onFrame(IAnimation animation, IAnimationHandle handle)
    {
        // Override if needed to use
    }

    @Override
    public void onClose(IAnimation animation, IAnimationHandle handle)
    {
        // Override if needed to use
    }
}
