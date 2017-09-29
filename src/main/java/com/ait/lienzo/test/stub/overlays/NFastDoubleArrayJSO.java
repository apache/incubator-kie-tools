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

package com.ait.lienzo.test.stub.overlays;

import com.ait.lienzo.test.annotation.StubClass;

@StubClass("com.ait.tooling.nativetools.client.collection.NFastDoubleArrayJSO")
public class NFastDoubleArrayJSO extends NFastPrimitiveArrayBaseJSO<NFastDoubleArrayJSO>
{
    public static NFastDoubleArrayJSO make(final double d, final double... list)
    {
        final NFastDoubleArrayJSO jso = make();

        jso.push(d, list);

        return jso;
    }

    public static NFastDoubleArrayJSO make()
    {
        return new NFastDoubleArrayJSO();
    }

    protected NFastDoubleArrayJSO()
    {
    }

    public double[] toArray()
    {
        final int size = size();

        final double[] array = new double[size];

        for (int i = 0; i < size; i++)
        {
            array[i] = get(i);
        }
        return array;
    }

    public void push(final double d, final double... list)
    {
        push(d);

        final int size = list.length;

        for (int i = 0; i < size; i++)
        {
            push(list[i]);
        }
    }

    public void push(final double value)
    {
        list.add(value);
    }

    public void set(final int indx, final double value)
    {
        list.set(indx, value);
    }

    public double get(final int indx)
    {
        return (double) list.get(indx);
    }

    public double pop()
    {
        double result = 0;

        if (!list.isEmpty())
        {
            final int i = list.size() - 1;

            result = (double) list.get(i);

            list.remove(i);
        }
        return result;
    }

    public double shift()
    {
        return doShift();
    }

    public boolean contains(final double value)
    {
        return list.contains(value);
    }
}
