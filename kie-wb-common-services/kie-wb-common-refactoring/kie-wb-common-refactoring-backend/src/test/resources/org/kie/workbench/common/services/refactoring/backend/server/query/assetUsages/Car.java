/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.refactoring.backend.server.query.assetUsages;

public class Car {
    private String brand;
    private String model;
    private Wheel frontLeft;
    private Wheel frontRight;
    private Wheel backLeft;
    private Wheel backRight;

    public Car(String brand,
               String model,
               Wheel frontLeft,
               Wheel frontRight,
               Wheel backLeft,
               Wheel backRight) {
        this.brand = brand;
        this.model = model;
        this.frontLeft = frontLeft;
        this.frontRight = frontRight;
        this.backLeft = backLeft;
        this.backRight = backRight;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Wheel getFrontLeft() {
        return frontLeft;
    }

    public void setFrontLeft(Wheel frontLeft) {
        this.frontLeft = frontLeft;
    }

    public Wheel getFrontRight() {
        return frontRight;
    }

    public void setFrontRight(Wheel frontRight) {
        this.frontRight = frontRight;
    }

    public Wheel getBackLeft() {
        return backLeft;
    }

    public void setBackLeft(Wheel backLeft) {
        this.backLeft = backLeft;
    }

    public Wheel getBackRight() {
        return backRight;
    }

    public void setBackRight(Wheel backRight) {
        this.backRight = backRight;
    }
}
