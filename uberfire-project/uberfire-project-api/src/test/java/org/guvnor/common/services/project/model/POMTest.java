package org.guvnor.common.services.project.model;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class POMTest {

    @Test
    public void isMultiModuleTest() {
        POM
                plainPom = new POM(),
                pomWithGav = new POM(new GAV()),
                pomWithDetails = new POM("name", "description", "url", new GAV()),
                multimodulePom = new POM("name", "description", "url", new GAV(), true);

        assertThat(plainPom.isMultiModule()).isFalse();
        assertThat(pomWithGav.isMultiModule()).isFalse();
        assertThat(pomWithDetails.isMultiModule()).isFalse();
        assertThat(multimodulePom.isMultiModule()).isTrue();
    }
}
