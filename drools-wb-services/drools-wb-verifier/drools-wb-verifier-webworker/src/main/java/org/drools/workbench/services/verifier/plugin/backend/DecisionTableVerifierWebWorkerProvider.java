/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.services.verifier.plugin.backend;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import javax.enterprise.context.ApplicationScoped;

import org.kie.workbench.common.services.verifier.service.VerifierWebWorkerProvider;

@ApplicationScoped
public class DecisionTableVerifierWebWorkerProvider
        implements VerifierWebWorkerProvider {

    @Override
    public String getId() {
        return "dtableVerifier";
    }

    @Override
    public String getWebWorker(final String fileName) throws Exception {
        return loadResource(fileName);
    }

    public String loadResource(final String name) throws Exception {
        final StringBuilder text = new StringBuilder();

        try (final InputStream in = DecisionTableVerifierWebWorkerProvider.class.getResourceAsStream(name);
             final Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {

            final char[] buf = new char[1024];
            int len = 0;
            while ((len = reader.read(buf)) >= 0) {
                text.append(buf,
                            0,
                            len);
            }
        } catch (final NullPointerException npe) {
            throw new FileNotFoundException("Could not find the verifier file " + name);
        }
        return text.toString();
    }
}
