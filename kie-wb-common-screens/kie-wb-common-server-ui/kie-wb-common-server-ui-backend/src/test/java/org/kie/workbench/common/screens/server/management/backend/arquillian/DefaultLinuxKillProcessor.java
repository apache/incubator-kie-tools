/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.screens.server.management.backend.arquillian;

import org.jboss.arquillian.container.spi.Container;
import org.jboss.arquillian.container.spi.ServerKillProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultLinuxKillProcessor implements ServerKillProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultLinuxKillProcessor.class);

    private static final long TIMEOUT = 1000;

    @Override
    public void kill(Container container) throws Exception {
        String osName = System.getProperty("os.name", "");
        String killSequence = null;
        Process p = null;

        if (osName.toLowerCase().contains("linux")) {
            killSequence = "kill -9 `ps aux | grep -v 'grep' | grep 'jboss.home.dir=[jbossHome] ' | sed -re '1,$s/[ \\t]+/ /g' | cut -d ' ' -f 2`";
            killSequence = killSequence.replace("[jbossHome]", container.getContainerConfiguration().getContainerProperties()
                                                                        .get("jbossHome"));
            p = Runtime.getRuntime().exec(new String[]{"sh", "-c", killSequence});
            executeKill(p, killSequence);
        } else {
            container.stop();
        }
    }

    private void executeKill(Process p, String logKillSequence) throws Exception {
        LOGGER.info("Issuing kill sequence (on " + System.getProperty("os.name") + "): " + logKillSequence);
        p.waitFor();
        if (p.exitValue() != 0) {
            throw new RuntimeException("Kill sequence failed => server not killed. (Exit value of killing process: " + p.exitValue() + " OS=" + System.getProperty("os.name") + ")");
        }
        Thread.sleep(TIMEOUT);
        LOGGER.info("Kill sequence successfully completed. \n");
    }
}
