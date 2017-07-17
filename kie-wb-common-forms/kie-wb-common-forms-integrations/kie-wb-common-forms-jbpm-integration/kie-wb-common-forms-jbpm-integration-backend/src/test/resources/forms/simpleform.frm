{
  "id": "process",
  "model": {
    "processId": "processId",
    "processName": "processId",
    "properties": [
      {
        "name": "text",
        "typeInfo": {
          "type": "BASE",
          "className": "java.lang.String",
          "multiple": false
        }
      },
      {
        "name": "integer",
        "typeInfo": {
          "type": "BASE",
          "className": "java.lang.Integer",
          "multiple": false
        }
      }
    ],
    "formModelType": "org.kie.workbench.common.forms.jbpm.model.authoring.process.BusinessProcessFormModel"
  },
  "fields": [
    {
      "maxLength": 100,
      "placeHolder": "",
      "annotatedId": false,
      "id": "field_4894191222478904E12",
      "name": "text",
      "label": "text",
      "required": false,
      "readOnly": false,
      "validateOnChange": true,
      "binding": "text",
      "standaloneClassName": "java.lang.String",
      "code": "TextBox",
      "serializedFieldClassName": "org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition.TextBoxFieldDefinition"
    },
    {
      "maxLength": 100,
      "placeHolder": "",
      "annotatedId": false,
      "id": "field_0552848260498176E12",
      "name": "integer",
      "label": "integer",
      "required": false,
      "readOnly": false,
      "validateOnChange": true,
      "binding": "integer",
      "standaloneClassName": "java.lang.Integer",
      "code": "IntegerBox",
      "serializedFieldClassName": "org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.integerBox.definition.IntegerBoxFieldDefinition"
    }
  ],
  "layoutTemplate": {
    "version": 1,
    "layoutProperties": {},
    "rows": [
      {
        "layoutColumns": [
          {
            "span": "12",
            "rows": [],
            "layoutComponents": [
              {
                "dragTypeName": "org.kie.workbench.common.forms.editor.client.editor.rendering.EditorFieldLayoutComponent",
                "properties": {
                  "field_id": "field_4894191222478904E12",
                  "form_id": "process"
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
            "rows": [],
            "layoutComponents": [
              {
                "dragTypeName": "org.kie.workbench.common.forms.editor.client.editor.rendering.EditorFieldLayoutComponent",
                "properties": {
                  "field_id": "field_0552848260498176E12",
                  "form_id": "process"
                }
              }
            ]
          }
        ]
      }
    ]
  }
}