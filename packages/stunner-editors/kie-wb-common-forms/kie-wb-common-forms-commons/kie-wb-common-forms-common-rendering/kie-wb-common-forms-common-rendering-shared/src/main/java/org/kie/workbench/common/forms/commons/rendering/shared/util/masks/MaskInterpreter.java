/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.forms.commons.rendering.shared.util.masks;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.Assert;

/**
 * Generates String representations for Objects based on a mask String. That mask defines the pattern of the resultant
 * String representation.
 * <p>
 * The mask String must contain the object fieldnames that are going to be contained on the result String surrounded by
 * the characters '{' & '}' (e.g. <i>"{field1}"</i>).
 * It also can contain
 * <p>
 * For example:
 * <blockquote><pre>
 *     MaskInterpreter<Author> interpreter = new MaskInterpreter<Author>( "{lastName}, {name} ({born} - {dead})" );
 *     System.out.println( interpreter.render( new Author( "William", "Shakespeare", 1564, 1616 ) ) );
 * </pre></blockquote>
 * <p>
 * Will print on the following text:
 * <blockquote><pre>
 *     Shakespeare, William (1564 - 1616)
 * </pre></blockquote>
 */
public abstract class MaskInterpreter<T> {

    protected List<MaskSection> sections = new ArrayList<>();

    protected String mask;

    /**
     * Constructs the MaskInterpreter object using the given String as a mask
     * @throws NullPointerException If the mask is null
     * @throws IllegalArgumentException If the mask has wrong format
     */
    public MaskInterpreter(String mask) {
        Assert.notNull("Mask cannot be null!",
                       mask);
        this.mask = mask;
        if (isValid(mask)) {
            while (mask.contains("{") && mask.contains("}")) {
                int open = mask.indexOf("{");
                int close = mask.indexOf("}");

                String text = mask.substring(0,
                                             open);
                String property = mask.substring(open + 1,
                                                 close);

                if (!text.isEmpty()) {
                    sections.add(new MaskSectionImpl(MaskSectionType.LITERAL,
                                                     text));
                }

                sections.add(new MaskSectionImpl(MaskSectionType.PROPERTY,
                                                 property));

                mask = mask.substring(close + 1);
            }
            if (!mask.isEmpty()) {
                sections.add(new MaskSectionImpl(MaskSectionType.LITERAL,
                                                 mask));
            }
        } else {
            throw new IllegalArgumentException("Invalid mask!");
        }
    }

    protected boolean isValid(String mask) {
        if (mask == null) {
            return false;
        }

        if (mask.isEmpty()) {
            return true;
        }

        int countOpeners = 0;

        int countClosers = 0;

        for (char c : mask.toCharArray()) {
            if (c == '{') {
                countOpeners++;
            } else if (c == '}') {
                countClosers++;
            }
        }

        return countOpeners != 0 && countOpeners == countClosers;
    }

    /**
     * Generates the String representation for the given Object
     */
    public String render(T model) {

        if (model == null) {
            return "";
        }

        if (sections.isEmpty()) {
            return model.toString();
        }

        ModelInterpreter<T> interpreter = getModelInterpreter(model);

        String result = "";

        for (MaskSection section : sections) {
            String value = section.getText();
            if (section.getType().equals(MaskSectionType.PROPERTY)) {
                Object propertyValue = interpreter.getPropertyValue(section.getText());
                if (propertyValue != null) {
                    value = propertyValue.toString();
                } else {
                    value = "";
                }
            }

            result += value;
        }

        return result;
    }

    protected abstract ModelInterpreter<T> getModelInterpreter(T model);

    /**
     * Returns a list containing all the mask fragments
     */
    public List<MaskSection> getSections() {
        return sections;
    }

    /**
     * Returns the mask used to initialize the interpreter.
     */
    public String getMask() {
        return mask;
    }

    private class MaskSectionImpl implements MaskSection {

        private MaskSectionType type;
        private String text;

        public MaskSectionImpl(MaskSectionType type,
                               String text) {
            this.type = type;
            this.text = text;
        }

        @Override
        public MaskSectionType getType() {
            return type;
        }

        @Override
        public String getText() {
            return text;
        }
    }
}
