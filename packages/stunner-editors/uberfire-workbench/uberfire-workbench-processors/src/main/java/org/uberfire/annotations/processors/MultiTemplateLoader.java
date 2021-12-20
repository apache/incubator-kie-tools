/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.uberfire.annotations.processors;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import freemarker.cache.StatefulTemplateLoader;
import freemarker.cache.TemplateLoader;

/**
 * This is a fork of {@link freemarker.cache.MultiTemplateLoader} that supports adding additional
 * {@link TemplateLoader} on demand. Uberfire has numerous code generators in different packages
 * each of which has their own template files and hence requires a different loader.
 */
public class MultiTemplateLoader implements StatefulTemplateLoader {

    private final List<TemplateLoader> loaders = new ArrayList<>();

    public void addTemplateLoader(final TemplateLoader loader) {
        loaders.add(loader);
    }

    @Override
    public Object findTemplateSource(String name)
            throws IOException {
        for (TemplateLoader loader : loaders) {
            Object source = loader.findTemplateSource(name);
            if (source != null) {
                return new MultiSource(source,
                                       loader);
            }
        }
        return null;
    }

    @Override
    public long getLastModified(Object templateSource) {
        return ((MultiSource) templateSource).getLastModified();
    }

    @Override
    public Reader getReader(Object templateSource,
                            String encoding)
            throws IOException {
        return ((MultiSource) templateSource).getReader(encoding);
    }

    @Override
    public void closeTemplateSource(Object templateSource)
            throws IOException {
        ((MultiSource) templateSource).close();
    }

    @Override
    public void resetState() {
        loaders.stream().filter(l -> l instanceof StatefulTemplateLoader).forEach(l -> ((StatefulTemplateLoader) l).resetState());
    }

    /**
     * Represents a template source bound to a specific template loader. It serves as the complete template source
     * descriptor used by the MultiTemplateLoader class.
     */
    static final class MultiSource {

        private final Object source;
        private final TemplateLoader loader;

        MultiSource(Object source,
                    TemplateLoader loader) {
            this.source = source;
            this.loader = loader;
        }

        long getLastModified() {
            return loader.getLastModified(source);
        }

        Reader getReader(String encoding)
                throws IOException {
            return loader.getReader(source,
                                    encoding);
        }

        void close()
                throws IOException {
            loader.closeTemplateSource(source);
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof MultiSource) {
                MultiSource m = (MultiSource) o;
                return m.loader.equals(loader) && m.source.equals(source);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return loader.hashCode() + 31 * source.hashCode();
        }

        @Override
        public String toString() {
            return source.toString();
        }
    }
}