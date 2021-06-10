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

import com.ait.lienzo.client.core.AttributeType;

public class MultiTypeValidator extends AbstractAttributeTypeValidator
{
    private final AttributeType[] m_types;

    /**
     * 
     * @param typeDescription   E.g. "Color or Gradient"
     * @param types
     */
    public MultiTypeValidator(final String typeName, final AttributeType[] types)
    {
        super(typeName);

        m_types = types;
    }

    @Override
    // @FIXME serialization (mdp)
    public void validate(final Object jval, final ValidationContext ctx) throws ValidationException
    {
        for (AttributeType type : m_types)
        {
            boolean valid = true;

            final ValidationContext test = new ValidationContext().setStopOnError(false);

            try
            {
                type.validate(jval, test);
            }
            catch (ValidationException e)
            {
                valid = false;
            }
            if (test.getErrorCount() > 0)
            {
                valid = false;
            }
            if (valid)
            {
                return;// OK
            }
        }
        ctx.addBadTypeError(getTypeName());
    }
}
