package org.kie.uberfire.wires.bayesian.network.parser.backend;

import java.io.InputStream;

import javax.enterprise.context.ApplicationScoped;

import org.jboss.errai.bus.server.annotations.Service;

import org.kie.uberfire.wires.bayesian.network.parser.client.builder.BayesianBuilder;
import org.kie.uberfire.wires.bayesian.network.parser.client.model.BayesNetwork;
import org.kie.uberfire.wires.bayesian.network.parser.client.parser.Bif;
import org.kie.uberfire.wires.bayesian.network.parser.client.parser.Definition;
import org.kie.uberfire.wires.bayesian.network.parser.client.parser.Network;
import org.kie.uberfire.wires.bayesian.network.parser.client.parser.Probability;
import org.kie.uberfire.wires.bayesian.network.parser.client.service.BayesianService;
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
