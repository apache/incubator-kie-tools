/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package com.ait.lienzo.client.core.animation;

import elemental2.dom.DomGlobal;
import elemental2.dom.Element;
import elemental2.dom.FrameRequestCallback;

public class AnimationScheduler {

    private static AnimationScheduler INSTANCE;

    public static AnimationScheduler get() {
        if (INSTANCE == null) {
            INSTANCE = new AnimationScheduler();
        }
        return INSTANCE;
    }

    public AnimationHandle requestAnimationFrame(AnimationCallback callback) {
        return requestAnimationFrame(callback,
                                     null);
    }

    public AnimationHandle requestAnimationFrame(AnimationCallback callback,
                                                 Element element) {
        final int id = requestImplNew(callback,
                                      element);
        return () -> cancelImpl(id);
    }

    private static int requestImplNew(AnimationCallback cb,
                                      Element element) {
        FrameRequestCallback callback = p0 -> cb.execute(p0);
        return DomGlobal.requestAnimationFrame(callback,
                                               element);
    }

    private static void cancelImpl(int id) {
        DomGlobal.cancelAnimationFrame(id);
    }

    /**
     * The callback used when an animation frame becomes available.
     */
    public interface AnimationCallback {

        /**
         * Invokes the command.
         *
         * @param timestamp the current timestamp
         */
        void execute(double timestamp);
    }

    /**
     * A handle to the requested animation frame created by
     * {@link #requestAnimationFrame(AnimationCallback, Element)}.
     */
    public interface AnimationHandle {

        /**
         * Cancel the requested animation frame. If the animation frame is already
         * canceled, do nothing.
         */
        void cancel();
    }
}
