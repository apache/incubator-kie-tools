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
package org.guvnor.ala.build.maven.executor.gwt;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.enterprise.context.ApplicationScoped;

import org.guvnor.ala.config.gwt.CodeServerPortHandle;

@ApplicationScoped
public class GWTCodeServerPortLeaserImpl implements GWTCodeServerPortLeaser {

    protected static final int CODE_SERVER_LOWEST_PORT = 5000;
    protected static final int CODE_SERVER_HIGHEST_PORT = 5010;
    protected final Set<Integer> leasedCodeServerPorts = new ConcurrentHashMap<Integer, Integer>().keySet();
    protected Map<String, Integer> codeServerByProject = new ConcurrentHashMap<>();

    @Override
    public boolean isCodeServerRunning(String projectName) {
        return codeServerByProject.containsKey(projectName);
    }

    @Override
    public Integer getCodeServerPortNumber(String projectName) {
        return codeServerByProject.get(projectName);
    }

    @Override
    public void setCodeServerForProject(String projectName,
                                        Integer portNumber) {
        codeServerByProject.put(projectName,
                                portNumber);
    }

    @Override
    public CodeServerPortHandle getAvailableCodeServerPort() {
        return new CodeServerPortHandle() {

            private Integer leasedPort = leaseAvailableCodeServerPort();

            @Override
            public void relinquishPort() {
                leasedCodeServerPorts.remove(leasedPort);
                leasedPort = null;
            }

            @Override
            public Integer getPortNumber() {
                if (leasedPort != null) {
                    return leasedPort;
                } else {
                    throw new RuntimeException("Cannot get port number after relinquishing.");
                }
            }
        };
    }

    private synchronized Integer leaseAvailableCodeServerPort() {
        Integer port = CODE_SERVER_LOWEST_PORT;

        while (port <= CODE_SERVER_HIGHEST_PORT && leasedCodeServerPorts.contains(port)) {
            port++;
        }

        if (port > CODE_SERVER_HIGHEST_PORT) {
            throw new RuntimeException("All available code server ports are in use.");
        }

        leasedCodeServerPorts.add(port);

        return port;
    }
}
