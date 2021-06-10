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

import java.util.Objects;

public class ArrayValidator extends AbstractAttributeTypeValidator
{
	private final IAttributeTypeValidator m_elementTypeValidator;

	public ArrayValidator(final IAttributeTypeValidator elementTypeValidator)
	{
		this("Array", elementTypeValidator);
	}

	public ArrayValidator(final String typeName, final IAttributeTypeValidator elementTypeValidator)
	{
		super(typeName);

		m_elementTypeValidator = Objects.requireNonNull(elementTypeValidator);
	}

	@Override
	public void validate(final Object jval, final ValidationContext ctx) throws ValidationException
	{
		if (null == jval)
		{
			ctx.addBadTypeError(getTypeName());

			return;
		}

		// @FIXME serialization (mdp)
//		final JSONArray jarr = jval.isArray();
//
//		if (null == jarr)
//		{
//			ctx.addBadTypeError(getTypeName());
//		}
//		else
//		{
//			final int size = jarr.size();
//
//			for (int i = 0; i < size; i++)
//			{
//				ctx.pushIndex(i);
//
//				m_elementTypeValidator.validate(jarr.get(i), ctx);
//
//				ctx.pop();// index
//			}
//		}
	}
}
