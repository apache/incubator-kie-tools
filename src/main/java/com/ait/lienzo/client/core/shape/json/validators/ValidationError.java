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

package com.ait.lienzo.client.core.shape.json.validators;

/**
 * ValidationError defines an error that was found during the 
 * deserialization process of a Node from a JSON string.
 * 
 * 
 * @see JSONDeserializer
 * @see ValidationContext
 */
public class ValidationError
{
    private final String m_context;

    private final String m_message;

    /**
     * Constructor for a ValidationError.
     * 
     * @param message the error message
     * @param context the context string that indicates which node or attribute was in error,
     * e.g. ".children[4].children[2].attributes.fillColor"
     */
    public ValidationError(final String message, final String context)
    {
        m_message = message;

        m_context = context;
    }

    /**
     * Returns the context string that indicates which node or attribute was in error,
     * e.g. ".children[4].children[2].attributes.fillColor"
     * 
     * @return String
     */
    public String getContext()
    {
        return m_context;
    }

    /**
     * Returns the error message.
     * 
     * @return String
     */
    public String getMessage()
    {
        return m_message;
    }
}
