package org.kie.uberfire.wires.bayesian.network.parser.client.parser;

import java.io.Serializable;

import org.jboss.errai.common.client.api.annotations.Portable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("BIF")
@Portable
public class Bif implements Serializable {

    private static final long serialVersionUID = -4817544750207015779L;

    @XStreamAlias("NETWORK")
    private Network network;

    public Network getNetwork() {
        return network;
    }

    public void setNetwork(Network network) {
        this.network = network;
    }

}
