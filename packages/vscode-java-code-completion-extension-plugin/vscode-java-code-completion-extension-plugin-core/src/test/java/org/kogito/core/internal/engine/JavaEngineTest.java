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

package org.kogito.core.internal.engine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class JavaEngineTest {

    private JavaEngine javaEngine;
    private final Path path = Path.of("aaa/src/main/java/pkg/MainClass.java");

    @BeforeEach
    public void setUp() {
        this.javaEngine = new JavaEngine();
    }

    @Test
    void testGetClassName() {
        String result = this.javaEngine.getClassName(path);
        assertThat(result).isEqualTo("MainClass");
    }

    @Test
    void testImportPosition() {
        BuildInformation info = this.javaEngine.buildImportClass(path, "java.util.");
        int position = info.getPosition();
        assertThat(position).isEqualTo(17);
    }

    @Test
    void testEmptyImportPosition() {
       BuildInformation info = this.javaEngine.buildImportClass(path, "");
        int position = info.getPosition();
        assertThat(position).isEqualTo(7);
    }

    @Test
    void testPublicMethodPosition() {
       BuildInformation info = this.javaEngine.buildPublicContent(path, "org.kie.MyClass", "get");
        int position = info.getPosition();
        assertThat(position).isEqualTo(33);
     }

    @Test
    void testEmptyPublicMethodPosition() {
        BuildInformation info = this.javaEngine.buildPublicContent(path, "org.kie.MyClass", "");
        int position = info.getPosition();
        assertThat(position).isEqualTo(30);
    }
}