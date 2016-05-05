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

package org.uberfire.ext.wires.bayesian.network.parser.backend;

import java.io.InputStream;

import javax.enterprise.context.ApplicationScoped;

import org.jboss.errai.bus.server.annotations.Service;

import org.uberfire.ext.wires.bayesian.network.parser.client.builder.BayesianBuilder;
import org.uberfire.ext.wires.bayesian.network.parser.client.model.BayesNetwork;
import org.uberfire.ext.wires.bayesian.network.parser.client.parser.Bif;
import org.uberfire.ext.wires.bayesian.network.parser.client.parser.Definition;
import org.uberfire.ext.wires.bayesian.network.parser.client.parser.Network;
import org.uberfire.ext.wires.bayesian.network.parser.client.parser.Probability;
import org.uberfire.ext.wires.bayesian.network.parser.client.service.BayesianService;
import com.thoughtworks.xstream.XStream;

@Service
@ApplicationScoped
public class BayesianServiceImpl implements BayesianService {

    @Override
    public BayesNetwork buildXml03(String relativePathtoXmlResource) {
        return new BayesianBuilder().build(xmlToObject(relativePathtoXmlResource));
    }

    @Override
    public Bif xmlToObject(String relativePathtoXmlResource) {
        InputStream resourceAsStream = loadResource( relativePathtoXmlResource );
        return processXML( resourceAsStream );
    }

    private Bif processXML( InputStream resourceAsStream ) {
        XStream xstream = new XStream();
        xstream.processAnnotations(Bif.class);
        xstream.processAnnotations(Network.class);
        xstream.processAnnotations(Probability.class);
        xstream.processAnnotations(Definition.class);
        return (Bif) xstream.fromXML(resourceAsStream);
    }

    private InputStream loadResource( String xmlFileName ) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        return loader.getResourceAsStream(xmlFileName);
    }

}
