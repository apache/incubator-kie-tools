{
  "id": "PersonShort.frm",
  "name": "PersonShort",
  "model": {
    "className": "com.Person",
    "name": "person",
    "formModelType": "org.kie.workbench.common.forms.data.modeller.model.DataObjectFormModel"
  },
  "fields": [
    {
      "maxLength": 100,
      "placeHolder": "Name",
      "id": "field_0849182264536859E12",
      "name": "person_name",
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
      "maxLength": 100,
      "placeHolder": "Last Name",
      "id": "field_1531245139910056E12",
      "name": "person_lastName",
      "label": "Last Name",
      "required": false,
      "readOnly": false,
      "validateOnChange": true,
      "binding": "lastName",
      "standaloneClassName": "java.lang.String",
      "code": "TextBox",
      "serializedFieldClassName": "org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition.TextBoxFieldDefinition"
    }
  ],
  "layoutTemplate": {
    "version": 1,
    "name": "PersonShort",
    "layoutProperties": {},
    "rows": [
      {
        "layoutColumns": [
          {
            "span": "6",
            "rows": [],
            "layoutComponents": [
              {
                "dragTypeName": "org.kie.workbench.common.forms.editor.client.editor.rendering.EditorFieldLayoutComponent",
                "properties": {
                  "field_id": "field_0849182264536859E12",
                  "form_id": "55000a62-d76f-4113-8fe1-dfca11d24394"
                }
              }
            ]
          },
          {
            "span": "6",
            "rows": [],
            "layoutComponents": [
              {
                "dragTypeName": "org.kie.workbench.common.forms.editor.client.editor.rendering.EditorFieldLayoutComponent",
                "properties": {
                  "field_id": "field_1531245139910056E12",
                  "form_id": "55000a62-d76f-4113-8fe1-dfca11d24394"
                }
              }
            ]
          }
        ]
      }
    ]
  }
}