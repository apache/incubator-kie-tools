/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.examples.model;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class ExamplesModel {

    private ExampleRepository sourceRepository;
    private ExampleTargetRepository targetRepository;
    private ExampleOrganizationalUnit targetOrganizationalUnit;
    private List<ExampleProject> projects = new ArrayList<ExampleProject>();

    public ExampleRepository getSourceRepository() {
        return sourceRepository;
    }

    public void setSourceRepository( final ExampleRepository sourceRepository ) {
        this.sourceRepository = sourceRepository;
    }

    public ExampleTargetRepository getTargetRepository() {
        return targetRepository;
    }

    public void setTargetRepository( final ExampleTargetRepository targetRepository ) {
        this.targetRepository = targetRepository;
    }

    public ExampleOrganizationalUnit getTargetOrganizationalUnit() {
        return targetOrganizationalUnit;
    }

    public void setTargetOrganizationalUnit( final ExampleOrganizationalUnit targetOrganizationalUnit ) {
        this.targetOrganizationalUnit = targetOrganizationalUnit;
    }

    public List<ExampleProject> getProjects() {
        return projects;
    }

}
