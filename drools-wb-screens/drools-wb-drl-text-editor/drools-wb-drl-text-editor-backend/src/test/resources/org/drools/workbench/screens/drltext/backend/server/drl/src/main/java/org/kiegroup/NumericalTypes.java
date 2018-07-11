/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kiegroup;

public class NumericalTypes {

    private java.math.BigDecimal bigDecimalField;
    private java.math.BigInteger bigIntegerField;
    private Byte byteField;
    private Double doubleField;
    private Float floatField;
    private Integer integerField;
    private Long longField;
    private Short shortField;
    private String result;

    public NumericalTypes() {
    }

    public NumericalTypes(java.math.BigDecimal bigDecimalField,
                          java.math.BigInteger bigIntegerField,
                          Byte byteField,
                          Double doubleField,
                          Float floatField,
                          Integer integerField,
                          Long longField,
                          Short shortField,
                          String result) {
        this.bigDecimalField = bigDecimalField;
        this.bigIntegerField = bigIntegerField;
        this.byteField = byteField;
        this.doubleField = doubleField;
        this.floatField = floatField;
        this.integerField = integerField;
        this.longField = longField;
        this.shortField = shortField;
        this.result = result;
    }

    public java.math.BigDecimal getBigDecimalField() {
        return this.bigDecimalField;
    }

    public void setBigDecimalField(java.math.BigDecimal bigDecimalField) {
        this.bigDecimalField = bigDecimalField;
    }

    public java.math.BigInteger getBigIntegerField() {
        return this.bigIntegerField;
    }

    public void setBigIntegerField(java.math.BigInteger bigIntegerField) {
        this.bigIntegerField = bigIntegerField;
    }

    public Byte getByteField() {
        return this.byteField;
    }

    public void setByteField(Byte byteField) {
        this.byteField = byteField;
    }

    public Double getDoubleField() {
        return this.doubleField;
    }

    public void setDoubleField(Double doubleField) {
        this.doubleField = doubleField;
    }

    public Float getFloatField() {
        return this.floatField;
    }

    public void setFloatField(Float floatField) {
        this.floatField = floatField;
    }

    public Integer getIntegerField() {
        return this.integerField;
    }

    public void setIntegerField(Integer integerField) {
        this.integerField = integerField;
    }

    public Long getLongField() {
        return this.longField;
    }

    public void setLongField(Long longField) {
        this.longField = longField;
    }

    public Short getShortField() {
        return this.shortField;
    }

    public void setShortField(Short shortField) {
        this.shortField = shortField;
    }

    public String getResult() {
        return this.result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}