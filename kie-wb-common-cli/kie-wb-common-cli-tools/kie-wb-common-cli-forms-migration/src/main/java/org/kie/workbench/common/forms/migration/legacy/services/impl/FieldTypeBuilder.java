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
package org.kie.workbench.common.forms.migration.legacy.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.kie.workbench.common.forms.migration.legacy.model.FieldType;

public class FieldTypeBuilder {

    public static final String INPUT_TEXT = "InputText";
    public static final String INPUT_TEXT_CHARACTER = "InputTextCharacter";
    public static final String INPUT_TEXT_CHAR = "InputTextPrimitiveCharacter";

    public static final String INPUT_TEXT_AREA = "InputTextArea";
    public static final String HTML_EDITOR = "HTMLEditor";

    public static final String INPUT_TEXT_FLOAT = "InputTextFloat";
    public static final String INPUT_TEXT_PRIMITIVE_FLOAT = "InputTextPrimitiveFloat";
    public static final String INPUT_TEXT_DOUBLE = "InputTextDouble";
    public static final String INPUT_TEXT_PRIMITIVE_DOUBLE = "InputTextPrimitiveDouble";
    public static final String INPUT_TEXT_BIG_DECIMAL = "InputTextBigDecimal";

    public static final String INPUT_TEXT_BIG_INTEGER = "InputTextBigInteger";
    public static final String INPUT_TEXT_BYTE = "InputTextByte";
    public static final String INPUT_TEXT_PRIMITIVE_BYTE = "InputTextPrimitiveByte";
    public static final String INPUT_TEXT_SHORT = "InputTextShort";
    public static final String INPUT_TEXT_PRIMITIVE_SHORT = "InputTextPrimitiveShort";
    public static final String INPUT_TEXT_INTEGER = "InputTextInteger";
    public static final String INPUT_TEXT_PRIMITIVE_INTEGER = "InputTextPrimitiveInteger";
    public static final String INPUT_TEXT_LONG = "InputTextLong";
    public static final String INPUT_TEXT_PRIMITIVE_LONG = "InputTextPrimitiveLong";

    public static final String MULTIPLE_INPUT = "MultipleInput";

    public static final String CHECKBOX = "CheckBox";
    public static final String PRIMITIVE_CHECKBOX = "CheckBoxPrimitiveBoolean";

    public static final String INPUT_DATE = "InputDate";
    public static final String INPUT_SHORT_DATE = "InputShortDate";

    public static final String SELECT_BOX = "SelectBox";
    public static final String RADIO_GROUP = "RadioGroup";

    public static final String SUBFORM = "Subform";
    public static final String MULTIPLE_SUBFORM = "MultipleSubform";

    public static final String HTML_LABEL = "HTMLLabel";
    public static final String SEPARATOR = "Separator";

    public static final String DOCUMENT = "Document";

    // Unsupported types
    public static final String INPUT_TEXT_EMAIL = "InputTextEmail";

