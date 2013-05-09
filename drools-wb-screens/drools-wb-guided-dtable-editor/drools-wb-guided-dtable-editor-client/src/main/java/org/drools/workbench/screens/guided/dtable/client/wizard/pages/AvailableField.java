/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.screens.guided.dtable.client.wizard.pages;

/**
 * A Pattern Field available for selection
 */
public class AvailableField {

    private final String name;

    private final String type;

    private final String displayType;

    private final int calculationType;

    public AvailableField( final String name,
                           final int calculationType ) {
        this( name,
              null,
              null,
              calculationType );
    }

    public AvailableField( final String name,
                           final String type,
                           final String displayType,
                           final int calculationType ) {
        this.name = name;
        this.type = type;
        this.displayType = displayType;
        this.calculationType = calculationType;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getDisplayType() {
        return this.displayType;
    }

    public int getCalculationType() {
        return this.calculationType;
    }

}
