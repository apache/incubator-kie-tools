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


package org.kie.workbench.common.stunner.core.util;

public class HashUtil {

    private static int combineHashCodesHelper(int hashCode1,
                                              int hashCode2) {
        int out = 1500450271;//large prime number
        out = ~~(~~(hashCode1 * out) + ~~(hashCode2 & out));
        out = ~~(hashCode1 ^ out - hashCode2);
        return ~~(hashCode2 ^ out - hashCode1);
    }

    public static int combineHashCodes(int... hashcodes) {
        int out = 0;
        for (int hashcode : hashcodes) {
            out = combineHashCodesHelper(out,
                                         hashcode);
        }
        return out;
    }
}
