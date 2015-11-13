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

package org.uberfire.ext.wires.bayesian.network.parser.client.parser;

import java.io.Serializable;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;

import com.thoughtworks.xstream.annotations.XStreamImplicit;

@Portable
public class NetworkXml implements Serializable {

    private static final long serialVersionUID = -3348355473054506395L;

    @XStreamImplicit(itemFieldName = "PROBABILITY")
    private List<Probability> probabilities;

    public List<Probability> getProbabilities() {
        return probabilities;
    }

    public void setProbabilities(List<Probability> probabilities) {
        this.probabilities = probabilities;
    }

}
