/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.guvnor.common.services.project.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.jboss.errai.common.client.api.annotations.Portable;

import static java.util.stream.Collectors.toList;

@Portable
public class Dependencies
        implements List<Dependency> {

    private final List<Dependency> dependencies;

    public Dependencies() {
        dependencies = new ArrayList<>();
    }

    public Dependencies(final List<Dependency> dependencies) {
        this.dependencies = dependencies;
    }

    public boolean containsDependency(final GAV other) {
        for (Dependency dependency : dependencies) {
            if (dependency.isGAVEqual(other)) {
                return true;
            }
        }
        return false;
    }

    public Dependency get(final GAV gav) {
        for (Dependency dependency : dependencies) {
            if (dependency.isGAVEqual(gav)) {
                return dependency;
            }
        }

        return null;
    }

    public Collection<GAV> getGavs(final String... scopes) {
        final List<String> scopesList = Arrays.asList(scopes);

        if (scopesList.isEmpty()) {
            return new ArrayList<>(dependencies);
        } else {
            return dependencies.stream()
                    .filter(dep -> scopesList.contains(dep.getScope()))
                    .collect(toList());
        }
    }

    public Collection<GAV> getCompileScopedGavs() {
        return getGavs("compile",
                       // When scope is not declared (dependency.getScope() == null), maven considers it to be compile scope
                       null
        );
    }

    @Override
    public int size() {
        return dependencies.size();
    }

    @Override
    public boolean isEmpty() {
        return dependencies.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return dependencies.contains(o);
    }

    @Override
    public Iterator<Dependency> iterator() {
        return dependencies.iterator();
    }

    @Override
    public Object[] toArray() {
        return dependencies.toArray();
    }

    @Override
    public <T> T[] toArray(T[] ts) {
        return dependencies.toArray(ts);
    }

    @Override
    public boolean add(Dependency dependency) {
        return dependencies.add(dependency);
    }

    @Override
    public boolean remove(Object o) {
        return dependencies.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return dependencies.containsAll(collection);
    }

    @Override
    public boolean addAll(Collection<? extends Dependency> collection) {
        return dependencies.addAll(collection);
    }

    @Override
    public boolean addAll(int i,
                          Collection<? extends Dependency> collection) {
        return dependencies.addAll(i,
                                   collection);
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        return dependencies.removeAll(collection);
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        return dependencies.retainAll(collection);
    }

    @Override
    public void clear() {
        dependencies.clear();
    }

    @Override
    public Dependency get(int i) {
        return dependencies.get(i);
    }

    @Override
    public Dependency set(int i,
                          Dependency dependency) {
        return dependencies.set(i, dependency);
    }

    @Override
    public void add(int i, Dependency dependency) {
        dependencies.add(i, dependency);
    }

    @Override
    public Dependency remove(int i) {
        return dependencies.remove(i);
    }

    @Override
    public int indexOf(Object o) {
        return dependencies.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return dependencies.lastIndexOf(o);
    }

    @Override
    public ListIterator<Dependency> listIterator() {
        return dependencies.listIterator();
    }

    @Override
    public ListIterator<Dependency> listIterator(int i) {
        return dependencies.listIterator(i);
    }

    @Override
    public List<Dependency> subList(int i,
                                    int i1) {
        return dependencies.subList(i, i1);
    }
}
