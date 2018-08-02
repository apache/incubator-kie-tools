/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.services.backend.logback;

import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class OutputSharedMapTest {

    private String KEY = "key";

    @Test
    public void addMessageTest(){
        List<String> msgs = OutputSharedMap.getLog(KEY);
        assertThat(msgs).isEmpty();
        OutputSharedMap.addMsgToLog(KEY, "msg");
        OutputSharedMap.addMsgToLog(KEY, "msgOne");
        msgs =OutputSharedMap.getLog(KEY);
        assertThat(msgs).hasSize(2);
        OutputSharedMap.purgeAll();
    }

    @Test
    public void getMessageTest(){
        List<String> msgs = OutputSharedMap.getLog(KEY);
        assertThat(msgs).isEmpty();
        OutputSharedMap.addMsgToLog(KEY, "msg");
        msgs =OutputSharedMap.getLog(KEY);
        assertThat(msgs).hasSize(1);
        assertThat(msgs.get(0)).isEqualTo("msg");
        OutputSharedMap.purgeAll();
    }

    @Test
    public void getRemoveMessageTest(){
        List<String> msgs = OutputSharedMap.getLog(KEY);
        assertThat(msgs).isEmpty();
        OutputSharedMap.addMsgToLog(KEY, "msg");
        msgs =OutputSharedMap.getLog(KEY);
        assertThat(msgs).hasSize(1);
        assertThat(msgs.get(0)).isEqualTo("msg");
        OutputSharedMap.removeLog(KEY);
        msgs =OutputSharedMap.getLog(KEY);
        assertThat(msgs).isEmpty();
        OutputSharedMap.purgeAll();
    }

    @Test
    public void getPurgeAllTest(){
        List<String> msgs = OutputSharedMap.getLog(KEY);
        assertThat(msgs).isEmpty();
        OutputSharedMap.addMsgToLog(KEY, "msg");
        OutputSharedMap.addMsgToLog(KEY, "msgOne");
        msgs =OutputSharedMap.getLog(KEY);
        assertThat(msgs).hasSize(2).contains("msg", "msgOne");
        OutputSharedMap.purgeAll();
        msgs =OutputSharedMap.getLog(KEY);
        assertThat(msgs).isEmpty();
    }
}
