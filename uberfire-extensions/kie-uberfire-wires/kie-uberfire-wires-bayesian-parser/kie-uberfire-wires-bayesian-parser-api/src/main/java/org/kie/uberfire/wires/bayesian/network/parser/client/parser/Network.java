package org.kie.uberfire.wires.bayesian.network.parser.client.parser;

import java.io.Serializable;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("NETWORK")
@Portable
public class Network extends NetworkXml implements Serializable {

    private static final long serialVersionUID = 8613096385540596843L;

    @XStreamAlias("NAME")
    private String name;

    @XStreamImplicit(itemFieldName = "VARIABLE")
    private List<Variable> variables;

    @XStreamImplicit(itemFieldName = "DEFINITION")
    private List<Definition> definitions;

    public List<Variable> getVariables() {
        return variables;
    }

    public void setVariables(List<Variable> variables) {
        this.variables = variables;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Definition> getDefinitions() {
        return definitions;
    }

    public void setDefinitions(List<Definition> definitions) {
        this.definitions = definitions;
    }

}
