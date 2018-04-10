/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.uberfire.ext.metadata.io;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.data.Pair;
import org.uberfire.ext.metadata.engine.Indexer;
import org.uberfire.ext.metadata.engine.IndexerScheduler;
import org.uberfire.ext.metadata.event.IndexEvent;

import static java.util.Comparator.comparingInt;
import static java.util.concurrent.CompletableFuture.allOf;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static java.util.stream.Collectors.toCollection;
import static org.uberfire.commons.data.Pair.newPair;

/**
 * Schedules {@link Indexer} jobs asynchronously based on assigned priorites and dependency
 * relations given for indexer IDs.
 *
 * @see ConstraintBuilder#addConstraint(String, String)
 * @see ConstraintBuilder#addPriority(String, int)
 */
public class ConstrainedIndexerScheduler implements IndexerScheduler {

    private static final Logger logger = LoggerFactory.getLogger(ConstrainedIndexerScheduler.class);

    private final OrderingGraph<JobNode> graph;

    private ConstrainedIndexerScheduler(OrderingGraph<JobNode> graph) {
        this.graph = graph;
    }

    @Override
    public Stream<CompletableFuture<Pair<String, List<IndexEvent>>>> schedule(ExecutorService executor) {
        Map<String, CompletableFuture<Pair<String, List<IndexEvent>>>> createdJobs = new HashMap<>();

        return graph.nodesById.values()
                              .stream()
                              .sorted(comparingInt(node -> node.priority))
                              .map(node -> node.id)
                              .map(id -> schedule(executor, createdJobs, id));
    }

    private CompletableFuture<Pair<String, List<IndexEvent>>> schedule(ExecutorService executor,
                                                                       Map<String, CompletableFuture<Pair<String, List<IndexEvent>>>> createdJobs,
                                                                       String id) {
        if (createdJobs.containsKey(id)) {
            logger.debug("Job [{}] already scheduled. Returning future.", id);
            return createdJobs.get(id);
        } else {
            logger.debug("Job [{}] not yet scheduled.", id);
            final JobNode jobNode = graph.nodesById.get(id);
            final CompletableFuture<?>[] dependencies =
                    graph.edgesById.get(id)
                                   .stream()
                                   .filter(constraint -> constraint.isFrom(id))
                                   .map(constraint -> constraint.to)
                                   .map(dependencyId -> schedule(executor, createdJobs, dependencyId))
                                   .toArray(n -> new CompletableFuture[n]);
            logger.debug("Dependencies scheduled. Scheduling job for [{}].", id);
            final CompletableFuture<Pair<String, List<IndexEvent>>> jobFuture =
                    allOf(dependencies).thenCompose(ignore -> supplyAsync(jobNode.job, executor).thenApply(events -> newPair(jobNode.id, events)));
            createdJobs.put(id, jobFuture);

            return jobFuture;
        }
    }

    private static class OrderingNode {

        final String id;
        final int priority;

        OrderingNode(String id, int priority) {
            this.id = id;
            this.priority = priority;
        }
    }

    private static class JobNode extends OrderingNode {

        final Supplier<List<IndexEvent>> job;

        JobNode(String id, int priority, Supplier<List<IndexEvent>> job) {
            super(id, priority);
            this.job = job;
        }
    }

    private static class OrderingGraph<T> {

        final Map<String, List<Constraint>> edgesById = new HashMap<>();
        final Map<String, T> nodesById = new HashMap<>();
    }

    private static class Constraint {
        private final String from;
        private final String to;

        Constraint(String from, String to) {
            this.from = from;
            this.to = to;
        }

        boolean isFrom(String id) {
            return from.equals(id);
        }
    }

    /**
     * Builder for defining priorities and dependencies of {@link Indexer} jobs.
     */
    public static class ConstraintBuilder {

        Map<String, Integer> priorities = new HashMap<>();
        Map<String, List<Constraint>> constraints = new HashMap<>();

        /**
         * Assigns a priority to the indexer with the given ID. Lower numbers mean that the
         * indexer will be scheduled to run earlier (but always after indexers it depends on via {@link #addConstraint(String, String)}).
         *
         * @param indexerId The ID of an indexer. Must not be null.
         * @param priority The priority assigned to this indexer. Lower numbers mean earlier scheduling.
         * @return This builder. Never null.
         *
         * @see #addConstraint(String, String)
         */
        public ConstraintBuilder addPriority(String indexerId, int priority) {
            priorities.put(indexerId, priority);
            constraints.computeIfAbsent(indexerId, id -> new ArrayList<>());
            return this;
        }

