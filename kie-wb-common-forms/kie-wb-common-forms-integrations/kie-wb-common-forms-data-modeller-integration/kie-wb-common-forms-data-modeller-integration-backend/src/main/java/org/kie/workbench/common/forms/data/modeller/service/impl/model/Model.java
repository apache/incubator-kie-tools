package org.kie.workbench.common.forms.data.modeller.service.impl.model;

import java.util.List;

public class Model {

    private String aString;

    private Integer aInteger;

    private Double aDouble;

    private Long aLong;

    private Boolean aBoolean;

    private List aList;

    private ModelEnum anEnum;

    public Model() {
    }

    public String getaString() {
        return aString;
    }

    public void setaString(String aString) {
        this.aString = aString;
    }

    public Integer getaInteger() {
        return aInteger;
    }

    public void setaInteger(Integer aInteger) {
        this.aInteger = aInteger;
    }

    public Double getaDouble() {
        return aDouble;
    }

    public void setaDouble(Double aDouble) {
        this.aDouble = aDouble;
    }

    public Long getaLong() {
        return aLong;
    }

    public void setaLong(Long aLong) {
        this.aLong = aLong;
    }

    public Boolean getaBoolean() {
        return aBoolean;
    }

    public void setaBoolean(Boolean aBoolean) {
        this.aBoolean = aBoolean;
    }

    public List getaList() {
        return aList;
    }

    public void setaList(List aList) {
        this.aList = aList;
    }

    public ModelEnum getAnEnum() {
        return anEnum;
    }

    public void setAnEnum(ModelEnum anEnum) {
        this.anEnum = anEnum;
    }
}
