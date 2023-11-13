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

package org.kie.workbench.common.stunner.lienzo.util;

import com.ait.lienzo.client.core.shape.MultiPath;

public class LienzoPaths {

    /**
     * Append the path parts for creating a rectangle multi-path shape.
     * @param w The rectangle width
     * @param h The rectangle height
     * @param r The rectangle corner radius
     */
    public static MultiPath rectangle(final double w,
                                      final double h,
                                      final double r) {
        return rectangle(new MultiPath(),
                         w,
                         h,
                         r);
    }

    /**
     * Append the path parts for creating a rectangle multi-path shape.
     * @param path The source multipath
     * @param w The rectangle width
     * @param h The rectangle height
     * @param r The rectangle corner radius
     */
    public static MultiPath rectangle(final MultiPath path,
                                      final double w,
                                      final double h,
                                      final double r) {
        if ((w > 0) && (h > 0)) {
            if ((r > 0) && (r < (w / 2)) && (r < (h / 2))) {
                path.M(r,
                       0);
                path.L(w - r,
                       0);
                path.A(w,
                       0,
                       w,
                       r,
                       r);
                path.L(w,
                       h - r);
                path.A(w,
                       h,
                       w - r,
                       h,
                       r);
                path.L(r,
                       h);
                path.A(0,
                       h,
                       0,
                       h - r,
                       r);
                path.L(0,
                       r);
                path.A(0,
                       0,
                       r,
                       0,
                       r);
            } else {
                path.rect(0,
                          0,
                          w,
                          h);
            }
            path.Z();
        }
        return path;
    }
}
