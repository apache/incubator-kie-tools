/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.guvnor.common.services.project.model;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class GAVTest {

    @Test
    public void testGAVFromString() {
        GAV gav = new GAV("myGroupID:myArtifactID:version");

        assertThat(gav.getGroupId()).isEqualTo("myGroupID");
        assertThat(gav.getArtifactId()).isEqualTo("myArtifactID");
        assertThat(gav.getVersion()).isEqualTo("version");
    }

    @Test
    public void whenGivenNullString_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> new GAV(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'gavString' should be not null!");
    }

    @Test
    public void createGavFromString() {
        GAV gav1 = new GAV("gr1:ar1:ver1");
        assertThat(gav1.getGroupId()).isEqualTo("gr1");
        assertThat(gav1.getArtifactId()).isEqualTo("ar1");
        assertThat(gav1.getVersion()).isEqualTo("ver1");

        GAV gav2 = new GAV("gr2:ar2:ver2:compile");
        assertThat(gav2.getGroupId()).isEqualTo("gr2");
        assertThat(gav2.getArtifactId()).isEqualTo("ar2");
        assertThat(gav2.getVersion()).isEqualTo("ver2");
    }

    @Test
    public void whenGivenInvalidGav_throwsIllegalArgumentExceptions() {
        assertThatThrownBy(() -> new GAV("nonsense"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("The GAV String must have the form group:artifact:version[:scope] but was 'nonsense'");

        assertThatThrownBy(() -> new GAV("mygroup:myartifact"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("The GAV String must have the form group:artifact:version[:scope] but was 'mygroup:myartifact'");
    }
    
    @Test
    public void isSnapshotTest() {
        GAV
                noSnapshot1 = new GAV(),
                noSnapshot2 = new GAV("group:artifact:1.0"),
                noSnapshot3 = new GAV("group", "artifact", "version"),
                noSnapshot4 = new GAV("group", "artifact", null),
                snapshot = new GAV("group", "artifact", "1.0-SNAPSHOT");

        assertThat(noSnapshot1.isSnapshot()).isFalse();
        assertThat(noSnapshot2.isSnapshot()).isFalse();
        assertThat(noSnapshot3.isSnapshot()).isFalse();
        assertThat(noSnapshot4.isSnapshot()).isFalse();
        assertThat(snapshot.isSnapshot()).isTrue();
    }
}