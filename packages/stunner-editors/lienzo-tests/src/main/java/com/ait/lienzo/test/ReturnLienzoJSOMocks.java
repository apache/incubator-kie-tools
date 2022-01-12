/*
 * Copyright (c) 2017 Ahome' Innovation Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ait.lienzo.test;

import org.mockito.Mockito;
import org.mockito.internal.stubbing.defaultanswers.ReturnsMocks;
import org.mockito.invocation.InvocationOnMock;

/**
 * Util class used at runtime to create mock objects for the given classes.
 *
 * @author Roger Martinez
 * @See com.ait.lienzo.test.translator.LienzoJSOMockTranslatorInterceptor
 * @since 1.0
 */
public class ReturnLienzoJSOMocks extends ReturnsMocks {

    private static final long serialVersionUID = -750634041840544191L;

    public static Object invoke(final Class<?> returnType) {
        return Mockito.mock(returnType, new ReturnLienzoJSOMocks());
    }

    @Override
    public Object answer(final InvocationOnMock invocation) throws Throwable {
        Class<?> returnType = invocation.getMethod().getReturnType();
        if (returnType.getName().equals("void")) {
            return null;
        }
        return invocation.getMock();
    }
}
