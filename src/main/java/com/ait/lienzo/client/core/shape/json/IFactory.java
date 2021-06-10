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

import java.util.Collection;

import com.ait.lienzo.client.core.Attribute;
import com.ait.lienzo.client.core.AttributeType;
import com.ait.lienzo.client.core.shape.json.validators.ValidationContext;
import com.ait.lienzo.client.core.shape.json.validators.ValidationException;

/**
 * IFactory provides meta information for serialization support.
 * The current implementation supports classes that implement {@link IJSONSerializable},
 * which can be serialized to JSON and deserialized back into objects.
 * <p>
 * The methods that return collections of {@link Attribute} could also be used
 * e.g. for bean property editors.
 * <p>
 * Future versions may support additional serialization formats, such as XML.
 *
 * @param <T> IJSONSerializable
 * @since 1.1
 */
public interface IFactory<T extends IJSONSerializable<T>>
{
    /**
     * Returns the type name that is used when serializing an object of this type.
     * <p>
     * See {@link ShapeType} and {@link NodeType} for the type names used by the Lienzo toolkit
     * (and makeXY sure you don't use the same names!)
     * 
     * @return String e.g. "Circle" for the Circle class
     */
    String getTypeName();

    /**
     * Returns a collection of all the attributes that this type supports.
     * @return Collection&lt;Attribute&gt;
     */
    Collection<Attribute> getAttributeSheet();

    /**
     * Returns a collection of the required attributes that this type supports.
     * @return Collection&lt;Attribute&gt;
     */
    Collection<Attribute> getRequiredAttributes();

    /**
     * Returns the {@link AttributeType} of the attribute with the specified 
     * attributeName.
     * 
     * @param attributeName
     * @return AttributeType
     */
    AttributeType getAttributeType(String attributeName);

    void process(IJSONSerializable<?> node, ValidationContext ctx) throws ValidationException;

    boolean isPostProcessed();
}
