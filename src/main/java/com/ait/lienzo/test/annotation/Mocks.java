/*
 * Copyright (c) 2014,2015,2016 Ahome' Innovation Technologies. All rights reserved.
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

package com.ait.lienzo.test.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Lienzo's JSO fully qualified class names to be mocked. 
 * 
 * NOTE: Use <code>$</code> as the inner class separator character on the fqcn.
 * 
 * @See com.ait.lienzo.test.annotation.Settings#mocks
 * 
 * @author Roger Martinez
 * @since 1.0
 * 
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface Mocks
{
    String[] value();
}
