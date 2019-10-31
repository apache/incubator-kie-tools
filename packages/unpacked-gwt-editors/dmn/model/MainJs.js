/**
 * == READ ME ==
 *
 * This file has been manually modified to include *ALL* mappings (and not just DMN12)
 *
 * @type {{marshall: MainJs.marshall, unmarshall: MainJs.unmarshall}}
 */
MainJs = {

    mappings: [DC, DI, DMNDI12, DMN12, KIE],

    initializeJsInteropConstructors: function (constructorsMap) {

        var extraTypes = [{typeName: 'Name', namespace: null}];

        function createFunction(typeName) {
            return new Function('return { "TYPE_NAME" : "' + typeName + '" }');
        }

        function createNoTypedFunction() {
            return new Function('return { }');
        }

        function createConstructor(value) {
            console.log("Create createConstructor " + value)
            const parsedJson = JSON.parse(value)
            const name = parsedJson["name"]
            const nameSpace = parsedJson["nameSpace"]
            const typeName = parsedJson["typeName"]
            console.log("parsedJson " + parsedJson)
            console.log("name " + name)
            console.log("nameSpace " + nameSpace)
            console.log("typeName " + typeName)
            if (nameSpace != null) {
                if (typeName != null) {
                    window[nameSpace][name] = createFunction(typeName);
                } else {
                    window[nameSpace][name] = createNoTypedFunction();
                }
            } else {
                if (typeName != null) {
                    window[name] = createFunction(typeName);
                } else {
                    window[name] = createNoTypedFunction();
                }
            }
        }

        function hasNameSpace(value) {
            return JSON.parse(value)["nameSpace"] != null
        }

        function hasNotNameSpace(value) {
            return JSON.parse(value)["nameSpace"] == null
        }

        function iterateValueEntry(values) {
            console.log("iterateValueEntry " + values);
            const baseTypes = values.filter(hasNotNameSpace)
            const innerTypes = values.filter(hasNameSpace)
            baseTypes.forEach(createConstructor)
            innerTypes.forEach(createConstructor)
        }

        function iterateKeyValueEntry(key, values) {
            console.log("iterateKeyValueEntry " + key + "  " + values);
            iterateValueEntry(values)
        }

        console.log('Generating JsInterop constructors.');

        for (const property in constructorsMap) {
            if (constructorsMap.hasOwnProperty(property)) {
                iterateKeyValueEntry(property, constructorsMap[property])
            }
        }
    },

    unmarshall: function (text, dynamicNamespace, callback) {
        // Create Jsonix context
        var context = new Jsonix.Context(this.mappings);

        // Create unmarshaller
        var unmarshaller = context.createUnmarshaller();
        var toReturn = unmarshaller.unmarshalString(text);
        callback(toReturn);
    },

    marshall: function (value, defaultNamespace, callback) {
        // Create Jsonix context
        var namespaces = {};
        namespaces[defaultNamespace] = "";
        namespaces["http://www.omg.org/spec/DMN/20180521/MODEL/"] = "dmn";
        namespaces["http://www.omg.org/spec/DMN/20180521/DI/"] = "di";
        namespaces["http://www.drools.org/kie/dmn/1.2"] = "kie";
        namespaces["http://www.omg.org/spec/DMN/20180521/DMNDI/"] = "dmndi";
        namespaces["http://www.omg.org/spec/DMN/20180521/DC/"] = "dc";
        namespaces["http://www.omg.org/spec/DMN/20180521/FEEL/"] = "feel";
        var context = new Jsonix.Context(this.mappings, {
            namespacePrefixes: namespaces
        });

        // Create unmarshaller
        var marshaller = context.createMarshaller();

        var xmlDocument = marshaller.marshalDocument(value);
        var s = new XMLSerializer();
        var toReturn = s.serializeToString(xmlDocument);
        callback(toReturn);
    }
}