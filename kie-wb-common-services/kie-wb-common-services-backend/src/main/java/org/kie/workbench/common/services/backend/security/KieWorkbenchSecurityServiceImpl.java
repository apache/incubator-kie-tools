/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.services.backend.security;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.enterprise.context.ApplicationScoped;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.services.shared.security.KieWorkbenchSecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@ApplicationScoped
public class KieWorkbenchSecurityServiceImpl implements KieWorkbenchSecurityService {

    private static final Logger logger = LoggerFactory.getLogger(KieWorkbenchSecurityServiceImpl.class);

    @Override
    public String loadPolicy() {
        try {
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("workbench-policy.properties");
            if (is != null) {
                String policy = fromStream(is);
                return policy;
            }
        } catch (Exception e) {
            logger.error("Workbench security policy couldn't be loaded.", e);
        }
        return "";
    }

    public static String fromStream(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder out = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            out.append(line).append("\n");
        }
        return out.toString();
    }
}
