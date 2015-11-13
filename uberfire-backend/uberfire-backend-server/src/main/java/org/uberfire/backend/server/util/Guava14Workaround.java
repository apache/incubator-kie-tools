/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.backend.server.util;

import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.Service;

/**
 * Produces an empty {@code Set<Service>} in order to prevent deploy-time failure.
 * The issue is <a href="https://code.google.com/p/guava-libraries/issues/detail?id=1527">described in the Guava bug
 * tracker</a>. This workaround is described in <a
 * href="https://code.google.com/p/guava-libraries/issues/detail?id=1433#c20">a comment on separate issue</a>.
 * 
 */
@ApplicationScoped
public class Guava14Workaround {

    @Produces Set<Service> dummyServices() {
        return ImmutableSet.of();
    }

}
