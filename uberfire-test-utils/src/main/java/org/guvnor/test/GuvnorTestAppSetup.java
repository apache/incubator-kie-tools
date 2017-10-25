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

package org.guvnor.test;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

import org.uberfire.ext.metadata.backend.lucene.LuceneConfig;
import org.uberfire.io.IOService;
import org.uberfire.io.impl.IOServiceDotFileImpl;
import org.uberfire.rpc.SessionInfo;

import static org.mockito.Mockito.*;

@ApplicationScoped
@Priority(1) // needed in order to inject the @Alternatives outside of this bean bundle (aka maven module)
public class GuvnorTestAppSetup {

    public static final IOService DEFAULT_IO_SERVICE = new IOServiceDotFileImpl();

    public static IOService ioService = DEFAULT_IO_SERVICE;

    @Produces
    @Named("ioStrategy")
    public IOService ioService() {
        return ioService;
    }

    @Produces
    @Alternative
    public SessionInfo sessionInfo() {
        return mock(SessionInfo.class);
    }

    @Produces
    @Named("luceneConfig")
    public LuceneConfig luceneConfig() {
        return mock(LuceneConfig.class);
    }

    public static void reset() {
        ioService = DEFAULT_IO_SERVICE;
    }
}
