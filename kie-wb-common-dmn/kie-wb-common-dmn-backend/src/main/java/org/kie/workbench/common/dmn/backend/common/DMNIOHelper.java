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

package org.kie.workbench.common.dmn.backend.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DMNIOHelper {

    public String isAsString(final InputStream inputStream) {

        try (final InputStream inputStreamAutoClosable = inputStream;
             final ByteArrayOutputStream result = new ByteArrayOutputStream()) {

            final byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStreamAutoClosable.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }

            return result.toString(StandardCharsets.UTF_8.name());
        } catch (final IOException ioe) {
            //Swallow. null is returned by default.
        }
        return null;
    }
}
