/*
   Copyright (c) 2014,2015,2016 Ahome' Innovation Technologies. All rights reserved.

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

package com.ait.lienzo.tools.common.api.flow;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import java.util.function.Predicate;

public final class Flows
{
    protected Flows()
    {
    }

    public static interface BooleanOp
    {
        public boolean test();
    }

    public static final BooleanOp orOp(final BooleanOp... ops)
    {
        return new OrBooleanOp(Arrays.asList(Objects.requireNonNull(ops)));
    }

    public static final BooleanOp andOp(final BooleanOp... ops)
    {
        return new AndBooleanOp(Arrays.asList(Objects.requireNonNull(ops)));
    }

    public static final BooleanOp notOp(final BooleanOp op)
    {
        Objects.requireNonNull(op);

        return () -> (false == op.test());
    }

    public static final <T> BooleanOp composeOp(final T value, final Predicate<T> predicate)
    {
        Objects.requireNonNull(predicate);

        return () -> predicate.test(value);
    }

    public abstract static class PredicateBooleanOp<T>implements BooleanOp, Predicate<T>
    {
        private final T m_value;

        public PredicateBooleanOp(final T value)
        {
            m_value = value;
        }

        @Override
        public final boolean test()
        {
            return test(m_value);
        }
    }

    private static final class OrBooleanOp implements BooleanOp
    {
        final List<BooleanOp> m_list;

        public OrBooleanOp(final List<BooleanOp> list)
        {
            m_list = Objects.requireNonNull(list);
        }

        @Override
        public final boolean test()
        {
            final int size = m_list.size();

            for (int i = 0; i < size; i++)
            {
                if (m_list.get(i).test())
                {
                    return true;
                }
            }
            return false;
        }
    }

    private static final class AndBooleanOp implements BooleanOp
    {
        final List<BooleanOp> m_list;

        public AndBooleanOp(final List<BooleanOp> list)
        {
            m_list = Objects.requireNonNull(list);
        }

        @Override
        public final boolean test()
        {
            final int size = m_list.size();

            for (int i = 0; i < size; i++)
            {
                if (!m_list.get(i).test())
                {
                    return false;
                }
            }
            return true;
        }
    }
}
