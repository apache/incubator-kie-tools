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


package org.kie.workbench.common.stunner.lienzo.flowpanel;

public interface FloatingView<V> {

    void add(final V item);

    FloatingView<V> setOffsetX(final double ox);

    FloatingView<V> setOffsetY(final double oy);

    FloatingView<V> setX(final double x);

    FloatingView<V> setY(final double y);

    FloatingView<V> show();

    FloatingView<V> hide();

    FloatingView<V> setTimeOut(final int timeOut);

    FloatingView<V> clearTimeOut();

    FloatingView<V> setHideCallback(final Runnable hideCallback);

    void clear();

    void destroy();
}
