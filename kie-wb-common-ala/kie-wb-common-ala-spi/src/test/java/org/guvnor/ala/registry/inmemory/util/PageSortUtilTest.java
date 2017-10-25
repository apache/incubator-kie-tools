/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.guvnor.ala.registry.inmemory.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.guvnor.ala.pipeline.Pipeline;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Page Sort Utils Test for checking correct paging and sorting
 */
public class PageSortUtilTest {

    @Test
    public void pipelinesPagingAndSorting() {
        Pipeline pipe1 = mock(Pipeline.class);
        when(pipe1.getName()).thenReturn("pipeline 1");

        Pipeline pipe2 = mock(Pipeline.class);
        when(pipe2.getName()).thenReturn("pipeline 2");

        Pipeline pipe3 = mock(Pipeline.class);
        when(pipe3.getName()).thenReturn("pipeline 3");

        Pipeline pipe4 = mock(Pipeline.class);
        when(pipe4.getName()).thenReturn("pipeline 4");

        Pipeline pipe5 = mock(Pipeline.class);
        when(pipe5.getName()).thenReturn("pipeline 5");

        Collection<Pipeline> pipes = new ArrayList<>();
        pipes.add(pipe1);
        pipes.add(pipe2);
        pipes.add(pipe3);
        pipes.add(pipe4);
        pipes.add(pipe5);

        String sort = "name";
        //Get the first page (0) with page size 2 and sorting ascending
        List<Pipeline> pageSort = PageSortUtil.pageSort(pipes,
                                                        (Pipeline p1, Pipeline p2) -> {
                                                            switch (sort) {
                                                                case "name":
                                                                    return p1.getName().compareTo(p2.getName());
                                                                default:
                                                                    return p1.toString().compareTo(p2.toString());
                                                            }
                                                        },
                                                        0,
                                                        2,
                                                        sort,
                                                        true);

        assertEquals(2,
                     pageSort.size());
        assertEquals("pipeline 1",
                     pageSort.get(0).getName());
        assertEquals("pipeline 2",
                     pageSort.get(1).getName());

        //Get the first page (0) with page size 2 and sorting descending
        pageSort = PageSortUtil.pageSort(pipes,
                                         (Pipeline p1, Pipeline p2) -> {
                                             switch (sort) {
                                                 case "name":
                                                     return p1.getName().compareTo(p2.getName());
                                                 default:
                                                     return p1.toString().compareTo(p2.toString());
                                             }
                                         },
                                         0,
                                         2,
                                         sort,
                                         false);

        assertEquals(2,
                     pageSort.size());
        assertEquals("pipeline 2",
                     pageSort.get(0).getName());
        assertEquals("pipeline 1",
                     pageSort.get(1).getName());

        //Get the first page (1) with page size 2 and sorting ascending
        pageSort = PageSortUtil.pageSort(pipes,
                                         (Pipeline p1, Pipeline p2) -> {
                                             switch (sort) {
                                                 case "name":
                                                     return p1.getName().compareTo(p2.getName());
                                                 default:
                                                     return p1.toString().compareTo(p2.toString());
                                             }
                                         },
                                         1,
                                         2,
                                         sort,
                                         true);

        assertEquals(2,
                     pageSort.size());
        assertEquals("pipeline 3",
                     pageSort.get(0).getName());
        assertEquals("pipeline 4",
                     pageSort.get(1).getName());

        //Get the first page (1) with page size 2 and sorting descending
        pageSort = PageSortUtil.pageSort(pipes,
                                         (Pipeline p1, Pipeline p2) -> {
                                             switch (sort) {
                                                 case "name":
                                                     return p1.getName().compareTo(p2.getName());
                                                 default:
                                                     return p1.toString().compareTo(p2.toString());
                                             }
                                         },
                                         1,
                                         2,
                                         sort,
                                         false);

        assertEquals(2,
                     pageSort.size());
        assertEquals("pipeline 4",
                     pageSort.get(0).getName());
        assertEquals("pipeline 3",
                     pageSort.get(1).getName());

        //Get the first page (2) with page size 2 and sorting ascending
        pageSort = PageSortUtil.pageSort(pipes,
                                         (Pipeline p1, Pipeline p2) -> {
                                             switch (sort) {
                                                 case "name":
                                                     return p1.getName().compareTo(p2.getName());
                                                 default:
                                                     return p1.toString().compareTo(p2.toString());
                                             }
                                         },
                                         2,
                                         2,
                                         sort,
                                         false);

        assertEquals(1,
                     pageSort.size());
        assertEquals("pipeline 5",
                     pageSort.get(0).getName());

        //Get the first page (3) with page size 2 and sorting ascending
        pageSort = PageSortUtil.pageSort(pipes,
                                         (Pipeline p1, Pipeline p2) -> {
                                             switch (sort) {
                                                 case "name":
                                                     return p1.getName().compareTo(p2.getName());
                                                 default:
                                                     return p1.toString().compareTo(p2.toString());
                                             }
                                         },
                                         3,
                                         2,
                                         sort,
                                         false);

        assertEquals(0,
                     pageSort.size());

        //Get the get the first 10 elements without sorting
        pageSort = PageSortUtil.pageSort(pipes,
                                         (Pipeline p1, Pipeline p2) -> {
                                             switch (sort) {
                                                 case "name":
                                                     return p1.getName().compareTo(p2.getName());
                                                 default:
                                                     return p1.toString().compareTo(p2.toString());
                                             }
                                         },
                                         0,
                                         10,
                                         "",
                                         false);

        assertEquals(5,
                     pageSort.size());
    }
}
