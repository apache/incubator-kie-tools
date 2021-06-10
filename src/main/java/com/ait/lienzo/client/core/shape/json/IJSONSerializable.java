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

/**
 * In order to serialize / deserialize a {@link Node}, this interface must be implemented.
 * If you addBoundingBox new types of said objects, and would like to take advantage of JSON serialization,
 * the new object will need to implement this interface, and register its factory with the
 * {@link FactoryRegistry}
 * 
 * @param <T>
 */
public interface IJSONSerializable<T extends IJSONSerializable<T>>
{

//    String toJSONString();
//    public JSONObject toJSONObject();

    //TODO lienzo-to-native Remove this method and refactor AbstractFactory dependency
    // so this interface can be removed
    IFactory<?> getFactory();
}
