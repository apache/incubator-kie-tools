/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
 */

package org.drools.workbench.screens.testscenario.backend.server.model;

import java.io.Serializable;
import java.util.List;

public class Employee implements Serializable {

    static final long serialVersionUID = 1L;

    private Salary salary;

    private List<String> jobs;

    public Employee() {
    }

    public Salary getSalary() {
        return this.salary;
    }

    public void setSalary(Salary salary) {
        this.salary = salary;
    }

    public List<String> getJobs() {
        return jobs;
    }

    public void setJobs(List<String> jobs) {
        this.jobs = jobs;
    }

    public Employee(Salary salary) {
        this.salary = salary;
    }
}