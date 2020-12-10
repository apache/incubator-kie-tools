export const customWorkItemWid = `
import org.drools.core.process.core.datatype.impl.type.StringDataType;
import org.drools.core.process.core.datatype.impl.type.ObjectDataType;

[
  [
    "name" : "CreateCustomer",
    "parameters" : [
	"in_customer_id" : new StringDataType()	
    ],
    "displayName" : "Create Customer",
    "icon" : "defaultemailicon.gif"
  ]
]`;
