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

package com.ait.lienzo.client.core.animation;

import com.ait.lienzo.shared.core.types.DoublePowerFunction;

public interface AnimationTweener extends DoublePowerFunction
{
    AnimationTweener LINEAR      = TweenerBuilder.MAKE_LINEAR();

    AnimationTweener EASE_IN     = TweenerBuilder.MAKE_EASE_IN(3.0);

    AnimationTweener EASE_OUT    = TweenerBuilder.MAKE_EASE_OUT(3.0);

    AnimationTweener EASE_IN_OUT = TweenerBuilder.MAKE_EASE_IN_OUT();

    AnimationTweener ELASTIC     = TweenerBuilder.MAKE_ELASTIC(3);

    AnimationTweener BOUNCE      = TweenerBuilder.MAKE_BOUNCE(3);

    final class TweenerBuilder
    {
        public static final AnimationTweener MAKE_EASE_IN(final double strength)
        {
            return MAKE_EASE_IN_P(Math.min(6.0, Math.max(1.0, strength)));
        }

        public static final AnimationTweener MAKE_EASE_OUT(final double strength)
        {
            return MAKE_EASE_OUT_P(Math.min(6.0, Math.max(1.0, strength)));
        }

        private static final AnimationTweener MAKE_LINEAR()
        {
            return percent -> percent;
        }

        private static final AnimationTweener MAKE_EASE_IN_P(final double strength)
        {
            return percent -> Math.pow(percent, strength * 2.0);
        }

        private static final AnimationTweener MAKE_EASE_OUT_P(final double strength)
        {
            return percent -> (1.0 - Math.pow(1.0 - percent, strength * 2.0));
        }

        private static final AnimationTweener MAKE_EASE_IN_OUT()
        {
            return percent -> (percent - Math.sin(percent * 2.0 * Math.PI) / (2.0 * Math.PI));
        }

        public static final AnimationTweener MAKE_ELASTIC(final int passes)
        {
            return percent -> (((1.0 - Math.cos(percent * Math.PI * passes)) * (1.0 - percent)) + percent);
        }

        public static final AnimationTweener MAKE_BOUNCE(final int bounces)
        {
            final AnimationTweener elastic = MAKE_ELASTIC(bounces);

            return percent -> {
                percent = elastic.apply(percent);

                return ((percent <= 1.0) ? (percent) : (2.0 - percent));
            };
        }
    }
}
