package org.kie.uberfire.wires.bayesian.network.parser.client.parser;

import java.io.Serializable;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("PROBABILITY")
@Portable
public class Probability implements Serializable {

    private static final long serialVersionUID = 329348179543890266L;

    @XStreamAlias("FOR")
    private String for_;

    @XStreamImplicit(itemFieldName = "GIVEN")
    private List<String> given;

    @XStreamAlias("TABLE")
    private String table;

    public String getFor_() {
        return for_;
    }

    public void setFor_(String for_) {
        this.for_ = for_;
    }

    public List<String> getGiven() {
        return given;
    }

    public void setGiven(List<String> given) {
        this.given = given;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

}