        /**
         * Assigns a constraint where one indexer must run after another indexer.
         *
         * @param fromIndexerId The indexer that must complete first. Must not be null.
         * @param toIndexerId The indexer that must start after the former completes. Must not be null.
         * @return This builder. Never null.
         */
        public ConstraintBuilder addConstraint(String fromIndexerId, String toIndexerId) {
            Constraint constraint = new Constraint(fromIndexerId, toIndexerId);
            constraints.computeIfAbsent(fromIndexerId, id -> new ArrayList<>()).add(constraint);
            constraints.computeIfAbsent(toIndexerId, id -> new ArrayList<>()).add(constraint);
            return this;
        }

        public IndexerScheduler.Factory createFactory() {
            final Set<String> visited = new HashSet<>();
            final Set<String> visiting = new LinkedHashSet<>();
            final OrderingGraph<OrderingNode> graph = new OrderingGraph<>();

            populateAndValidateGraph(graph, visited, visiting);

            return new SchedulerFactory(graph);
        }

        private void populateAndValidateGraph(OrderingGraph<OrderingNode> graph,
                                              Set<String> visited,
                                              Set<String> visiting) {
            for (final String id : constraints.keySet()) {
                populateAndValidateGraph(graph, visited, visiting, id);
            }
        }

        private void populateAndValidateGraph(OrderingGraph<OrderingNode> graph,
                                              Set<String> visited,
                                              Set<String> visiting,
                                              String id) {
            if (visiting.contains(id)) {
                throw new IllegalArgumentException("Cannot have cycles in constraints: " + visiting);
            } else if (!visited.contains(id)) {
                visiting.add(id);
                graph.nodesById.put(id, new OrderingNode(id, priorities.getOrDefault(id, 0)));
                constraints.get(id)
                           .stream()
                           .filter(constraint -> constraint.isFrom(id))
                           .forEach(constraint -> {
                               graph.edgesById.computeIfAbsent(id, ignore -> new ArrayList<>())
                                              .add(constraint);
                               populateAndValidateGraph(graph, visited, visiting, constraint.to);
                           });
                visiting.remove(id);
                visited.add(id);
            }
        }
    }

    private static class SchedulerFactory implements IndexerScheduler.Factory {

        private final OrderingGraph<OrderingNode> graph;

        SchedulerFactory(OrderingGraph<OrderingNode> graph) {
            this.graph = graph;
        }

        @Override
        public IndexerScheduler create(Map<String, ? extends Supplier<List<IndexEvent>>> jobsByIndexerId) {
            final OrderingGraph<JobNode> jobGraph = new OrderingGraph<>();
            final Set<String> missingJobs = new HashSet<>();
            jobsByIndexerId.forEach((id, job) -> {
                final int priority = Optional.ofNullable(graph.nodesById.get(id)).map(node -> node.priority).orElse(0);
                jobGraph.nodesById.put(id, new JobNode(id, priority, job));
                List<Constraint> constraints = graph.edgesById.getOrDefault(id, Collections.emptyList());
                findMissingDependencies(jobsByIndexerId, missingJobs, id, constraints);
                copyValidConstraints(jobsByIndexerId, jobGraph, id, constraints);
            });

            if (missingJobs.isEmpty()) {
                return new ConstrainedIndexerScheduler(jobGraph);
            } else {
                throw new IllegalArgumentException("Cannot schedule jobs without missing dependencies: " + missingJobs);
            }

        }

        private void copyValidConstraints(Map<String, ? extends Supplier<List<IndexEvent>>> jobsByIndexerId,
                                          final OrderingGraph<JobNode> jobGraph,
                                          String id,
                                          List<Constraint> constraints) {
            constraints.stream()
                       .filter(c -> jobsByIndexerId.containsKey(c.from) && jobsByIndexerId.containsKey(c.to))
                       .collect(toCollection(() -> jobGraph.edgesById.computeIfAbsent(id, ignore -> new ArrayList<>())));
        }

        private void findMissingDependencies(Map<String, ? extends Supplier<List<IndexEvent>>> jobsByIndexerId,
                                             Set<String> missingJobs,
                                             String id,
                                             List<Constraint> constraints) {
            constraints.stream()
                       .filter(c -> c.isFrom(id) && !jobsByIndexerId.containsKey(c.to))
                       .map(c -> c.to)
                       .collect(Collectors.toCollection(() -> missingJobs));
        }

    }
}
