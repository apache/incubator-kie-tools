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
import org.yard.validator.key.Location;
import org.yard.validator.key.RowLocation;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class YardValidatorTest {

    @Test
    public void subsumption() throws FileNotFoundException {
        final String read = read("subsumption.yml");
        final YardValidator validator = new YardValidator();

        final List<Issue> issues = new ArrayList<>();

        validator.validate(read, issues::add);

        for (Issue issue : issues) {
            System.out.println(issue.getMessage());
        }

        assertEquals(1, issues.size());
        assertIssue(issues.get(0), "Subsumption found. If both rows return the same result, the other can be removed. If they return different results, the table fails to return a value.", 2, 3);

    }

    @Test
    public void subsumptionTheOtherWay() throws FileNotFoundException {
        final String read = read("subsumption-the-other-way.yml");
        final YardValidator validator = new YardValidator();

        final List<Issue> issues = new ArrayList<>();

        validator.validate(read, issues::add);

        for (Issue issue : issues) {
            System.out.println(issue.getMessage());
        }

        assertEquals(1, issues.size());
        assertIssue(issues.get(0), "Subsumption found. If both rows return the same result, the other can be removed. If they return different results, the table fails to return a value.", 2, 3);

    }

    @Test
    public void maskingRule() throws FileNotFoundException {
        final String read = read("subsumption-the-other-way.yml", "First");
        final YardValidator validator = new YardValidator();

        final List<Issue> issues = new ArrayList<>();

        validator.validate(read, issues::add);

        assertEquals(1, issues.size());
        assertIssue(issues.get(0), "Masking row. The higher row prevents the activation of the other row.", 2, 3);
    }

    @Test
    public void redundancy() throws FileNotFoundException {
        final String read = read("redundancy.yml");
        final YardValidator validator = new YardValidator();

        final List<Issue> issues = new ArrayList<>();

        validator.validate(read, issues::add);

        for (Issue issue : issues) {
            System.out.println(issue.getMessage());
        }

        assertEquals(1, issues.size());
        assertIssue(issues.get(0), "Redundancy found. If both rows return the same result, the other can be removed. If they return different results, the table fails to return a value.", 1, 2);
    }

    @Test
    public void redundancyWithUniqueHP() throws FileNotFoundException {
        final String read = read("redundancy.yml", "Unique");
        final YardValidator validator = new YardValidator();

        final List<Issue> issues = new ArrayList<>();

        validator.validate(read, issues::add);

        for (Issue issue : issues) {
            System.out.println(issue.getMessage());
        }

        assertEquals(1, issues.size());
        assertIssue(issues.get(0), "Redundancy found. Unique hit policy fails when more than one row returns results.", 1, 2);
    }
    private void assertIssue(
            final Issue issue,
            final String message,
            final Integer... wantedRows) {
        final List<Integer> rows = Arrays.asList(wantedRows);
        assertEquals(message, issue.getMessage());

        for (Location location : issue.getLocations()) {
            System.out.println(location);
        }

        assertEquals(issue.getLocations().length, rows.size());
        for (Location location : issue.getLocations()) {
            if (location instanceof RowLocation) {
                assertTrue(rows.contains(((RowLocation) location).getTableRowNumber()));
            }
        }
    }

    private String read(final String name) throws FileNotFoundException {
        return read(name, null);
    }

    private String read(
            final String name,
            final String hitPolicy) throws FileNotFoundException {
        final StringBuilder buffer = new StringBuilder();

        final URL resource = ParserTest.class.getResource(name);

        final Scanner sc = new Scanner(new File(resource.getFile()));

        while (sc.hasNextLine()) {
            final String line = sc.nextLine();
            buffer.append(line);
            buffer.append(System.lineSeparator());
            if (hitPolicy != null && line.trim().startsWith("outputComponents")) {
                buffer.append("      hitPolicy: ").append(hitPolicy);
                buffer.append(System.lineSeparator());
            }
        }

        return buffer.toString();
    }
}