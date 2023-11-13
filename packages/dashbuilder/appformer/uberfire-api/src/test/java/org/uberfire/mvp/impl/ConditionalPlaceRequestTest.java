/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.uberfire.mvp.impl;

import org.junit.Test;
import org.uberfire.mvp.PlaceRequest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class ConditionalPlaceRequestTest {

    @Test
    public void conditionalTestTruePredicate() {
        DefaultPlaceRequest other = new DefaultPlaceRequest("other");
        ConditionalPlaceRequest dora = (ConditionalPlaceRequest) new ConditionalPlaceRequest("dora").when(p -> true)
                .orElse(other);
        assertEquals(dora,
                     dora.resolveConditionalPlaceRequest());
    }

    @Test
    public void conditionalTestFalsePredicate() {
        DefaultPlaceRequest other = new DefaultPlaceRequest("other");
        ConditionalPlaceRequest dora = (ConditionalPlaceRequest) new ConditionalPlaceRequest("dora").when(p -> false)
                .orElse(other);
        assertEquals(other,
                     dora.resolveConditionalPlaceRequest());
    }

    @Test
    public void incompleteConditionalShouldReturnDefaultPlaceRequest() {
        ConditionalPlaceRequest dora = new ConditionalPlaceRequest("dora");
        assertEquals("dora",
                     dora.resolveConditionalPlaceRequest().getIdentifier());
    }

    @Test
    public void incompleteConditionalWithNullsShouldReturnDefaultPlaceRequest() {
        ConditionalPlaceRequest dora = (ConditionalPlaceRequest) new ConditionalPlaceRequest("dora").when(null).orElse(null);
        assertEquals("dora",
                     dora.resolveConditionalPlaceRequest().getIdentifier());
    }

    @Test
    public void conditionalTestChainingPredicate() {
        DefaultPlaceRequest my = new DefaultPlaceRequest("my");
        ConditionalPlaceRequest bento = (ConditionalPlaceRequest) new ConditionalPlaceRequest("bento").when(p -> false)
                .orElse(my);
        ConditionalPlaceRequest dora = (ConditionalPlaceRequest) new ConditionalPlaceRequest("dora").when(p -> false)
                .orElse(bento);
        assertEquals(my,
                     dora.resolveConditionalPlaceRequest());
    }

    @Test
    public void conditionalTestChainingPredicateReturningConditional() {
        DefaultPlaceRequest my = new DefaultPlaceRequest("my");
        ConditionalPlaceRequest bento = (ConditionalPlaceRequest) new ConditionalPlaceRequest("bento").when(p -> true)
                .orElse(my);
        ConditionalPlaceRequest dora = (ConditionalPlaceRequest) new ConditionalPlaceRequest("dora").when(p -> false)
                .orElse(bento);
        assertEquals("bento",
                     dora.resolveConditionalPlaceRequest().getIdentifier());
    }

    @Test
    public void defaultPlaceRequestEqualsToConditionalPlaceRequest() {
        final DefaultPlaceRequest defaultPlaceRequest = new DefaultPlaceRequest("my");
        final PlaceRequest conditionalPlaceRequest = new ConditionalPlaceRequest("my").when(p -> true).orElse(new DefaultPlaceRequest("other"));

        equals(defaultPlaceRequest,
               conditionalPlaceRequest);
    }

    @Test
    public void defaultPlaceRequestNotEqualsToConditionalPlaceRequest() {
        final DefaultPlaceRequest defaultPlaceRequest = new DefaultPlaceRequest("my");
        final PlaceRequest conditionalPlaceRequest = new ConditionalPlaceRequest("my").when(p -> false).orElse(new DefaultPlaceRequest("other"));

        notEquals(defaultPlaceRequest,
                  conditionalPlaceRequest);
    }

    @Test
    public void defaultPlaceRequestEqualsToIncompleteConditionalPlaceRequest() {
        final DefaultPlaceRequest defaultPlaceRequest = new DefaultPlaceRequest("my");
        final PlaceRequest conditionalPlaceRequest = new ConditionalPlaceRequest("my");

        equals(defaultPlaceRequest,
               conditionalPlaceRequest);
    }

    @Test
    public void trueEvaluatedConditionalPlaceRequestEqualsToAnotherTrueEvaluatedConditionalPlaceRequest() {
        final PlaceRequest conditionalPlaceRequest1 = new ConditionalPlaceRequest("place1").when(p -> true).orElse(new DefaultPlaceRequest("place2"));
        final PlaceRequest conditionalPlaceRequest2 = new ConditionalPlaceRequest("place1").when(p -> true).orElse(new DefaultPlaceRequest("place3"));

        equals(conditionalPlaceRequest1,
               conditionalPlaceRequest2);
    }

    @Test
    public void falseEvaluatedConditionalPlaceRequestEqualsToAnotherFalseEvaluatedConditionalPlaceRequest() {
        final PlaceRequest conditionalPlaceRequest1 = new ConditionalPlaceRequest("place1").when(p -> false).orElse(new DefaultPlaceRequest("place2"));
        final PlaceRequest conditionalPlaceRequest2 = new ConditionalPlaceRequest("place3").when(p -> false).orElse(new DefaultPlaceRequest("place2"));

        equals(conditionalPlaceRequest1,
               conditionalPlaceRequest2);
    }

    @Test
    public void trueEvaluatedConditionalPlaceRequestEqualsToAnotherFalseEvaluatedConditionalPlaceRequest() {
        final PlaceRequest conditionalPlaceRequest1 = new ConditionalPlaceRequest("place1").when(p -> true).orElse(new DefaultPlaceRequest("place2"));
        final PlaceRequest conditionalPlaceRequest2 = new ConditionalPlaceRequest("place3").when(p -> false).orElse(new DefaultPlaceRequest("place1"));

        equals(conditionalPlaceRequest1,
               conditionalPlaceRequest2);
    }

    @Test
    public void falseEvaluatedConditionalPlaceRequestEqualsToAnotherTrueEvaluatedConditionalPlaceRequest() {
        final PlaceRequest conditionalPlaceRequest1 = new ConditionalPlaceRequest("place1").when(p -> false).orElse(new DefaultPlaceRequest("place2"));
        final PlaceRequest conditionalPlaceRequest2 = new ConditionalPlaceRequest("place2").when(p -> true).orElse(new DefaultPlaceRequest("place3"));

        equals(conditionalPlaceRequest1,
               conditionalPlaceRequest2);
    }

    private void equals(final PlaceRequest placeRequest1,
                        final PlaceRequest placeRequest2) {
        assertTrue(placeRequest1.equals(placeRequest2));
        assertTrue(placeRequest2.equals(placeRequest1));
        assertEquals(placeRequest1.hashCode(),
                     placeRequest2.hashCode());
    }

    private void notEquals(final PlaceRequest placeRequest1,
                           final PlaceRequest placeRequest2) {
        assertFalse(placeRequest1.equals(placeRequest2));
        assertFalse(placeRequest2.equals(placeRequest1));
        assertNotEquals(placeRequest1.hashCode(),
                        placeRequest2.hashCode());
    }
}