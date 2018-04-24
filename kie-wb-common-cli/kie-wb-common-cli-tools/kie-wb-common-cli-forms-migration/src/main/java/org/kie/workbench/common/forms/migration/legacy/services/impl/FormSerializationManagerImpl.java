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

import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.xerces.parsers.DOMParser;
import org.kie.workbench.common.forms.migration.legacy.model.DataHolder;
import org.kie.workbench.common.forms.migration.legacy.model.Field;
import org.kie.workbench.common.forms.migration.legacy.model.Form;
import org.kie.workbench.common.forms.migration.legacy.services.FieldTypeManager;
import org.kie.workbench.common.forms.migration.legacy.services.FormSerializationManager;
import org.kie.workbench.common.forms.migration.legacy.services.impl.util.XMLNode;
import org.kie.workbench.common.migration.cli.SystemAccess;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class FormSerializationManagerImpl implements FormSerializationManager {

    public static final String NODE_FORM = "form";
    public static final String NODE_FIELD = "field";
    public static final String NODE_PROPERTY = "property";
    public static final String NODE_DATA_HOLDER = "dataHolder";

    public static final String ATTR_ID = "id";
    public static final String ATTR_INPUT_ID = "inputId";
    public static final String ATTR_OUT_ID = "outId";
    public static final String ATTR_POSITION = "position";
    public static final String ATTR_TYPE = "type";
    public static final String ATTR_BAG_TYPE = "bag-type";
    public static final String ATTR_SUPPORTED_TYPE = "supportedType";
    public static final String ATTR_NAME = "name";
    public static final String ATTR_VALUE = "value";

    protected FieldTypeManager fieldTypeManager;

    public FormSerializationManagerImpl() {
        this.fieldTypeManager = FieldTypeManager.get();
    }

    @Override
    public String generateFormXML(Form form, SystemAccess system) {
        XMLNode rootNode = new XMLNode(NODE_FORM, null);

        try {
            return generateFormXML(form, rootNode);
        } catch (Exception e) {
            system.err().println("Error serializing form to XML:");
            e.printStackTrace(system.err());
            return "";
        }
    }

    @Override
    public Form loadFormFromXML(String xml) throws Exception {
        if (StringUtils.isBlank(xml)) {
            return null;
        }

        DOMParser parser = new DOMParser();
        parser.parse(new InputSource(new StringReader(xml)));
        Document doc = parser.getDocument();
        NodeList nodes = doc.getElementsByTagName(NODE_FORM);
        Node rootNode = nodes.item(0);
        return deserializeForm(rootNode);
    }

    public Form deserializeForm(Node nodeForm) throws Exception {
        if (!nodeForm.getNodeName().equals(NODE_FORM)) {
            return null;
        }

        Form form = new Form();
        form.setId(Long.valueOf(StringEscapeUtils.unescapeXml(nodeForm.getAttributes().getNamedItem(ATTR_ID).getNodeValue())));

        Set<Field> fields = new TreeSet<>();
        NodeList childNodes = nodeForm.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            if (node.getNodeName().equals(NODE_PROPERTY)) {
                String propName = node.getAttributes().getNamedItem(ATTR_NAME).getNodeValue();
                String value = StringEscapeUtils.unescapeXml(node.getAttributes().getNamedItem(ATTR_VALUE).getNodeValue());

                if ("subject".equals(propName)) {
                    form.setSubject(value);
                } else if ("name".equals(propName)) {
                    form.setName(value);
                } else if ("displayMode".equals(propName)) {
                    form.setDisplayMode(value);
                } else if ("labelMode".equals(propName)) {
                    form.setLabelMode(value);
                } else if ("showMode".equals(propName)) {
                    form.setShowMode(value);
                } else if ("status".equals(propName)) {
                    form.setStatus(Long.valueOf(value));
                } else if ("formTemplate".equals(propName)) {
                    form.setFormTemplate(value);
                } else if ("migrationStep".equals(propName)) {
                    form.setMigrationStep(Integer.decode(value));
                }
            } else if (node.getNodeName().equals(NODE_FIELD)) {
                Field field = deserializeField(node);
                field.setForm(form);
                fields.add(field);
            } else if (node.getNodeName().equals(NODE_DATA_HOLDER)) {
                String holderId = getNodeAttributeValue(node, ATTR_ID);
                String holderInputId = getNodeAttributeValue(node, ATTR_INPUT_ID);
                String holderOutId = getNodeAttributeValue(node, ATTR_OUT_ID);
                String holderType = getNodeAttributeValue(node, ATTR_TYPE);
                String holderValue = getNodeAttributeValue(node, ATTR_VALUE);
                String holderRenderColor = getNodeAttributeValue(node, ATTR_NAME);
                String holderSupportedType = getNodeAttributeValue(node, ATTR_SUPPORTED_TYPE);

                DataHolder dataHolder = new DataHolder(holderId, holderInputId, holderOutId, holderType, holderValue, holderRenderColor, holderSupportedType);

                form.setDataHolder(dataHolder);
            }
        }
        if (fields != null) {
            form.setFormFields(fields);
        }
        return form;
    }

    protected String getNodeAttributeValue(Node node, String attributeName) {
        Node attribute = node.getAttributes().getNamedItem(attributeName);
        return attribute != null ? attribute.getNodeValue() : "";
    }

    private void addXMLNode(String propName, String value, XMLNode parent) {
        if (!StringUtils.isEmpty(value)) {
            XMLNode propertyNode = new XMLNode(NODE_PROPERTY, parent);
            propertyNode.addAttribute(ATTR_NAME, propName);
            propertyNode.addAttribute(ATTR_VALUE, value);
            parent.addChild(propertyNode);
        }
    }

    /**
     * Generates the xml representation and mount in rootNode the structure to be included.
     * Fills the XMLNode structure with the form representation and returns the string.
     */
    public String generateFormXML(Form form, XMLNode rootNode) throws Exception {
        rootNode.addAttribute(ATTR_ID, form.getId().toString());

        addXMLNode("subject", form.getSubject(), rootNode);
        addXMLNode("name", form.getName(), rootNode);
        addXMLNode("displayMode", form.getDisplayMode(), rootNode);
        addXMLNode("labelMode", form.getLabelMode(), rootNode);
        addXMLNode("showMode", form.getShowMode(), rootNode);
        addXMLNode("status", (form.getStatus() != null ? String.valueOf(form.getStatus()) : null), rootNode);
        addXMLNode("formTemplate", form.getFormTemplate(), rootNode);
        addXMLNode("migrationStep", String.valueOf(form.getMigrationStep()), rootNode);

        for (Field field : form.getFormFields()) {
            generateFieldXML(field, rootNode);
        }

        for (DataHolder dataHolder : form.getHolders()) {
            generateDataHolderXML(dataHolder, rootNode);
        }

        StringWriter sw = new StringWriter();
        rootNode.writeXML(sw, true);

        return sw.toString();
    }

    public Field deserializeField(Node nodeField) throws Exception {
        if (!nodeField.getNodeName().equals(NODE_FIELD)) {
            return null;
        }

        Field field = new Field();
        field.setId(Long.valueOf(nodeField.getAttributes().getNamedItem(ATTR_ID).getNodeValue()));
        field.setFieldName(nodeField.getAttributes().getNamedItem(ATTR_NAME).getNodeValue());
        field.setPosition(Integer.parseInt(nodeField.getAttributes().getNamedItem(ATTR_POSITION).getNodeValue()));
        field.setFieldType(fieldTypeManager.getTypeByCode(nodeField.getAttributes().getNamedItem(ATTR_TYPE).getNodeValue()));

        Node bag = nodeField.getAttributes().getNamedItem(ATTR_BAG_TYPE);

        if (bag != null) {
            field.setBag(bag.getNodeValue());
        }

        NodeList fieldPropsNodes = nodeField.getChildNodes();
        for (int j = 0; j < fieldPropsNodes.getLength(); j++) {
            Node nodeFieldProp = fieldPropsNodes.item(j);
            if (nodeFieldProp.getNodeName().equals(NODE_PROPERTY)) {
                String propName = nodeFieldProp.getAttributes().getNamedItem(ATTR_NAME).getNodeValue();
                String value = StringEscapeUtils.unescapeXml(nodeFieldProp.getAttributes().getNamedItem(ATTR_VALUE).getNodeValue());
                if (propName != null && value != null) {
                    if ("fieldRequired".equals(propName)) {
                        field.setFieldRequired(Boolean.valueOf(value));
                    } else if ("groupWithPrevious".equals(propName)) {
                        field.setGroupWithPrevious(Boolean.valueOf(value));
                    } else if ("height".equals(propName)) {
                        field.setHeight(value);
                    } else if ("labelCSSClass".equals(propName)) {
                        field.setLabelCSSClass(value);
                    } else if ("labelCSSStyle".equals(propName)) {
                        field.setLabelCSSStyle(value);
                    } else if ("label".equals(propName)) {
                        field.setLabel(deserializeI18nEntrySet(value));
                    } else if ("errorMessage".equals(propName)) {
                        field.setErrorMessage(deserializeI18nEntrySet(value));
                    } else if ("title".equals(propName)) {
                        field.setTitle(deserializeI18nEntrySet(value));
                    } else if ("readonly".equals(propName)) {
                        field.setReadonly(Boolean.valueOf(value));
                    } else if ("size".equals(propName)) {
                        if (!StringUtils.isEmpty(value) && StringUtils.isNumeric(value)) {
                            field.setSize(Long.valueOf(value));
                        }
                    } else if ("formula".equals(propName)) {
                        field.setFormula(value);
                    } else if ("rangeFormula".equals(propName)) {
                        field.setRangeFormula(value);
                    } else if ("pattern".equals(propName)) {
                        field.setPattern(value);
                    } else if ("maxlength".equals(propName)) {
                        if (!StringUtils.isEmpty(value) && StringUtils.isNumeric(value)) {
                            field.setMaxlength(Long.valueOf(value));
                        }
                    } else if ("styleclass".equals(propName)) {
                        field.setStyleclass(value);
                    } else if ("cssStyle".equals(propName)) {
                        field.setCssStyle(value);
                    } else if ("tabindex".equals(propName)) {
                        if (!StringUtils.isEmpty(value) && StringUtils.isNumeric(value)) {
                            field.setTabindex(Long.valueOf(value));
                        }
                    } else if ("accesskey".equals(propName)) {
                        field.setAccesskey(value);
                    } else if ("isHTML".equals(propName)) {
                        field.setIsHTML(Boolean.valueOf(value));
                    } else if ("htmlContent".equals(propName)) {
                        field.setHtmlContent(deserializeI18nEntrySet(value));
                    } else if ("hideContent".equals(propName)) {
                        field.setHideContent(Boolean.valueOf(value));
                    } else if ("defaultValueFormula".equals(propName)) {
                        field.setDefaultValueFormula(value);
                    } else if ("defaultSubform".equals(propName)) {
                        field.setDefaultSubform(value);
                    } else if ("previewSubform".equals(propName)) {
                        field.setPreviewSubform(value);
                    } else if ("tableSubform".equals(propName)) {
                        field.setTableSubform(value);
                    } else if ("newItemText".equals(propName)) {
                        field.setNewItemText(deserializeI18nEntrySet(value));
                    } else if ("addItemText".equals(propName)) {
                        field.setAddItemText(deserializeI18nEntrySet(value));
                    } else if ("cancelItemText".equals(propName)) {
                        field.setCancelItemText(deserializeI18nEntrySet(value));
                    } else if ("deleteItems".equals(propName)) {
                        field.setDeleteItems(Boolean.valueOf(value));
                    } else if ("updateItems".equals(propName)) {
                        field.setUpdateItems(Boolean.valueOf(value));
                    } else if ("visualizeItems".equals(propName)) {
                        field.setVisualizeItem(Boolean.valueOf(value));
                    } else if ("hideCreateItem".equals(propName)) {
                        field.setHideCreateItem(Boolean.valueOf(value));
                    } else if ("expanded".equals(propName)) {
                        field.setExpanded(Boolean.valueOf(value));
                    } else if ("enableTableEnterData".equals(propName)) {
                        field.setEnableTableEnterData(Boolean.valueOf(value));
                    } else if ("inputBinding".equals(propName)) {
                        field.setInputBinding(value);
                    } else if ("outputBinding".equals(propName)) {
                        field.setOutputBinding(value);
                    } else if ("customFieldType".equals(propName)) {
                        field.setCustomFieldType(value);
                    } else if ("param1".equals(propName)) {
                        field.setParam1(value);
                    } else if ("param2".equals(propName)) {
                        field.setParam2(value);
                    } else if ("param3".equals(propName)) {
                        field.setParam3(value);
                    } else if ("param4".equals(propName)) {
                        field.setParam4(value);
                    } else if ("param5".equals(propName)) {
                        field.setParam5(value);
                    } else if ("fieldClass".equals(propName)) {
                        field.getFieldType().setFieldClass(value);
                    } else if ("onChangeScript".equals(propName)) {
                        field.setOnChangeScript(value);
                    } else if ("movedToForm".equals(propName) && value != null) {
                        field.setMovedToForm(value);
                    } else if ("sourceLink".equals(propName) && value != null) {
                        field.setSourceLink(value);
                    }
                }
            }
        }

        return field;
    }

    private String getFieldProperty(String formName, String fieldName, String selector, Properties props) {
        if (props == null) {
            return null;
        }

        String value = props.getProperty(formName + "." + fieldName + "." + selector);

        if (StringUtils.isEmpty(value)) {
            value = props.getProperty(fieldName + "." + selector);
        }

        return value;
    }

    public void generateFieldXML(Field field, XMLNode parent) {
        XMLNode rootNode = new XMLNode(NODE_FIELD, parent);
        rootNode.addAttribute(ATTR_ID, String.valueOf(field.getId()));
        rootNode.addAttribute(ATTR_POSITION, String.valueOf(field.getPosition()));
        rootNode.addAttribute(ATTR_NAME, field.getFieldName());
        if (field.getFieldType() != null) {
            rootNode.addAttribute(ATTR_TYPE, field.getFieldType().getCode());
        }

        if (!StringUtils.isEmpty(field.getBag())) {
            rootNode.addAttribute(ATTR_BAG_TYPE, field.getBag());
        }

        addXMLNode("fieldRequired", (field.getFieldRequired() != null ? String.valueOf(field.getFieldRequired()) : null), rootNode);
        addXMLNode("groupWithPrevious", (field.getGroupWithPrevious() != null ? String.valueOf(field.getGroupWithPrevious()) : null), rootNode);
        addXMLNode("height", field.getHeight(), rootNode);
        addXMLNode("labelCSSClass", field.getLabelCSSClass(), rootNode);
        addXMLNode("labelCSSStyle", field.getLabelCSSStyle(), rootNode);
        addXMLNode("label", (field.getLabel() != null ? serializeI18nSet(field.getLabel()) : null), rootNode);
        addXMLNode("errorMessage", (field.getErrorMessage() != null ? serializeI18nSet(field.getErrorMessage()) : null), rootNode);
        addXMLNode("title", (field.getTitle() != null ? serializeI18nSet(field.getTitle()) : null), rootNode);
        addXMLNode("readonly", (field.getReadonly() != null ? String.valueOf(field.getReadonly()) : null), rootNode);
        addXMLNode("size", (field.getSize() != null ? String.valueOf(field.getSize()) : null), rootNode);
        addXMLNode("formula", field.getFormula(), rootNode);
        addXMLNode("rangeFormula", field.getRangeFormula(), rootNode);
        addXMLNode("pattern", field.getPattern(), rootNode);
        addXMLNode("maxlength", (field.getMaxlength() != null ? String.valueOf(field.getMaxlength()) : null), rootNode);
        addXMLNode("styleclass", field.getStyleclass(), rootNode);
        addXMLNode("cssStyle", field.getCssStyle(), rootNode);
        addXMLNode("tabindex", (field.getTabindex() != null ? String.valueOf(field.getTabindex()) : null), rootNode);
        addXMLNode("accesskey", field.getAccesskey(), rootNode);
        addXMLNode("isHTML", (field.getIsHTML() != null ? String.valueOf(field.getIsHTML()) : null), rootNode);
        addXMLNode("hideContent", (field.getHideContent() != null ? String.valueOf(field.getHideContent()) : null), rootNode);
        addXMLNode("defaultValueFormula", field.getDefaultValueFormula(), rootNode);
        addXMLNode("inputBinding", field.getInputBinding(), rootNode);
        addXMLNode("outputBinding", field.getOutputBinding(), rootNode);
        addXMLNode("htmlContent", (field.getHtmlContent() != null ? serializeI18nSet(field.getHtmlContent()) : null), rootNode);

        addXMLNode("defaultSubform", field.getDefaultSubform(), rootNode);
        addXMLNode("previewSubform", field.getPreviewSubform(), rootNode);
        addXMLNode("tableSubform", field.getTableSubform(), rootNode);
        addXMLNode("newItemText", (field.getNewItemText() != null ? serializeI18nSet(field.getNewItemText()) : null), rootNode);
        addXMLNode("addItemText", (field.getAddItemText() != null ? serializeI18nSet(field.getAddItemText()) : null), rootNode);
        addXMLNode("cancelItemText", (field.getCancelItemText() != null ? serializeI18nSet(field.getCancelItemText()) : null), rootNode);
        addXMLNode("deleteItems", (field.getDeleteItems() != null ? String.valueOf(field.getDeleteItems()) : null), rootNode);
        addXMLNode("updateItems", (field.getUpdateItems() != null ? String.valueOf(field.getUpdateItems()) : null), rootNode);
        addXMLNode("visualizeItems", (field.getVisualizeItem() != null ? String.valueOf(field.getVisualizeItem()) : null), rootNode);
        addXMLNode("hideCreateItem", (field.getHideCreateItem() != null ? String.valueOf(field.getHideCreateItem()) : null), rootNode);
        addXMLNode("expanded", (field.getExpanded() != null ? String.valueOf(field.getExpanded()) : null), rootNode);
        addXMLNode("enableTableEnterData", (field.getEnableTableEnterData() != null ? String.valueOf(field.getEnableTableEnterData()) : null), rootNode);
        addXMLNode("customFieldType", field.getCustomFieldType(), rootNode);
        addXMLNode("fieldClass", field.getFieldType().getFieldClass(), rootNode);
        addXMLNode("param1", field.getParam1(), rootNode);
        addXMLNode("param2", field.getParam2(), rootNode);
        addXMLNode("param3", field.getParam3(), rootNode);
        addXMLNode("param4", field.getParam4(), rootNode);
        addXMLNode("param5", field.getParam5(), rootNode);
        addXMLNode("onChangeScript", field.getOnChangeScript(), rootNode);

        if(field.getMovedToForm() != null) {
            addXMLNode("movedToForm", field.getMovedToForm(), rootNode);
        }

        if(field.getSourceLink() != null) {
            addXMLNode("sourceLink", field.getMovedToForm(), rootNode);
        }

        parent.addChild(rootNode);
    }

    public void generateDataHolderXML(DataHolder dataHolder, XMLNode parent) {
        XMLNode rootNode = new XMLNode(NODE_DATA_HOLDER, parent);
        rootNode.addAttribute(ATTR_ID, dataHolder.getUniqeId());
        rootNode.addAttribute(ATTR_INPUT_ID, dataHolder.getInputId());
        rootNode.addAttribute(ATTR_OUT_ID, dataHolder.getOuputId());
        rootNode.addAttribute(ATTR_TYPE, dataHolder.getType());
        rootNode.addAttribute(ATTR_VALUE, dataHolder.getClassName());
        rootNode.addAttribute(ATTR_NAME, dataHolder.getRenderColor());

        if (!StringUtils.isEmpty(dataHolder.getSupportedType())) {
            rootNode.addAttribute(ATTR_SUPPORTED_TYPE, dataHolder.getSupportedType());
        }

        parent.addChild(rootNode);
    }

    protected String[] decodeStringArray(String textValue) {
        if (textValue == null || textValue.trim().length() == 0) {
            return new String[0];
        }
        String[] lista;
        lista = textValue.split("quot;");
        return lista;
    }

    protected String encodeStringArray(String[] value) {
        String cad = "";
        for (int i = 0; i < value.length; i++) {
            cad += "quot;" + value[i] + "quot;";
            i++;
            cad += ",quot;" + value[i] + "quot;";
        }
        return cad;
    }

    public String serializeI18nSet(Map<String, String> i18nSet) {
        String[] values = new String[i18nSet.entrySet().size() * 2];
        int i = 0;
        for (Iterator it = i18nSet.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, String> entry = (Map.Entry<String, String>) it.next();
            values[i] = entry.getKey();
            i++;
            values[i] = entry.getValue();
            i++;
        }
        return encodeStringArray(values);
    }

    public Map<String, String> deserializeI18nEntrySet(String cadena) {
        String[] values = decodeStringArray(cadena);
        Map<String, String> mapValues = new HashMap();
        for (int i = 0; i < values.length; i = i + 4) {
            String key = values[i + 1];
            String value = "";
            if (i + 3 < values.length) {
                value = values[i + 3];
            }
            if (key.length() == 2) {
                mapValues.put(key, value);
            }
        }
        return mapValues;
    }
}
