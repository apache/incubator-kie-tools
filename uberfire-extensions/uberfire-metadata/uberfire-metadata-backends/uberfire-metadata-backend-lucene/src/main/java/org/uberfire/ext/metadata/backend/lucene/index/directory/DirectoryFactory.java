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

package org.uberfire.ext.metadata.backend.lucene.index.directory;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.codecs.Codec;
import org.apache.lucene.codecs.PostingsFormat;
import org.apache.lucene.codecs.lucene62.Lucene62Codec;
import org.apache.lucene.index.IndexWriterConfig;
import org.uberfire.ext.metadata.backend.lucene.index.LuceneIndex;
import org.uberfire.ext.metadata.backend.lucene.index.LuceneIndexFactory;
import org.uberfire.ext.metadata.backend.lucene.model.KClusterImpl;
import org.uberfire.ext.metadata.model.KCluster;

import static org.kie.soup.commons.validation.PortablePreconditions.checkCondition;
import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

public class DirectoryFactory implements LuceneIndexFactory {

    private static final String REPOSITORIES_ROOT_DIR = ".index";
    public static final String CLUSTER_ID_SEGMENT_SEPARATOR = "/";

    private final Map<KCluster, LuceneIndex> clusters = new ConcurrentHashMap<>();
    private final DirectoryType type;
    private final Analyzer analyzer;

    public DirectoryFactory(final DirectoryType type,
                            final Analyzer analyzer) {
        this(type,
             analyzer,
             defaultHostingDir());
    }

    public DirectoryFactory(final DirectoryType type,
                            final Analyzer analyzer,
                            final File hostingDir) {
        this.analyzer = analyzer;
        this.type = type;
        this.loadIndexes(type,
                         analyzer,
                         hostingDir);
    }

    protected void loadIndexes(DirectoryType type,
                               Analyzer analyzer,
                               File hostingDir) {
        listFiles(hostingDir)
                .filter(File::isDirectory)
                .flatMap(file -> listFiles(file))
                .filter(File::isDirectory)
                .flatMap(file -> listFiles(file))
                .map(file -> new KClusterImpl(clusterIdOf(file)))
                .forEach(cluster -> clusters.put(cluster,
                                                 type.newIndex(cluster,
                                                               newConfig(analyzer))));
    }

    protected static String clusterIdOf(File file) {
        return file.getParentFile().getParentFile().getName() + CLUSTER_ID_SEGMENT_SEPARATOR +
                file.getParentFile().getName() + CLUSTER_ID_SEGMENT_SEPARATOR +
                file.getName();
    }

    private Stream<File> listFiles(final File hostingDir) {
        final File[] files = hostingDir.listFiles();
        return (files == null) ? Stream.empty() : Arrays.stream(files);
    }

    public static File defaultHostingDir() {
        final String value = System.getProperty("org.uberfire.metadata.index.dir");
        if (value == null || value.trim().isEmpty()) {
            return new File(REPOSITORIES_ROOT_DIR);
        } else {
            return new File(value.trim(),
                            REPOSITORIES_ROOT_DIR);
        }
    }

    private IndexWriterConfig newConfig(final Analyzer analyzer) {
        final IndexWriterConfig config = new IndexWriterConfig(analyzer);
        final Codec codec = new Lucene62Codec() {
            @Override
            public PostingsFormat getPostingsFormatForField(String field) {
                if (field.equals("id")) {
                    return PostingsFormat.forName("Memory");
                } else {
                    return PostingsFormat.forName("Lucene50");
                }
            }
        };
        config.setCodec(codec);

        return config;
    }

    @Override
    public LuceneIndex newCluster(final KCluster kcluster) {
        checkCondition("Cluster already exists",
                       !clusters.containsKey(checkNotNull("kcluster",
                                                          kcluster)));

        final LuceneIndex newIndex = type.newIndex(kcluster,
                                                   newConfig(analyzer));
        clusters.put(kcluster,
                     newIndex);

        return newIndex;
    }

    @Override
    public void remove(KCluster cluster) {
        clusters.remove(cluster);
    }

    @Override
    public Map<? extends KCluster, ? extends LuceneIndex> getIndexes() {
        return Collections.unmodifiableMap(clusters);
    }

    @Override
    public synchronized void dispose() {
        for (final LuceneIndex luceneIndex : clusters.values()) {
            luceneIndex.dispose();
        }
    }
}
