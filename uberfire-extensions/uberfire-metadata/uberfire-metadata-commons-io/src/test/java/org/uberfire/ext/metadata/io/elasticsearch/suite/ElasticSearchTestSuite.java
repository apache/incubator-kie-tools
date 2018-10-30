/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.metadata.io.elasticsearch.suite;

import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerException;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.ext.metadata.io.elasticsearch.BatchIndexConcurrencyTest;
import org.uberfire.ext.metadata.io.elasticsearch.BatchIndexSingleThreadTest;
import org.uberfire.ext.metadata.io.elasticsearch.BatchIndexTest;
import org.uberfire.ext.metadata.io.elasticsearch.ComplexFieldsTest;
import org.uberfire.ext.metadata.io.elasticsearch.ElasticFullTextSearchIndexTest;
import org.uberfire.ext.metadata.io.elasticsearch.ElasticSearchIndexTest;
import org.uberfire.ext.metadata.io.elasticsearch.IOSearchServiceImplTest;
import org.uberfire.ext.metadata.io.elasticsearch.IOServiceIndexedDeleteFileTest;
import org.uberfire.ext.metadata.io.elasticsearch.IOServiceIndexedDotFileGitImplTest;
import org.uberfire.ext.metadata.io.elasticsearch.IOServiceIndexedDotFileGitInternalImplTest;
import org.uberfire.ext.metadata.io.elasticsearch.IOServiceIndexedGitImplTest;
import org.uberfire.ext.metadata.io.elasticsearch.IOServiceIndexedSortingTest;
import org.uberfire.ext.metadata.io.elasticsearch.ReplaceIndexedObjectTest;
import pl.domzal.junit.docker.rule.DockerRule;
import pl.domzal.junit.docker.rule.StopOption;
import pl.domzal.junit.docker.rule.WaitFor;

@RunWith(Suite.class)
@Suite.SuiteClasses({BatchIndexConcurrencyTest.class,
        BatchIndexSingleThreadTest.class,
        BatchIndexTest.class,
        ComplexFieldsTest.class,
        ElasticFullTextSearchIndexTest.class,
        ElasticSearchIndexTest.class,
        IOSearchServiceImplTest.class,
        IOServiceIndexedDeleteFileTest.class,
        IOServiceIndexedDotFileGitImplTest.class,
        IOServiceIndexedDotFileGitInternalImplTest.class,
        IOServiceIndexedGitImplTest.class,
        IOServiceIndexedSortingTest.class,
        ReplaceIndexedObjectTest.class})
public class ElasticSearchTestSuite {

    private static Logger logger = LoggerFactory.getLogger(ElasticSearchTestSuite.class);

    private static String image = "docker.elastic.co/elasticsearch/elasticsearch:5.6.1";

    public static final String CONTAINER_NAME = "kie-elasticsearch";
    public static DockerRule elasticsearchRule = DockerRule.builder()
            .imageName(image)
            .name(CONTAINER_NAME)
            .env("cluster.name",
                 "kie-cluster")
            .env("discovery.type",
                 "single-node")
            .env("http.host",
                 "0.0.0.0")
            .env("transport.host",
                 "0.0.0.0")
            .env("transport.tcp.port",
                 "9300")
            .env("xpack.security.enabled",
                 "false")
            .stopOptions(StopOption.KILL,
                         StopOption.REMOVE)
            .waitForTimeout(60)
            .expose("9200",
                    "9200")
            .expose("9300",
                    "9300")
            .waitFor(WaitFor.logMessage("mode [trial] - valid"))
            .build();

    private static boolean existContainer() throws DockerException, InterruptedException {
        return elasticsearchRule.getDockerClient().listContainers(DockerClient.ListContainersParam.allContainers(true))
                .stream()
                .anyMatch(container -> container.names()
                        .contains("/" + CONTAINER_NAME));
    }

    public static void before() throws Throwable {
        if (existContainer()) {
            logger.info("Container exists, removing");
            if (elasticsearchRule.getDockerClient().inspectContainer(CONTAINER_NAME).state().running()) {
                elasticsearchRule.getDockerClient().killContainer(CONTAINER_NAME);
            }
            elasticsearchRule.getDockerClient().removeContainer(CONTAINER_NAME);
        } else {
            logger.info("Container does not exist");
        }

        logger.info("Creating container");
        elasticsearchRule.before();
    }

    public static void after() {
        elasticsearchRule.after();
    }
}
