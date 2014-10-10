package org.kie.uberfire.wires.bayesian.network.parser.client.parser;

import java.io.Serializable;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("DEFINITION")
@Portable
public class Definition implements Serializable {

    private static final long serialVersionUID = -4549156706417732124L;

    @XStreamAlias("FOR")
    private String name;

    @XStreamImplicit(itemFieldName = "GIVEN")
    private List<String> given;

    @XStreamAlias("TABLE")
    private String probabilities;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getGiven() {
        return given;
    }

    public void setGiven(List<String> given) {
        this.given = given;
    }

    public String getProbabilities() {
        return probabilities;
    }

    public void setProbabilities(String probabilities) {
        this.probabilities = probabilities;
    }

}
