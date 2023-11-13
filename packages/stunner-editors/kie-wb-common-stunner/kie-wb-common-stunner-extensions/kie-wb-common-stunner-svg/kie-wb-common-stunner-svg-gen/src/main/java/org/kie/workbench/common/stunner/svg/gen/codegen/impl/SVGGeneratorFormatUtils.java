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


package org.kie.workbench.common.stunner.svg.gen.codegen.impl;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import org.kie.workbench.common.stunner.svg.gen.model.PrimitiveDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.impl.SVGModelUtils;

/**
 * As the generated values will end up in a source file, this utils class provides different methods
 * as for using concrete formatting behaviors common along all generated code.
 */
public class SVGGeneratorFormatUtils {

    private static final DecimalFormat DF = new DecimalFormat("#0.00") {{
        setDecimalFormatSymbols(new DecimalFormatSymbols() {{
            setDecimalSeparator('.');
            setGroupingUsed(false);
        }});
    }};

    public static String getValidInstanceId(final PrimitiveDefinition<?> primitiveDefinition) {
        return getValidInstanceId(primitiveDefinition.getId());
    }

    public static String getValidInstanceId(final String value) {
        return SVGModelUtils.toValidJavaId(value);
    }

    public static String format(final double value) {
        return DF.format(value);
    }

    /**
     * As the generated values will end up in a source file, let's use a concrete formatting
     * for double values along all code generation.
     */
    public static String format(final String pattern,
                                final double... values) {
        final String[] raw = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            raw[i] = DF.format(values[i]);
        }
        return String.format(pattern,
                             raw);
    }
}