    public static List<FieldType> getSimpleFieldTypes() {

        List<FieldType> result = new ArrayList<>();

        FieldType ft = new FieldType();
        ft.setCode(INPUT_TEXT);
        ft.setFieldClass("java.lang.String");
        ft.setManagerClass("org.jbpm.formModeler.core.processing.fieldHandlers.InputTextFieldHandler");
        ft.setMaxlength(new Long(4000));
        ft.setSize("25");
        result.add(ft);

        ft = new FieldType();
        ft.setCode(INPUT_TEXT_AREA);
        ft.setFieldClass("java.lang.String");
        ft.setManagerClass("org.jbpm.formModeler.core.processing.fieldHandlers.TextAreaFieldHandler");
        ft.setMaxlength(new Long(4000));
        ft.setSize("25");
        result.add(ft);

        ft = new FieldType();
        ft.setCode(INPUT_TEXT_CHARACTER);
        ft.setFieldClass("java.lang.Character");
        ft.setManagerClass("org.jbpm.formModeler.core.processing.fieldHandlers.CharacterFieldHandler");
        ft.setMaxlength(new Long(1));
        ft.setSize("25");
        result.add(ft);

        ft = new FieldType();
        ft.setCode(INPUT_TEXT_CHAR);
        ft.setFieldClass("char");
        ft.setManagerClass("org.jbpm.formModeler.core.processing.fieldHandlers.CharacterFieldHandler");
        ft.setMaxlength(new Long(1));
        ft.setSize("25");
        result.add(ft);

        ft = new FieldType();
        ft.setCode(INPUT_TEXT_FLOAT);
        ft.setFieldClass("java.lang.Float");
        ft.setManagerClass("org.jbpm.formModeler.core.processing.fieldHandlers.NumericFieldHandler");
        ft.setMaxlength(new Long(100));
        ft.setSize("25");
        ft.setPattern("#.##");
        result.add(ft);

        ft = new FieldType();
        ft.setCode(INPUT_TEXT_PRIMITIVE_FLOAT);
        ft.setFieldClass("float");
        ft.setManagerClass("org.jbpm.formModeler.core.processing.fieldHandlers.NumericFieldHandler");
        ft.setMaxlength(new Long(50));
        ft.setSize("25");
        ft.setPattern("#.##");
        result.add(ft);

        ft = new FieldType();
        ft.setCode(INPUT_TEXT_DOUBLE);
        ft.setFieldClass("java.lang.Double");
        ft.setManagerClass("org.jbpm.formModeler.core.processing.fieldHandlers.NumericFieldHandler");
        ft.setMaxlength(new Long(100));
        ft.setSize("25");
        ft.setPattern("#.##");
        result.add(ft);

        ft = new FieldType();
        ft.setCode(INPUT_TEXT_PRIMITIVE_DOUBLE);
        ft.setFieldClass("double");
        ft.setManagerClass("org.jbpm.formModeler.core.processing.fieldHandlers.NumericFieldHandler");
        ft.setMaxlength(new Long(100));
        ft.setSize("25");
        ft.setPattern("#.##");
        result.add(ft);

        ft = new FieldType();
        ft.setCode(INPUT_TEXT_BIG_DECIMAL);
        ft.setFieldClass("java.math.BigDecimal");
        ft.setManagerClass("org.jbpm.formModeler.core.processing.fieldHandlers.NumericFieldHandler");
        ft.setMaxlength(new Long(100));
        ft.setSize("25");
        ft.setPattern("#.##");
        result.add(ft);

        ft = new FieldType();
        ft.setCode(INPUT_TEXT_BIG_INTEGER);
        ft.setFieldClass("java.math.BigInteger");
        ft.setManagerClass("org.jbpm.formModeler.core.processing.fieldHandlers.NumericFieldHandler");
        ft.setMaxlength(new Long(100));
        ft.setSize("25");
        result.add(ft);

        ft = new FieldType();
        ft.setCode(INPUT_TEXT_BYTE);
        ft.setFieldClass("java.lang.Byte");
        ft.setManagerClass("org.jbpm.formModeler.core.processing.fieldHandlers.NumericFieldHandler");
        ft.setMaxlength(new Long(4));
        ft.setSize("25");
        result.add(ft);

        ft = new FieldType();
        ft.setCode(INPUT_TEXT_PRIMITIVE_BYTE);
        ft.setFieldClass("byte");
        ft.setManagerClass("org.jbpm.formModeler.core.processing.fieldHandlers.NumericFieldHandler");
        ft.setMaxlength(new Long(4));
        ft.setSize("25");
        result.add(ft);

        ft = new FieldType();
        ft.setCode(INPUT_TEXT_SHORT);
        ft.setFieldClass("java.lang.Short");
        ft.setManagerClass("org.jbpm.formModeler.core.processing.fieldHandlers.NumericFieldHandler");
        ft.setMaxlength(new Long(100));
        ft.setSize("25");
        result.add(ft);

        ft = new FieldType();
        ft.setCode(INPUT_TEXT_PRIMITIVE_SHORT);
        ft.setFieldClass("short");
        ft.setManagerClass("org.jbpm.formModeler.core.processing.fieldHandlers.NumericFieldHandler");
        ft.setMaxlength(new Long(6));
        ft.setSize("25");
        result.add(ft);

        ft = new FieldType();
        ft.setCode(INPUT_TEXT_INTEGER);
        ft.setFieldClass("java.lang.Integer");
        ft.setManagerClass("org.jbpm.formModeler.core.processing.fieldHandlers.NumericFieldHandler");
        ft.setMaxlength(new Long(100));
        ft.setSize("25");
        result.add(ft);

        ft = new FieldType();
        ft.setCode(INPUT_TEXT_PRIMITIVE_INTEGER);
        ft.setFieldClass("int");
        ft.setManagerClass("org.jbpm.formModeler.core.processing.fieldHandlers.NumericFieldHandler");
        ft.setMaxlength(new Long(11));
        ft.setSize("25");
        result.add(ft);

        ft = new FieldType();
        ft.setCode(INPUT_TEXT_LONG);
        ft.setFieldClass("java.lang.Long");
        ft.setManagerClass("org.jbpm.formModeler.core.processing.fieldHandlers.NumericFieldHandler");
        ft.setMaxlength(new Long(100));
        ft.setSize("25");
        result.add(ft);

        ft = new FieldType();
        ft.setCode(INPUT_TEXT_PRIMITIVE_LONG);
        ft.setFieldClass("long");
        ft.setManagerClass("org.jbpm.formModeler.core.processing.fieldHandlers.NumericFieldHandler");
        ft.setMaxlength(new Long(20));
        ft.setSize("25");
        result.add(ft);

        ft = new FieldType();
        ft.setCode(INPUT_TEXT_EMAIL);
        ft.setFieldClass("java.lang.String");
        ft.setManagerClass("org.jbpm.formModeler.core.processing.fieldHandlers.InputTextFieldHandler");
        ft.setPattern("[a-zA-Z0-9.!#$%&'*+-/=?\\^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*");
        ft.setMaxlength(new Long(4000));
        ft.setSize("25");
        result.add(ft);

        ft = new FieldType();
        ft.setCode(MULTIPLE_INPUT);
        ft.setFieldClass("java.util.List");
        ft.setManagerClass("org.jbpm.formModeler.core.processing.fieldHandlers.multiple.MultipleInputFieldHandler");
        ft.setMaxlength(new Long(4000));
        ft.setSize("25");
        result.add(ft);

        ft = new FieldType();
        ft.setCode(CHECKBOX);
        ft.setFieldClass("java.lang.Boolean");
        ft.setManagerClass("org.jbpm.formModeler.core.processing.fieldHandlers.CheckBoxFieldHandler");
        result.add(ft);

        ft = new FieldType();
        ft.setCode(PRIMITIVE_CHECKBOX);
        ft.setFieldClass("boolean");
        ft.setManagerClass("org.jbpm.formModeler.core.processing.fieldHandlers.CheckBoxFieldHandler");
        result.add(ft);

        ft = new FieldType();
        ft.setCode(HTML_EDITOR);
        ft.setFieldClass("java.lang.String");
        ft.setManagerClass("org.jbpm.formModeler.core.processing.fieldHandlers.HTMLTextAreaFieldHandler");
        ft.setHeight("200");
        ft.setSize("350");
        result.add(ft);

        ft = new FieldType();
        ft.setCode("I18nHTMLText");
        ft.setFieldClass("org.jbpm.formModeler.core.wrappers.HTMLi18n");
        ft.setManagerClass("org.jbpm.formModeler.core.processing.fieldHandlers.HTMLi18nFieldHandler");
        ft.setHeight("30");
        ft.setSize("50");
        result.add(ft);

        ft = new FieldType();
        ft.setCode("I18nText");
        ft.setFieldClass("org.jbpm.formModeler.api.model.wrappers.I18nSet");
        ft.setManagerClass("org.jbpm.formModeler.core.processing.fieldHandlers.I18nSetFieldHandler");
        ft.setMaxlength(new Long(4000));
        ft.setSize("16");
        result.add(ft);

        ft = new FieldType();
        ft.setCode("I18nTextArea");
        ft.setFieldClass("org.jbpm.formModeler.api.model.wrappers.I18nSet");
        ft.setManagerClass("org.jbpm.formModeler.core.processing.fieldHandlers.I18nTextAreaFieldHandler");
        ft.setHeight("5");
        ft.setMaxlength(new Long(4000));
        ft.setSize("25");
        result.add(ft);

        ft = new FieldType();
        ft.setCode(INPUT_DATE);
        ft.setFieldClass("java.util.Date");
        ft.setManagerClass("org.jbpm.formModeler.core.processing.fieldHandlers.date.DateFieldHandler");
        ft.setMaxlength(new Long(25));
        ft.setSize("25");
        ft.setPattern("MM-dd-yyyy HH:mm:ss");
        result.add(ft);

        ft = new FieldType();
        ft.setCode(INPUT_SHORT_DATE);
        ft.setFieldClass("java.util.Date");
        ft.setManagerClass("org.jbpm.formModeler.core.processing.fieldHandlers.date.ShortDateFieldHandler");
        ft.setMaxlength(new Long(25));
        ft.setSize("25");
        ft.setPattern("MM-dd-yyyy");
        result.add(ft);

        ft = new FieldType();
        ft.setCode(SELECT_BOX);
        ft.setFieldClass("java.lang.String");
        ft.setManagerClass("org.jbpm.formModeler.core.processing.fieldHandlers.select.SelectBoxFieldHandler");
        result.add(ft);

        ft = new FieldType();
        ft.setCode(RADIO_GROUP);
        ft.setFieldClass("java.lang.String");
        ft.setManagerClass("org.jbpm.formModeler.core.processing.fieldHandlers.radio.RadioGroupFieldHandler");
        ft.setMaxlength(new Long(5));
        result.add(ft);

        ft = new FieldType();
        ft.setCode(DOCUMENT);
        ft.setFieldClass("org.jbpm.document.Document");
        ft.setManagerClass("org.jbpm.formModeler.fieldTypes.document.handling.JBPMDocumentFieldTypeHandler");
        result.add(ft);

        return result;
    }

