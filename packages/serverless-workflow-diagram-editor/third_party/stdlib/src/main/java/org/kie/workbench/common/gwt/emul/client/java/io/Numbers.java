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


package java.io;

import elemental2.core.Float32Array;
import elemental2.core.Float64Array;
import elemental2.core.Int32Array;
import elemental2.core.Int8Array;

public final class Numbers {
    private static final Int8Array wba = new Int8Array(8);
    private static final Int32Array wia;
    private static final Float32Array wfa;
    private static final Float64Array wda;

    public Numbers() {
    }

    public static int floatToIntBits(float f) {
        wfa.setAt(0, (double) f);
        return ((Double) wia.getAt(0)).intValue();
    }

    public static float intBitsToFloat(int i) {
        wia.setAt(0, (double) i);
        return ((Double) wfa.getAt(0)).floatValue();
    }

    public static long doubleToLongBits(double d) {
        wda.setAt(0, d);
        return ((Double) wia.getAt(1)).longValue() << 32 | ((Double) wia.getAt(0)).longValue() & 4294967295L;
    }

    public static double longBitsToDouble(long l) {
        wia.setAt(1, (double) (l >>> 32));
        wia.setAt(0, (double) (l & 4294967295L));
        return (Double) wda.getAt(0);
    }

    public static long doubleToRawLongBits(double d) {
        wda.setAt(0, d);
        return ((Double) wia.getAt(1)).longValue() << 32 | ((Double) wia.getAt(0)).longValue() & 4294967295L;
    }

    static {
        wia = new Int32Array(wba.buffer, 0, 2);
        wfa = new Float32Array(wba.buffer, 0, 1);
        wda = new Float64Array(wba.buffer, 0, 1);
    }
}
