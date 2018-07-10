/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.config;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.dashbuilder.test.BaseCDITest;
import org.dashbuilder.test.ShrinkWrapHelper;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.dashbuilder.pojo.Bean;
import org.dashbuilder.pojo.BeanExt;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
@Ignore("see https://issues.jboss.org/browse/RHPAM-832")
public class ConfigAnnotationTest extends BaseCDITest {

    @Inject
    protected Bean bean;

    @Test
    public void checkBean() {
        assertThat(bean.propString1).isEqualTo("From class");
        assertThat(bean.propString2).isEqualTo("From beans.config");
        assertThat(bean.propString3).isEqualTo("From Bean.config");
        assertThat(((BeanExt) bean).propString4).isEqualTo("From class");
        assertThat(((BeanExt) bean).propString5).isEqualTo("From beans.config");
        assertThat(((BeanExt) bean).propString6).isEqualTo("From BeanExt.config");
        assertThat(bean.propMap4.size()).isEqualTo(3);
        assertThat(bean.propMap4.get("a")).isEqualTo("1");
        assertThat(bean.propMap4.get("b")).isEqualTo("2");
        assertThat(bean.propMap4.get("c")).isEqualTo("3");
        assertThat(bean.props5.size()).isEqualTo(3);
        assertThat(bean.props5.getProperty("a")).isEqualTo("1");
        assertThat(bean.props5.getProperty("b")).isEqualTo("2");
        assertThat(bean.props5.getProperty("c")).isEqualTo("3");
    }
}
