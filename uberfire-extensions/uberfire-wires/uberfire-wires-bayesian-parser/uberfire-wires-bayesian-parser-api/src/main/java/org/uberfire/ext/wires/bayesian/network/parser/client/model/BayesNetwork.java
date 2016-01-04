/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.wires.bayesian.network.parser.client.model;

import java.io.Serializable;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

import com.google.common.collect.Lists;

@Portable
public class BayesNetwork implements Serializable {

    private static final long serialVersionUID = 6231201134802600033L;

    private String name;
    private List<BayesVariable> nodos;

    public BayesNetwork( @MapsTo("name") String name ) {
        this.nodos = Lists.newArrayList();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<BayesVariable> getNodos() {
        return nodos;
    }

    public void setNodos(List<BayesVariable> nodos) {
        this.nodos = nodos;
    }

}
