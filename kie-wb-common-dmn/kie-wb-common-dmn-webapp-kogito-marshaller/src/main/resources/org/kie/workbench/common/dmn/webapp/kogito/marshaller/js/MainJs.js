/**
 * == READ ME ==
 *
 * This file has been manually modified to include *ALL* mappings (and not just DMN12)
 *
 * @type {{marshall: MainJs.marshall, unmarshall: MainJs.unmarshall}}
 */
MainJs = {

    mappings: [DC, DI, DMNDI12, DMN12, KIE],

    isJsInteropConstructorsInitialized: false,

    initializeJsInteropConstructors: function (constructorsMap) {

        if (this.isJsInteropConstructorsInitialized) {
            return;
        }

        this.isJsInteropConstructorsInitialized = true;

        function createFunction(typeName) {
            return new Function('return { "TYPE_NAME" : "' + typeName + '" }');
        }

        function createNoTypedFunction() {
            return new Function('return { }');
        }

        function createConstructor(value) {

            var parsedJson = JSON.parse(value);
            var name = parsedJson["name"];
            var nameSpace = parsedJson["nameSpace"];
            var typeName = parsedJson["typeName"];

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
            return JSON.parse(value)["nameSpace"] != null;
        }

        function hasNotNameSpace(value) {
            return JSON.parse(value)["nameSpace"] == null;
        }

        function iterateValueEntry(values) {
            var baseTypes = values.filter(hasNotNameSpace);
            var innerTypes = values.filter(hasNameSpace);
            baseTypes.forEach(createConstructor);
            innerTypes.forEach(createConstructor);
        }

        function iterateKeyValueEntry(key, values) {
            iterateValueEntry(values);
        }

        for (var property in constructorsMap) {
            if (constructorsMap.hasOwnProperty(property)) {
                iterateKeyValueEntry(property, constructorsMap[property]);
            }
        }

        console.log('JsInterop constructors successfully generated.');
    },

    unmarshall: function (text, dynamicNamespace, callback) {
        // Create Jsonix context
        var context = new Jsonix.Context(this.mappings);

        // Create unmarshaller
        var unmarshaller = context.createUnmarshaller();
        var toReturn = unmarshaller.unmarshalString(text);
        callback(toReturn);
    },

    marshall: function (value, namespacesValues, callback) {
        // Create Jsonix context
        var context = new Jsonix.Context(this.mappings, {
            namespacePrefixes: namespacesValues
        });

        // Create marshaller
        var marshaller = context.createMarshaller();
        var xmlDocument = marshaller.marshalDocument(value);
        if (typeof FormatterJs !== "undefined") {
            var toReturn = FormatterJs.format(xmlDocument);
            callback(toReturn);
        } else {
            var s = new XMLSerializer();
            var toReturn = s.serializeToString(xmlDocument);
            callback(toReturn);
        }
    }
}
