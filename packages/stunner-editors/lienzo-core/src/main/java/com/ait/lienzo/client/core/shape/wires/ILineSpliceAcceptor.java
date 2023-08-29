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


package com.ait.lienzo.client.core.shape.wires;

import java.util.List;

public interface ILineSpliceAcceptor {

    ILineSpliceAcceptor ALL = new DefaultLineSpliceAcceptor(true);

    ILineSpliceAcceptor NONE = new DefaultLineSpliceAcceptor(false);

    boolean allowSplice(WiresShape shape,
                        double[] candidateLocation,
                        WiresConnector connector,
                        WiresContainer parent);

    boolean acceptSplice(WiresShape shape,
                         double[] candidateLocation,
                         WiresConnector connector,
                         List<double[]> firstHalfPoints,
                         List<double[]> secondHalfPoints,
                         WiresContainer parent);

    void ensureUnHighLight();

    class DefaultLineSpliceAcceptor implements ILineSpliceAcceptor {

        final private boolean m_defaultValue;

        private DefaultLineSpliceAcceptor(final boolean defaultValue) {
            m_defaultValue = defaultValue;
        }

        @Override
        public boolean allowSplice(WiresShape shape,
                                   double[] candidateLocation,
                                   WiresConnector connector,
                                   WiresContainer parent) {
            return m_defaultValue;
        }

        @Override
        public boolean acceptSplice(WiresShape shape,
                                    double[] candidateLocation,
                                    WiresConnector connector,
                                    List<double[]> firstHalfPoints,
                                    List<double[]> secondHalfPoints,
                                    WiresContainer parent) {
            return m_defaultValue;
        }

        public void ensureUnHighLight() {
        }
    }
}
