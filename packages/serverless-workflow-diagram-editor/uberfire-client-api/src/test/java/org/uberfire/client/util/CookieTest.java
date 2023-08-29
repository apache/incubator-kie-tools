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


package org.uberfire.client.util;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

public class CookieTest {

    Cookie cookie;

    @Before
    public void setup() {
        cookie = spy(Cookie.class);
    }

    @Test
    public void emptyCookie() {
        doReturn("").when(cookie).get();
        String value = cookie.get("key1");
        assertEquals("", value);
    }

    @Test
    public void inexistentSingleCookie() {
        doReturn("key1=val1").when(cookie).get();
        String value = cookie.get("key2");
        assertEquals("", value);
    }

    @Test
    public void singleCookie() {
        doReturn("key1=val1").when(cookie).get();
        String value = cookie.get("key1");
        assertEquals("val1", value);
    }

    @Test
    public void inexistentMultipleCookies() {
        doReturn("key1=val1; key2=val2; key3=val3").when(cookie).get();
        String value = cookie.get("key4");
        assertEquals("", value);
    }

    @Test
    public void firstMultipleCookies() {
        doReturn("key1=val1; key2=val2; key3=val3").when(cookie).get();
        String value = cookie.get("key1");
        assertEquals("val1", value);
    }

    @Test
    public void middleMultipleCookies() {
        doReturn("key1=val1; key2=val2; key3=val3").when(cookie).get();
        String value = cookie.get("key2");
        assertEquals("val2", value);
    }

    @Test
    public void lastMultipleCookies() {
        doReturn("key1=val1; key2=val2; key3=val3").when(cookie).get();
        String value = cookie.get("key3");
        assertEquals("val3", value);
    }
}
