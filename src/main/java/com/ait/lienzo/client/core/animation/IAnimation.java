/*
   Copyright (c) 2014,2015 Ahome' Innovation Technologies. All rights reserved.

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

public interface IAnimation
{
    public static final double INDEFINITE_ANIMATION = -1;

    public double getPercent();

    public double getDuration();

    public IAnimation doStart();

    public IAnimation doFrame();

    public IAnimation doClose();

    public Node<?> getNode();

    public IAnimation setNode(Node<?> node);
}
