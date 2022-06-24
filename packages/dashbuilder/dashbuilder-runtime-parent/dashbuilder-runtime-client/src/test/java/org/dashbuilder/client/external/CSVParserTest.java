/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.dashbuilder.client.external;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CSVParserTest {
    private static final String TEST =
            "1942,1943,15,SPECIAL AWARD,\" Metro-Goldwyn-Mayer for its achievement in representing the American Way of Life in the production of the \"\"Andy Hardy\"\" series of films.\",,True\n"
                    + "1943,1944,16,ART DIRECTION (Black-and-White),\"Art Direction:  Hans Dreier, Ernst Fegte;  Interior Decoration:  Bertram Granger\",Five Graves to Cairo,False\n"
                    + "1953,1954,26,HONORARY AWARD,\" Pete Smith for his witty and pungent observations on the American scene in his series of \"\"Pete Smith Specialties.\"\"\",,True";
    private static final String EXP =
            "[[\"1943\",\"1944\",\"16\",\"ART DIRECTION (Black-and-White)\",\"Art Direction:  Hans Dreier, Ernst Fegte;  Interior Decoration:  Bertram Granger\",\"Five Graves to Cairo\",\"false\"],[\"1953\",\"1954\",\"26\",\"HONORARY AWARD\",\" Pete Smith for his witty and pungent observations on the American scene in his series of \\\"Pete Smith Specialties.\\\"\",\"\",\"true\"]]";

    @Test
    public void testParse() {
        var parser = new CSVParser();
        var array = parser.toJsonArray(TEST);
        assertEquals(EXP, array);
    }
}
