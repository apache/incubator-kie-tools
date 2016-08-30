/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.screens.datamodeller.backend.server.indexing;

import java.io.Serializable;
import java.math.BigDecimal;

@Annotation1
public class Pojo2 implements Interface1 {

    private java.math.BigDecimal o_BigDecimal;

    private int p_int;

    public Pojo2() {
    }

    public Pojo2( java.math.BigDecimal o_BigDecimal,
                  int p_int, ) {
        this.o_BigDecimal = o_BigDecimal;
        this.p_int = p_int;
    }

    public Pojo1 getPojo1() {
        return new Pojo1();
    }

    public void setP_int( int p_int ) {
        this.p_int = p_int;
    }

}
