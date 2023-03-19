/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.dataprovider.kafka.mbean;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ObjectNameBuilderTest {

    @Test
    public void testBuildWithName() {
        String objectName = ObjectNameBuilder.create("my.domain")
                                             .type("MyType")
                                             .name("MyName")
                                             .build();
        assertEquals("my.domain:type=MyType,name=MyName", objectName);
    }

    @Test
    public void testBuildWithRequest() {
        String objectName = ObjectNameBuilder.create("my.domain")
                                             .type("MyType")
                                             .name("MyName")
                                             .request("MyRequest")
                                             .build();
        assertEquals("my.domain:type=MyType,name=MyName,request=MyRequest", objectName);
    }

    @Test
    public void testBuildWithDelayedOperation() {
        String objectName = ObjectNameBuilder.create("my.domain")
                                             .type("MyType")
                                             .name("MyName")
                                             .request("MyRequest")
                                             .delayedOperation("MyOp")
                                             .build();
        assertEquals("my.domain:type=MyType,name=MyName,request=MyRequest,delayedOperation=MyOp", objectName);
    }

    @Test
    public void testBuildWithClientId() {
        String objectName = ObjectNameBuilder.create("my.domain")
                                             .type("MyType")
                                             .name("MyName")
                                             .request("MyRequest")
                                             .delayedOperation("MyOp")
                                             .clientId("MyClientId")
                                             .build();
        assertEquals("my.domain:type=MyType,name=MyName,request=MyRequest,delayedOperation=MyOp,clientId=MyClientId", objectName);
    }

    @Test
    public void testBuildWithTopic() {
        String objectName = ObjectNameBuilder.create("my.domain")
                                             .type("MyType")
                                             .clientId("MyClientId")
                                             .topic("MyTopic")
                                             .build();
        assertEquals("my.domain:type=MyType,clientId=MyClientId,topic=MyTopic", objectName);
    }

    @Test
    public void testBuildWithNodeId() {
        String objectName = ObjectNameBuilder.create("my.domain")
                                             .type("MyType")
                                             .clientId("MyClientId")
                                             .nodeId("MyNode")
                                             .build();
        assertEquals("my.domain:type=MyType,clientId=MyClientId,nodeId=MyNode", objectName);
    }

    @Test
    public void testBuildWithHyfenClientId() {
        String objectName = ObjectNameBuilder.create("my.domain")
                                             .type("MyType")
                                             .hyfenClientId("MyClientId")
                                             .build();
        assertEquals("my.domain:type=MyType,client-id=MyClientId", objectName);
    }

    @Test
    public void testWithHyfenNodeId() {
        String objectName = ObjectNameBuilder.create("my.domain")
                                             .type("MyType")
                                             .hyfenNodeId("MyNodeId")
                                             .build();
        assertEquals("my.domain:type=MyType,node-id=MyNodeId", objectName);
    }

    @Test
    public void testWithPartition() {
        String objectName = ObjectNameBuilder.create("my.domain")
                                             .type("MyType")
                                             .partition("0")
                                             .build();
        assertEquals("my.domain:type=MyType,partition=0", objectName);
    }
}