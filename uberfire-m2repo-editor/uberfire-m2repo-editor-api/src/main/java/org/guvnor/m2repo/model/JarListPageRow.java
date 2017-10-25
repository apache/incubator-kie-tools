/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.m2repo.model;

import java.util.Date;

import org.guvnor.common.services.project.model.GAV;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.paging.AbstractPageRow;

/**
 * A single row of a paged data
 */
@Portable
public class JarListPageRow extends AbstractPageRow {

    private String name;
    private String path;
    private GAV gav;
    private Date lastModified;
    private String repository;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public GAV getGav() {
        return gav;
    }

    public void setGav(GAV gav) {
        this.gav = gav;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public String getRepositoryName() {
        return repository;
    }

    public void setRepositoryName(final String repository) {
        this.repository = repository;
    }
}
