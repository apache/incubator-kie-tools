/*
   Copyright (c) 2014,2015 Ahome' Innovation Technologies. All rights reserved.

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

package com.ait.lienzo.client.core;

import java.util.List;

import com.ait.lienzo.client.core.types.NFastStringSet;
import com.ait.lienzo.shared.java.util.function.Predicate;

public final class AttributeOp
{
    private static NFastStringSet s_changed;

    private AttributeOp()
    {
    }

    public static interface BooleanOp
    {
        boolean test();
    }

    public static final boolean evaluate(NFastStringSet changed, BooleanOp op)
    {
        if ((null == changed) || (null == op))
        {
            return false;
        }
        if (changed.isEmpty())
        {
            return false;
        }
        s_changed = changed;

        final boolean result = op.test();

        s_changed = null;

        return result;
    }

    private static final NFastStringSet context()
    {
        return s_changed;
    }

    private static final NFastStringSet toSet(final Attribute... attributes)
    {
        final NFastStringSet nset = new NFastStringSet();

        for (Attribute attribute : attributes)
        {
            nset.add(attribute.getProperty());
        }
        return nset;
    }

    private static final NFastStringSet toSet(final List<Attribute> attributes)
    {
        final NFastStringSet nset = new NFastStringSet();

        for (Attribute attribute : attributes)
        {
            nset.add(attribute.getProperty());
        }
        return nset;
    }

    public static final BooleanOp has(final Attribute attributes)
    {
        return new AnyStringSetOp(toSet(attributes));
    }

    public static final BooleanOp any(final Attribute... attributes)
    {
        return new AnyStringSetOp(toSet(attributes));
    }

    public static final BooleanOp any(final List<Attribute> attributes)
    {
        return new AnyStringSetOp(toSet(attributes));
    }

    public static final BooleanOp none(final Attribute... attributes)
    {
        return new NoneStringSetOp(toSet(attributes));
    }

    public static final BooleanOp none(final List<Attribute> attributes)
    {
        return new NoneStringSetOp(toSet(attributes));
    }

    public static final BooleanOp all(final Attribute... attributes)
    {
        return new AllStringSetOp(toSet(attributes));
    }

    public final BooleanOp all(final List<Attribute> attributes)
    {
        return new AllStringSetOp(toSet(attributes));
    }

    public static final BooleanOp one(final Attribute... attributes)
    {
        return new OneStringSetOp(toSet(attributes));
    }

    public static final BooleanOp one(final List<Attribute> attributes)
    {
        return new OneStringSetOp(toSet(attributes));
    }

    public static final BooleanOp or(final BooleanOp op, final BooleanOp... ops)
    {
        return new BooleanOp()
        {
            @Override
            public final boolean test()
            {
                if (op.test())
                {
                    return true;
                }
                for (BooleanOp or : ops)
                {
                    if (or.test())
                    {
                        return true;
                    }
                }
                return false;
            }
        };
    }

    public static final BooleanOp and(final BooleanOp op, final BooleanOp... ops)
    {
        return new BooleanOp()
        {
            @Override
            public final boolean test()
            {
                if (false == op.test())
                {
                    return false;
                }
                for (BooleanOp or : ops)
                {
                    if (false == or.test())
                    {
                        return false;
                    }
                }
                return true;
            }
        };
    }

    public static final BooleanOp not(final BooleanOp op)
    {
        return new BooleanOp()
        {
            @Override
            public final boolean test()
            {
                return (false == op.test());
            }
        };
    }

    private static abstract class AbstractStringSetOp implements Predicate<NFastStringSet>, BooleanOp
    {
        private final NFastStringSet m_attributes;

        protected AbstractStringSetOp(final NFastStringSet attributes)
        {
            m_attributes = attributes;
        }

        @Override
        public final boolean test()
        {
            return test(m_attributes);
        }
    }

    private static final class AnyStringSetOp extends AbstractStringSetOp
    {
        private AnyStringSetOp(final NFastStringSet attributes)
        {
            super(attributes);
        }

        @Override
        public final boolean test(final NFastStringSet attributes)
        {
            final NFastStringSet changed = context();

            for (String attribute : attributes)
            {
                if (changed.contains(attribute))
                {
                    return true;
                }
            }
            return false;
        }
    }

    private static final class NoneStringSetOp extends AbstractStringSetOp
    {
        private NoneStringSetOp(final NFastStringSet attributes)
        {
            super(attributes);
        }

        @Override
        public final boolean test(final NFastStringSet attributes)
        {
            final NFastStringSet changed = context();

            for (String attribute : attributes)
            {
                if (changed.contains(attribute))
                {
                    return false;
                }
            }
            return true;
        }
    }

    private static final class AllStringSetOp extends AbstractStringSetOp
    {
        private AllStringSetOp(final NFastStringSet attributes)
        {
            super(attributes);
        }

        @Override
        public final boolean test(final NFastStringSet attributes)
        {
            boolean seen = false;

            final NFastStringSet changed = context();

            for (String attribute : attributes)
            {
                if (false == changed.contains(attribute))
                {
                    return false;
                }
                seen = true;
            }
            return seen;
        }
    }

    private static final class OneStringSetOp extends AbstractStringSetOp
    {
        private OneStringSetOp(final NFastStringSet attributes)
        {
            super(attributes);
        }

        @Override
        public final boolean test(final NFastStringSet attributes)
        {
            int count = 0;

            final NFastStringSet seen = new NFastStringSet();

            final NFastStringSet changed = context();

            for (String attribute : attributes)
            {
                if (false == seen.contains(attribute))
                {
                    if (changed.contains(attribute))
                    {
                        if (++count > 1)
                        {
                            return false;
                        }
                        seen.add(attribute);
                    }
                }
            }
            return (0 != count);
        }
    }
}
