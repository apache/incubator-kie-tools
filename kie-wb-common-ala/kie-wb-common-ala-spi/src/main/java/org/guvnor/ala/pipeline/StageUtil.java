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
package org.guvnor.ala.pipeline;

import java.util.function.Consumer;
import java.util.function.Function;

import org.guvnor.ala.config.Config;

/**
 * Base implementation for creating Stages
 */
public final class StageUtil {

    private StageUtil() {
    }

    public static <INPUT extends Config, OUTPUT extends Config> Stage<INPUT, OUTPUT> config(final String name,
                                                                                            final Function<INPUT, OUTPUT> f) {
        return new Stage<INPUT, OUTPUT>() {

            @Override
            public void execute(final INPUT input,
                                final Consumer<OUTPUT> callback) {
                callback.accept(f.apply(input));
            }

            @Override
            public String getName() {
                return name;
            }
        };
    }
}
