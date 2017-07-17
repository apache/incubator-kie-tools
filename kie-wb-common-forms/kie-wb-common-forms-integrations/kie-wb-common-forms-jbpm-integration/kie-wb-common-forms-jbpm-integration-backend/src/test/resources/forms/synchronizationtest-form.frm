{
  "id": "ad1d7795-887c-43bc-89cc-55d8b92ac2ca",
  "name": "task-taskform.frm",
  "model": {
    "taskName": "task",
    "processId": "issues.Process",
    "properties": [
      {
        "name": "lastName",
        "typeInfo": {
          "type": "BASE",
          "className": "java.lang.String",
          "multiple": false
        }
      },
      {
        "name": "address",
        "typeInfo": {
          "type": "BASE",
          "className": "java.lang.String",
          "multiple": false
        }
      },
      {
        "name": "name",
        "typeInfo": {
          "type": "BASE",
          "className": "java.lang.String",
          "multiple": false
        }
      },
      {
        "name": "age",
        "typeInfo": {
          "type": "BASE",
          "className": "java.lang.Integer",
          "multiple": false
        }
      },
      {
        "name": "married",
        "typeInfo": {
          "type": "BASE",
          "className": "java.lang.Boolean",
          "multiple": false
        }
      }
    ],
    "formModelType": "org.kie.workbench.common.forms.jbpm.model.authoring.task.TaskFormModel"
  },
  "fields": [
    {
      "maxLength": 100,
      "placeHolder": "Name",
      "id": "name",
      "name": "name",
      "label": "Name",
      "required": false,
      "readOnly": false,
      "validateOnChange": true,
      "binding": "name",
      "standaloneClassName": "java.lang.String",
      "code": "TextBox",
      "serializedFieldClassName": "org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition.TextBoxFieldDefinition"
    },
    {
      "id": "married",
      "name": "married",
      "label": "Married",
      "required": false,
      "readOnly": false,
      "validateOnChange": true,
      "binding": "married",
      "standaloneClassName": "java.lang.Boolean",
      "code": "CheckBox",
      "serializedFieldClassName": "org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.checkBox.definition.CheckBoxFieldDefinition"
    },
    {
      "placeHolder": "Age",
      "maxLength": 100,
      "id": "age",
      "name": "age",
      "label": "Age",
      "required": false,
      "readOnly": false,
      "validateOnChange": true,
      "binding": "age",
      "standaloneClassName": "java.lang.Integer",
      "code": "IntegerBox",
      "serializedFieldClassName": "org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.integerBox.definition.IntegerBoxFieldDefinition"
    },
    {
      "maxLength": 100,
      "placeHolder": "LastName",
      "id": "lastName",
      "name": "lastName",
      "label": "LastName",
      "required": false,
      "readOnly": false,
      "validateOnChange": true,
      "binding": "lastName",
      "standaloneClassName": "java.lang.String",
      "code": "TextBox",
      "serializedFieldClassName": "org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition.TextBoxFieldDefinition"
    },
    {
      "maxLength": 100,
      "placeHolder": "Address",
      "id": "address",
      "name": "address",
      "label": "Address",
      "required": false,
      "readOnly": false,
      "validateOnChange": true,
      "binding": "address",
      "standaloneClassName": "java.lang.String",
      "code": "TextBox",
      "serializedFieldClassName": "org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition.TextBoxFieldDefinition"
    }
  ],
  "layoutTemplate": {
    "version": 2,
    "style": "FLUID",
    "layoutProperties": {},
    "rows": [
      {
        "layoutColumns": [
          {
            "span": "12",
            "height": "12",
            "rows": [],
            "layoutComponents": [
              {
                "dragTypeName": "org.kie.workbench.common.forms.editor.client.editor.rendering.EditorFieldLayoutComponent",
                "properties": {
                  "field_id": "lastName",
                  "form_id": "ad1d7795-887c-43bc-89cc-55d8b92ac2ca"
                }
              }
            ]
          }
        ]
      },
      {
        "layoutColumns": [
          {
            "span": "12",
            "height": "12",
            "rows": [],
            "layoutComponents": [
              {
                "dragTypeName": "org.kie.workbench.common.forms.editor.client.editor.rendering.EditorFieldLayoutComponent",
                "properties": {
                  "field_id": "name",
                  "form_id": "ad1d7795-887c-43bc-89cc-55d8b92ac2ca"
                }
              }
            ]
          }
        ]
      },
      {
        "layoutColumns": [
          {
            "span": "12",
            "height": "12",
            "rows": [],
            "layoutComponents": [
              {
                "dragTypeName": "org.kie.workbench.common.forms.editor.client.editor.rendering.EditorFieldLayoutComponent",
                "properties": {
                  "field_id": "married",
                  "form_id": "ad1d7795-887c-43bc-89cc-55d8b92ac2ca"
                }
              }
            ]
          }
        ]
      },
      {
        "layoutColumns": [
          {
            "span": "12",
            "height": "12",
            "rows": [],
            "layoutComponents": [
              {
                "dragTypeName": "org.kie.workbench.common.forms.editor.client.editor.rendering.EditorFieldLayoutComponent",
                "properties": {
                  "field_id": "age",
                  "form_id": "ad1d7795-887c-43bc-89cc-55d8b92ac2ca"
                }
              }
            ]
          }
        ]
      },
      {
        "layoutColumns": [
          {
            "span": "12",
            "height": "12",
            "rows": [],
            "layoutComponents": [
              {
                "dragTypeName": "org.kie.workbench.common.forms.editor.client.editor.rendering.EditorFieldLayoutComponent",
                "properties": {
                  "field_id": "address",
                  "form_id": "ad1d7795-887c-43bc-89cc-55d8b92ac2ca"
                }
              }
            ]
          }
        ]
      }
    ]
  }
}