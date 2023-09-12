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


package org.kie.workbench.common.stunner.core.client.animation;

public abstract class AbstractAnimation<S> implements Animation<S> {

    private static final long ANIMATION_DURATION = 50;

    private AnimationCallback callback;
    private long duration = ANIMATION_DURATION;

    @Override
    public AbstractAnimation setCallback(final AnimationCallback callback) {
        this.callback = callback;
        return this;
    }

    @Override
    public AbstractAnimation setDuration(final long duration) {
        this.duration = duration;
        return this;
    }

    public AnimationCallback getCallback() {
        return callback;
    }

    public long getDuration() {
        return duration;
    }
}
