package org.kie.uberfire.wires.bayesian.network.parser.client.model;

import java.io.Serializable;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;

import com.google.common.collect.Lists;

@Portable
public class BayesNetwork implements Serializable {

    private static final long serialVersionUID = 6231201134802600033L;

    private String name;
    private List<BayesVariable> nodos;

    public BayesNetwork() {

    }

    public BayesNetwork(String name) {
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
