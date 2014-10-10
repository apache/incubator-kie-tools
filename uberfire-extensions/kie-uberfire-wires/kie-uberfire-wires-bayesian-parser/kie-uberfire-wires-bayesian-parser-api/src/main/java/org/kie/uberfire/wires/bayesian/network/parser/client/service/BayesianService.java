package org.kie.uberfire.wires.bayesian.network.parser.client.service;

import org.jboss.errai.bus.server.annotations.Remote;

import org.kie.uberfire.wires.bayesian.network.parser.client.model.BayesNetwork;
import org.kie.uberfire.wires.bayesian.network.parser.client.parser.Bif;


@Remote
public interface BayesianService {

    BayesNetwork buildXml03(String relativePathtoXmlResource);

    Bif xmlToObject(String relativePathtoXmlResource);

}
