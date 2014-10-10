package org.kie.uberfire.wires.bayesian.network.parser.client.model;

import java.io.Serializable;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class BayesVariable implements Serializable {

    private static final long serialVersionUID = -6018017577352463589L;

    private String name;
    private int id;
    double[][] probabilities;
    private List<String> outcomes;
    private String type;
    double[][] position;
    private List<String> given;
    private List<BayesVariable> incomingNodes;

    public BayesVariable() {

    }

    public BayesVariable(String name, int id, List<String> outcomes, double[][] probabilities) {
        this.name = name;
        this.id = id;
        this.probabilities = probabilities;
        this.outcomes = outcomes;
    }

    public BayesVariable(String name, int id, List<String> outcomes, double[][] probabilities, List<String> given,
            double[][] position) {
        this.name = name;
        this.id = id;
        this.probabilities = probabilities;
        this.outcomes = outcomes;
        this.given = given;
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public double[][] getProbabilities() {
        return probabilities;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double[][] getPosition() {
        return position;
    }

    public void setPosition(double[][] position) {
        this.position = position;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setProbabilities(double[][] probabilities) {
        this.probabilities = probabilities;
    }

    public List<String> getOutcomes() {
        return outcomes;
    }

    public void setOutcomes(List<String> outcomes) {
        this.outcomes = outcomes;
    }

    public List<String> getGiven() {
        return given;
    }

    public void setGiven(List<String> given) {
        this.given = given;
    }

    public List<BayesVariable> getIncomingNodes() {
        return incomingNodes;
    }

    public void setIncomingNodes(List<BayesVariable> incomingNodes) {
        this.incomingNodes = incomingNodes;
    }

}