    public static List<FieldType> getComplexTypesList() {

        List<FieldType> result = new ArrayList<>();

        FieldType ft = new FieldType();
        ft.setCode(SUBFORM);
        ft.setFieldClass("java.lang.Object");
        ft.setManagerClass("org.jbpm.formModeler.core.processing.fieldHandlers.SubformFieldHandler");
        ft.setMaxlength(new Long(4000));
        ft.setSize("25");
        result.add(ft);

        ft = new FieldType();
        ft.setCode(MULTIPLE_SUBFORM);
        ft.setFieldClass("java.util.List");
        ft.setManagerClass("org.jbpm.formModeler.core.processing.fieldHandlers.multipleSubform.CreateDynamicObjectFieldHandler");
        ft.setMaxlength(new Long(4000));
        ft.setSize("25");
        result.add(ft);

        return result;
    }

    public static List<FieldType> getDecoratorTypesList() {

        List<FieldType> result = new ArrayList<>();

        FieldType ft = new FieldType();
        ft.setCode(HTML_LABEL);
        ft.setFieldClass("HTMLlabel");
        ft.setManagerClass("org.jbpm.formModeler.core.processing.formDecorators.HTMLlabel");
        result.add(ft);

        ft = new FieldType();
        ft.setCode(SEPARATOR);
        ft.setFieldClass("Separator");
        ft.setManagerClass("org.jbpm.formModeler.core.processing.formDecorators.Separator");
        result.add(ft);

        return result;
    }
}
