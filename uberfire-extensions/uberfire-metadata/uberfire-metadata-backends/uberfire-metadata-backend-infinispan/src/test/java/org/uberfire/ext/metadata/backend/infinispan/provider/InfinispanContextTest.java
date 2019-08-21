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
 *
 */

package org.uberfire.ext.metadata.backend.infinispan.provider;

import org.infinispan.client.hotrod.configuration.SaslQop;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.ext.metadata.backend.infinispan.exceptions.InfinispanException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class InfinispanContextTest {

    private Logger logger = LoggerFactory.getLogger(InfinispanContextTest.class);

    @Test
    public void testToSaslQoP() {
        SaslQop sqsl = InfinispanContext.toSaslQop("auth");
        assertEquals(sqsl,
                     SaslQop.AUTH);

        sqsl = InfinispanContext.toSaslQop("auth-int");
        assertEquals(sqsl,
                     SaslQop.AUTH_INT);

        sqsl = InfinispanContext.toSaslQop("auth-conf");
        assertEquals(sqsl,
                     SaslQop.AUTH_CONF);
    }

    @Test
    public void testWrongToSaslQoP() {
        try {
            SaslQop sqsl = InfinispanContext.toSaslQop("auths");
            assertEquals(sqsl,
                         SaslQop.AUTH);
            fail("auths is an invalid option");
        } catch (InfinispanException e) {
            logger.info(e.getMessage());
        }
    }

    @Test
    public void testBiuildSaslQop() {
        {
            SaslQop[] sasl = InfinispanContext.buildSaslQop("auth");

            assertThat(sasl).extracting(SaslQop::toString).contains("auth");
        }

        {
            SaslQop[] sasl = InfinispanContext.buildSaslQop("   AUTH  , auth-int,");

            assertThat(sasl).extracting(SaslQop::toString).contains("auth",
                                                                    "auth-int");
        }

        {
            try {
                SaslQop[] sasl = InfinispanContext.buildSaslQop("auths");
                fail("auths is an invalid option");
            } catch (InfinispanException e) {
                logger.info(e.getMessage());
            }
        }
    }
}