/*
 * Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.gwtproject.timer.client;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TimerTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @After
    public void restoreStreams() {
        System.setOut(originalOut);
    }

    @Test
    public void testStartTimer() throws InterruptedException {
        Timer example = new Timer(){
            @Override
            public void run() {
                System.out.println("Timer task executed!");
            }
        };
        example.schedule(5000);

        Thread.sleep(6000);

        assertTrue(outContent.toString().contains("Timer task executed!"));
    }

    @Test
    public void testScheduleRepeating() throws InterruptedException {
        Timer example = new Timer() {
            @Override
            public void run() {
                System.out.println("Timer task executed!");
            }
        };
        example.scheduleRepeating(2000);

        Thread.sleep(6500);

        String[] occurrences = outContent.toString().split("Timer task executed!");

        assertTrue(occurrences.length > 2);
    }
}
