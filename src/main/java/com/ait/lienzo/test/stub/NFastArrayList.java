/*
 *
 *    Copyright (c) 2014,2015,2016 Ahome' Innovation Technologies. All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *  
 */

package com.ait.lienzo.test.stub;

import com.ait.lienzo.test.annotation.StubClass;
import com.ait.lienzo.test.util.LienzoMockitoLogger;

import java.util.*;

/**
 * In-memory array list implementation stub for class <code>com.ait.tooling.nativetools.client.collection.NFastArrayList</code>.
 * 
 * Results easier creating this stub class for this wrapper of FastArrayListJSO than creating concrete stubs for NFastArrayListJSO and
 * its super classes.
 * 
 * @author Roger Martinez
 * @since 1.0
 * 
 */
@StubClass("com.ait.tooling.nativetools.client.collection.NFastArrayList")
public class NFastArrayList <M> implements Iterable<M>
{
    private static final class FastArrayListJSO <M> extends com.ait.tooling.nativetools.client.NArrayBaseJSO<FastArrayListJSO<M>>
    {
    }

    private final ArrayList<M> list = new ArrayList<M>();

    public NFastArrayList()
    {
        LienzoMockitoLogger.log("NFastArrayList", "Creating custom Lienzo overlay type.");
    }

    @SuppressWarnings("unchecked")
    public NFastArrayList(final M value, final M... values)
    {
        this();

        add(value);

        if ((null != values) && (values.length > 0))
        {
            for (int i = 0; i < values.length; i++)
            {
                add(values[i]);
            }
        }
    }

    private NFastArrayList(final FastArrayListJSO<M> jso)
    {
        this();
    }

    public boolean isEmpty()
    {
        return list.isEmpty();
    }

    public int size()
    {
        return list.size();
    }

    public M get(final int index)
    {
        if ((index >= 0) && (index < size()))
        {
            return list.get(index);
        }
        return null;
    }

    public NFastArrayList<M> add(final M value)
    {
        list.add(value);

        return this;
    }

    public NFastArrayList<M> set(final int i, final M value)
    {
        list.set(i, value);

        return this;
    }

    public boolean contains(final M value)
    {
        return list.contains(value);
    }

    public NFastArrayList<M> clear()
    {
        list.clear();

        return this;
    }

    public NFastArrayList<M> remove(final M value)
    {
        list.remove(value);

        return this;
    }

    public NFastArrayList<M> unshift(final M value)
    {
        doUnShift(value);

        return this;
    }

    public NFastArrayList<M> moveUp(final M value)
    {
        if (!list.isEmpty())
        {
            int i = list.indexOf(value);
            list.set(i + 1, value);
        }

        return this;
    }

    public NFastArrayList<M> moveDown(final M value)
    {
        if (!list.isEmpty())
        {
            int i = list.indexOf(value);
            list.set(i > 0 ? i - 1 : 0, value);
        }

        return this;
    }

    public NFastArrayList<M> moveToTop(final M value)
    {
        if ((size() < 2) || (false == contains(value)))
        {
            return this;
        }
        remove(value);

        add(value);

        return this;
    }

    public NFastArrayList<M> moveToBottom(final M value)
    {
        if ((size() < 2) || (false == contains(value)))
        {
            return this;
        }
        remove(value);

        unshift(value);

        return this;
    }

    public M pop()
    {
        M result = null;

        if (!list.isEmpty())
        {
            int i = list.size() - 1;
            result = (M) list.get(i);
            list.remove(i);
        }

        return result;
    }

    public M shift()
    {
        return doShift();
    }

    public NFastArrayList<M> splice(final int beg, final int removed, final M value)
    {
        // TODO

        return this;
    }

    public NFastArrayList<M> reverse()
    {
        Collections.reverse(list);
        return this;
    }

    @SuppressWarnings("unchecked")
    public NFastArrayList<M> push(final M v, final M... values)
    {
        add(v);

        for (int i = 0; i < values.length; i++)
        {
            add(values[i]);
        }
        return this;
    }

    public NFastArrayList<M> copy()
    {
        NFastArrayList result = new NFastArrayList<M>();
        result.list.addAll(this.list);
        return result;
    }

    public NFastArrayList<M> concat(final NFastArrayList<M> value)
    {
        NFastArrayList result = copy();

        if (null != value)
        {
            result.list.addAll(value.list);
            return copy();
        }

        return result;
    }

    public NFastArrayList<M> slice(final int beg)
    {
        // TODO
        return copy();
    }

    public NFastArrayList<M> slice(final int beg, final int end)
    {
        // TODO: Arrays.copyOfRange(list, beg, end);

        return copy();
    }

    public List<M> toList()
    {
        final int size = size();

        final ArrayList<M> list = new ArrayList<M>(size);

        for (int i = 0; i < size; i++)
        {
            list.add(get(i));
        }
        return Collections.unmodifiableList(list);
    }

    public Iterator<M> iterator()
    {
        return toList().iterator();
    }

    @SuppressWarnings("unchecked")
    private M doShift()
    {
        M t = (M) list.get(0);
        list.remove(0);
        return t;
    }

    private void doUnShift(final M value)
    {
        list.add(0, value);
    }

}
