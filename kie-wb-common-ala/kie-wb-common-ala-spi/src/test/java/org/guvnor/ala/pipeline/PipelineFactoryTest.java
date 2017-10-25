/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.ala.pipeline;

import java.util.ArrayList;
import java.util.List;

import org.guvnor.ala.config.Config;
import org.guvnor.ala.pipeline.impl.PipelineConfigImpl;
import org.junit.Before;
import org.junit.Test;

import static org.guvnor.ala.AlaSPITestCommons.mockList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PipelineFactoryTest {

    private static final String PIPELINE_NAME = "PIPELINE_NAME";

    private static final int CONFIG_COUNT = 10;

    private static final String CONFIG_STAGE_PREFIX = "ConfigStage";

    private List<Config> configs;

    @Before
    public void setUp() {
        configs = mockList(Config.class,
                           CONFIG_COUNT);
    }

    @Test
    public void testNewBuilder() {
        ConfigBasedPipelineBuilder builder = PipelineFactory.newBuilder();
        for (int i = 0; i < CONFIG_COUNT; i++) {
            builder.addConfigStage(mockConfigStageName(i),
                                   configs.get(i));
        }
        ConfigBasedPipeline result = builder.buildAs(PIPELINE_NAME);
        assertPipelineIsTheExpected(result);
    }

    @Test
    public void testNewPipeline() {
        List<PipelineConfigStage> configStages = new ArrayList<>();
        for (int i = 0; i < CONFIG_COUNT; i++) {
            configStages.add(new PipelineConfigStage(mockConfigStageName(i),
                                                     configs.get(i)));
        }
        PipelineConfigImpl pipelineConfig = new PipelineConfigImpl(PIPELINE_NAME,
                                                                   configStages);
        ConfigBasedPipeline result = PipelineFactory.newPipeline(pipelineConfig);
        assertPipelineIsTheExpected(result);
    }

    private void assertPipelineIsTheExpected(ConfigBasedPipeline pipeline) {
        assertEquals(PIPELINE_NAME,
                     pipeline.getName());
        assertNotNull(pipeline.getConfig());
        PipelineConfig pipelineConfig = pipeline.getConfig();
        assertEquals(PIPELINE_NAME,
                     pipelineConfig.getName());
        List<PipelineConfigStage> configStages = pipelineConfig.getConfigStages();
        assertEquals(CONFIG_COUNT,
                     configStages.size());
        for (int i = 0; i < CONFIG_COUNT; i++) {
            PipelineConfigStage configStage = configStages.get(i);
            assertEquals(mockConfigStageName(i),
                         configStage.getName());
            assertEquals(configs.get(i),
                         configStage.getConfig());
        }
    }

    private String mockConfigStageName(int i) {
        return CONFIG_STAGE_PREFIX + i;
    }
}
