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
package org.yard.validator;


import org.junit.Test;
import org.yard.validator.key.Key;
import org.yard.validator.key.Location;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ParserTest {

    @Test
    public void inlineRuleTest() throws IOException {
        final String read = read("traffic-violation.yml");
        final ParserResult parse = new Parser().parse(read);


        for (Location location : parse.getResult().keySet()) {
            System.out.println(location);

            for (Key key : parse.getResult().get(location).toArray(new Key[parse.getResult().get(location).size()])) {
                System.out.println(key);
            }
        }
        assertEquals(4, parse.getResult().keySet().size());
    }

    @Test
    public void whenThenRuleTest() throws IOException {
        final String read = read("service-price.yml");
        final ParserResult parse = new Parser().parse(read);


        for (Location location : parse.getResult().keySet()) {
            System.out.println(location);
            for (Key key : parse.getResult().get(location).toArray(new Key[parse.getResult().get(location).size()])) {
                System.out.println(key);
            }
        }
        assertEquals(4, parse.getResult().keySet().size());
    }

    private String read(final String name) throws FileNotFoundException {
        final StringBuffer buffer = new StringBuffer();

        final URL resource = ParserTest.class.getResource(name);

        final Scanner sc = new Scanner(new File(resource.getFile()));

        while (sc.hasNextLine()) {
            buffer.append(sc.nextLine());
            buffer.append(System.lineSeparator());
        }

        return buffer.toString();
    }

}