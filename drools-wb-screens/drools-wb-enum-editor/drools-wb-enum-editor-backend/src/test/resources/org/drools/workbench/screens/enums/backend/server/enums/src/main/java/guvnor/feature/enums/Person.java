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

package guvnor.feature.enums;

// $HASH(ee15bb4bdabae23dd989493b198c753e) (added manually)
public class Person {
    
    private String sex;
    
    private int age;
    
    private String name;
    
    private int value;
    
    private int dummy;

    public Person() {
    }

    public Person(int value, String name, int dummy, String sex, int age) {
        this.value = value;
        this.name = name;
        this.dummy = dummy;
        this.sex = sex;
        this.age = age;
    }

    public String getSex() {
        return this.sex;
    }

    public void setSex( String sex ) {
        this.sex = sex;
    }
    
    public int getAge() {
        return this.age;
    }

    public void setAge(int age ) {
        this.age = age;
    }
    
    public String getName() {
        return this.name;
    }

    public void setName(String name ) {
        this.name = name;
    }
    
    public int getValue() {
        return this.value;
    }

    public void setValue(int value ) {
        this.value = value;
    }
    
    public int getDummy() {
        return this.dummy;
    }

    public void setDummy(int dummy ) {
        this.dummy = dummy;
    }
}
