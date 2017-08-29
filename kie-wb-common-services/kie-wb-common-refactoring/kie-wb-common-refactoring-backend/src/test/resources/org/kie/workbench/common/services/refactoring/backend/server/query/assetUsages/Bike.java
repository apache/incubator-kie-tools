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

public class Bike {

    private String brand;
    private String model;

    private Wheel front;
    private Wheel back;

    public Bike(String brand,
                String model,
                Wheel front,
                Wheel back) {
        this.brand = brand;
        this.model = model;
        this.front = front;
        this.back = back;
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

    public Wheel getFront() {
        return front;
    }

    public void setFront(Wheel front) {
        this.front = front;
    }

    public Wheel getBack() {
        return back;
    }

    public void setBack(Wheel back) {
        this.back = back;
    }
}
