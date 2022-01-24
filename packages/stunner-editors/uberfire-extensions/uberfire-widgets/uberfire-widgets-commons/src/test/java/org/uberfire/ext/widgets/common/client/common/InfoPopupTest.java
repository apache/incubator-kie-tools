/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.widgets.common.client.common;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.gwtmockito.GwtMockitoTestRunner;

/**
 * Test InfoPopup is initialized correctly for each constructor.
 *
 */
@RunWith(GwtMockitoTestRunner.class)
public class InfoPopupTest {

    private InfoPopup infoPopup;
    private boolean infoPopupRecreated;

    @Before
    public void setup() {
        infoPopupRecreated = false;
    }

    @Test
    public void testRecreate_1() {

        infoPopup = new InfoPopup("title") {

            @Override
            public void recreate() {
                super.recreate();
                infoPopupRecreated = true;
            }
        };
        assertTrue(infoPopupRecreated);

    }

    @Test
    public void testRecreate_2() {

        infoPopup = new InfoPopup("title", "content") {

            @Override
            public void recreate() {
                super.recreate();
                infoPopupRecreated = true;
            }
        };
        assertTrue(infoPopupRecreated);

    }
}
