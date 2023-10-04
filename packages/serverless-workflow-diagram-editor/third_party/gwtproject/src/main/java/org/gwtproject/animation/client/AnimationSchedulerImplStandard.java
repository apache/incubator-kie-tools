/*
 * Copyright © 2020 The GWT Project Authors
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
package org.gwtproject.animation.client;

import elemental2.dom.DomGlobal;
import elemental2.dom.Element;
import jsinterop.base.Js;
import org.gwtproject.core.client.Duration;

/**
 * {@link AnimationScheduler} implementation that uses standard {@code requestAnimationFrame} API.
 */
class AnimationSchedulerImplStandard extends AnimationScheduler {

    @Override
    public AnimationHandle requestAnimationFrame(AnimationCallback callback, Element element) {
        final int id = requestImplNew(callback, element);
        return new AnimationHandle() {
            @Override
            public void cancel() {
                cancelImpl(id);
            }
        };
    }

    private static int requestImplNew(AnimationCallback cb, Element element) {
        return DomGlobal.requestAnimationFrame(
                p0 -> cb.execute(Duration.currentTimeMillis()), Js.cast(element));
    }

    private static void cancelImpl(int id) {
        DomGlobal.cancelAnimationFrame(id);
    }
}
