{
  "id": "6544f16e-c765-451e-882d-8202f6ea824c",
  "name": "CreationForm",
  "model": {
    "className": "com.myteam.myproject.NestedDO",
    "name": "NestedDO",
    "properties": [
      {
        "name": "checkbox",
        "typeInfo": {
          "type": "BASE",
          "className": "boolean",
          "multiple": false
        },
        "metaData": {
          "entries": []
        }
      },
      {
        "name": "textbox",
        "typeInfo": {
          "type": "BASE",
          "className": "java.lang.String",
          "multiple": false
        },
        "metaData": {
          "entries": []
        }
      },
      {
        "name": "textarea",
        "typeInfo": {
          "type": "BASE",
          "className": "java.lang.String",
          "multiple": false
        },
        "metaData": {
          "entries": []
        }
      },
      {
        "name": "integerbox",
        "typeInfo": {
          "type": "BASE",
          "className": "int",
          "multiple": false
        },
        "metaData": {
          "entries": []
        }
      },
      {
        "name": "decimalbox",
        "typeInfo": {
          "type": "BASE",
          "className": "double",
          "multiple": false
        },
        "metaData": {
          "entries": []
        }
      },
      {
        "name": "datepicker",
        "typeInfo": {
          "type": "BASE",
          "className": "java.util.Date",
          "multiple": false
        },
        "metaData": {
          "entries": []
        }
      },
      {
        "name": "slider",
        "typeInfo": {
          "type": "BASE",
          "className": "double",
          "multiple": false
        },
        "metaData": {
          "entries": []
        }
      },
      {
        "name": "listbox",
        "typeInfo": {
          "type": "BASE",
          "className": "java.lang.String",
          "multiple": false
        },
        "metaData": {
          "entries": []
        }
      },
      {
        "name": "radiogroup",
        "typeInfo": {
          "type": "BASE",
          "className": "java.lang.String",
          "multiple": false
        },
        "metaData": {
          "entries": []
        }
      }
    ],
    "formModelType": "org.kie.workbench.common.forms.data.modeller.model.DataObjectFormModel"
  },
  "fields": [
    {
      "id": "field_2524",
      "name": "checkbox",
      "label": "Checkbox",
      "required": false,
      "readOnly": false,
      "validateOnChange": true,
      "helpMessage": "",
      "binding": "checkbox",
      "standaloneClassName": "boolean",
      "code": "CheckBox",
      "serializedFieldClassName": "org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.checkBox.definition.CheckBoxFieldDefinition"
    },
    {
      "maxLength": 100,
      "placeHolder": "Textbox",
      "id": "field_6615",
      "name": "textbox",
      "label": "Textbox",
      "required": false,
      "readOnly": false,
      "validateOnChange": true,
      "helpMessage": "",
      "binding": "textbox",
      "standaloneClassName": "java.lang.String",
      "code": "TextBox",
      "serializedFieldClassName": "org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition.TextBoxFieldDefinition"
    },
    {
      "placeHolder": "Textarea",
      "rows": 4,
      "id": "field_9231",
      "name": "textarea",
      "label": "Textarea",
      "required": false,
      "readOnly": false,
      "validateOnChange": true,
      "helpMessage": "",
      "binding": "textarea",
      "standaloneClassName": "java.lang.String",
      "code": "TextArea",
      "serializedFieldClassName": "org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textArea.definition.TextAreaFieldDefinition"
    },
    {
      "placeHolder": "Integerbox",
      "maxLength": 100,
      "id": "field_7487",
      "name": "integerbox",
      "label": "Integerbox",
      "required": false,
      "readOnly": false,
      "validateOnChange": true,
      "helpMessage": "",
      "binding": "integerbox",
      "standaloneClassName": "int",
      "code": "IntegerBox",
      "serializedFieldClassName": "org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.integerBox.definition.IntegerBoxFieldDefinition"
    },
    {
      "placeHolder": "Decimalbox",
      "maxLength": 100,
      "id": "field_1304",
      "name": "decimalbox",
      "label": "Decimalbox",
      "required": false,
      "readOnly": false,
      "validateOnChange": true,
      "helpMessage": "",
      "binding": "decimalbox",
      "standaloneClassName": "double",
      "code": "DecimalBox",
      "serializedFieldClassName": "org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.decimalBox.definition.DecimalBoxFieldDefinition"
    }
  ],
  "layoutTemplate": {
    "version": 2,
    "style": "FLUID",
    "layoutProperties": {},
    "rows": [
      {
        "height": "12",
        "layoutColumns": [
          {
            "span": "12",
            "height": "12",
            "rows": [],
            "layoutComponents": [
              {
                "dragTypeName": "org.kie.workbench.common.forms.editor.client.editor.rendering.EditorFieldLayoutComponent",
                "properties": {
                  "field_id": "field_2524",
                  "form_id": "6544f16e-c765-451e-882d-8202f6ea824c"
                }
              }
            ]
          }
        ]
      },
      {
        "height": "12",
        "layoutColumns": [
          {
            "span": "12",
            "height": "12",
            "rows": [],
            "layoutComponents": [
              {
                "dragTypeName": "org.kie.workbench.common.forms.editor.client.editor.rendering.EditorFieldLayoutComponent",
                "properties": {
                  "field_id": "field_6615",
                  "form_id": "6544f16e-c765-451e-882d-8202f6ea824c"
                }
              }
            ]
          }
        ]
      },
      {
        "height": "12",
        "layoutColumns": [
          {
            "span": "12",
            "height": "12",
            "rows": [],
            "layoutComponents": [
              {
                "dragTypeName": "org.kie.workbench.common.forms.editor.client.editor.rendering.EditorFieldLayoutComponent",
                "properties": {
                  "field_id": "field_9231",
                  "form_id": "6544f16e-c765-451e-882d-8202f6ea824c"
                }
              }
            ]
          }
        ]
      },
      {
        "height": "12",
        "layoutColumns": [
          {
            "span": "12",
            "height": "12",
            "rows": [],
            "layoutComponents": [
              {
                "dragTypeName": "org.kie.workbench.common.forms.editor.client.editor.rendering.EditorFieldLayoutComponent",
                "properties": {
                  "field_id": "field_7487",
                  "form_id": "6544f16e-c765-451e-882d-8202f6ea824c"
                }
              }
            ]
          }
        ]
      },
      {
        "height": "12",
        "layoutColumns": [
          {
            "span": "12",
            "height": "12",
            "rows": [],
            "layoutComponents": [
              {
                "dragTypeName": "org.kie.workbench.common.forms.editor.client.editor.rendering.EditorFieldLayoutComponent",
                "properties": {
                  "field_id": "field_1304",
                  "form_id": "6544f16e-c765-451e-882d-8202f6ea824c"
                }
              }
            ]
          }
        ]
      }
    ]
  }
}