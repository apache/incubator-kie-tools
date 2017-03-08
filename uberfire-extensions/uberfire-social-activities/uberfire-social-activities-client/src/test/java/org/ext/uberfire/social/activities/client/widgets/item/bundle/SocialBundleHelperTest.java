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

package org.ext.uberfire.social.activities.client.widgets.item.bundle;

import org.junit.Test;

import static org.junit.Assert.*;

public class SocialBundleHelperTest {

    @Test
    public void getTranslationFromServiceWithNoPreviouslyFoundTranslationTest() throws DuplicatedTranslationException {
        final String value = SocialBundleHelper.getTranslationFromService("key",
                                                                          null,
                                                                          getService());
        assertEquals("value",
                     value);
    }

    @Test
    public void getTranslationFromServiceWithoutValueWithNoPreviouslyFoundTranslationTest() throws DuplicatedTranslationException {
        final String value = SocialBundleHelper.getTranslationFromService("key",
                                                                          null,
                                                                          getServiceWithoutValue());
        assertNull(value);
    }

    @Test(expected = DuplicatedTranslationException.class)
    public void getTranslationFromServiceWithPreviouslyFoundTranslationTest() throws DuplicatedTranslationException {
        SocialBundleHelper.getTranslationFromService("key",
                                                     "previously-found-value",
                                                     getService());
    }

    @Test
    public void getTranslationFromServiceWithoutValueWithPreviouslyFoundTranslationTest() throws DuplicatedTranslationException {
        final String value = SocialBundleHelper.getTranslationFromService("key",
                                                                          "previously-found-value",
                                                                          getServiceWithoutValue());
        assertEquals("previously-found-value",
                     value);
    }

    private SocialBundleService getService() {
        return new SocialBundleService() {
            @Override
            public String getTranslation(final String key) {
                return "value";
            }
        };
    }

    private SocialBundleService getServiceWithoutValue() {
        return new SocialBundleService() {
            @Override
            public String getTranslation(final String key) {
                return null;
            }
        };
    }
}
