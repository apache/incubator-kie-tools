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

package com.ait.lienzo.test.stub.overlays;

import com.ait.lienzo.client.core.types.PathPartEntryJSO;
import com.ait.lienzo.test.annotation.StubClass;

/**
 * In-memory array implementation stub for <code>com.ait.lienzo.client.core.types.PathPartList$PathPartListJSO</code>.
 * 
 * @author Roger Martinez
 * @since 1.0
 * 
 */
@StubClass("com.ait.lienzo.client.core.types.PathPartList$PathPartListJSO")
public class PathPartListJSO extends JsArray<PathPartEntryJSO>
{
    public static PathPartListJSO make()
    {
        return new PathPartListJSO();
    }

    protected PathPartListJSO()
    {
    }
}