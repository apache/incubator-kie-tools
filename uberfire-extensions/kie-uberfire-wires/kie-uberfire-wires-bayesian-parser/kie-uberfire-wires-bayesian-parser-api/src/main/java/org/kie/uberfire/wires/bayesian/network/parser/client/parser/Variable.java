package org.kie.uberfire.wires.bayesian.network.parser.client.parser;

import java.io.Serializable;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("VARIABLE")
@Portable
public class Variable extends VariableXml implements Serializable {

    private static final long serialVersionUID = -4272919361549757485L;

    @XStreamAlias("NAME")
    private String name;

    @XStreamImplicit(itemFieldName = "OUTCOME")
    private List<String> outComes;

    @XStreamAlias("PROPERTY")
    private String position;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getOutComes() {
        return outComes;
    }

    public void setOutComes(List<String> outComes) {
        this.outComes = outComes;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

}
