/*
 *
 *    Copyright (c) 2018 Ahome' Innovation Technologies. All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */
package com.ait.lienzo.test;

import com.ait.lienzo.client.core.config.LienzoCore;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(LienzoMockitoTestRunner.class)
public class LienzoCoreAttributesTest {

    @Test
    public void canEnableHiDPI() {
        LienzoCore.get().setHidpiEnabled(true);
        Assert.assertTrue(LienzoCore.get().isHidpiEnabled());
    }

    @Test
    public void canEnableThenDisableHiDPI() {
        LienzoCore.get().setHidpiEnabled(true);
        Assert.assertTrue(LienzoCore.get().isHidpiEnabled());
        LienzoCore.get().setHidpiEnabled(false);
        Assert.assertFalse(LienzoCore.get().isHidpiEnabled());
    }
}