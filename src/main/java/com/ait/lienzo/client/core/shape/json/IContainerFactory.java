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

package com.ait.lienzo.client.core.shape.json;

import com.ait.lienzo.client.core.shape.IContainer;
import com.ait.lienzo.client.core.shape.Node;
import com.ait.lienzo.client.core.shape.json.validators.ValidationContext;

/**
 * IContainerFactory should be implemented by {@link IFactory factories} 
 * for objects that may contain child nodes.
 * 
 * @since 1.1
 */
public interface IContainerFactory
{
    /**
     * Returns whether the specified childNode is valid for this container, and if so, addBoundingBox it.
     * 
     * @param childNode IJSONSerializable
     */

    public boolean addNodeForContainer(IContainer<?, ?> container, Node<?> node, ValidationContext ctx);
}
