package org.kie.uberfire.wires.bayesian.network.parser.client.parser;

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
