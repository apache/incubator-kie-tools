/**
 * == READ ME ==
 *
 * This file has been manually modified to include *ALL* mappings (and not just DMN12)
 *
 * @type {{marshall: MainJs.marshall, unmarshall: MainJs.unmarshall}}
 */

MainJs = {
    initializeMappings: function () {
        function remapDMN12(version, namespace, namespaceDmnDi, namespaceDi) {
            var dmnMock = JSON.parse(JSON.stringify(DMN12));
            var dmnDiMock = JSON.parse(JSON.stringify(DMNDI12));

            dmnMock.name = "DMN" + version;
            dmnMock.defaultElementNamespaceURI = namespace;
            dmnMock.dependencies = ["DMNDI" + version];

            (dmnMock.typeInfos || []).map(function (typeInfo) {
                (typeInfo.propertyInfos || []).map(function (propertyInfo) {
                    if (propertyInfo.name === "dmndi") {
                        propertyInfo.elementName.namespaceURI = namespaceDmnDi;
                        propertyInfo.typeInfo = "DMNDI" + version + ".DMNDI";
                    }
                });
            });

            dmnDiMock.name = "DMNDI" + version;
            dmnDiMock.defaultElementNamespaceURI = namespaceDmnDi;

            (dmnDiMock.elementInfos || []).map(function (elementInfo) {
                if (elementInfo.elementName === "DMNStyle") {
                    elementInfo.substitutionHead.namespaceURI = namespaceDi;
                }
            });

            return [dmnMock, dmnDiMock];
        }

        var DMN10 = remapDMN12(
                "10",
                "http://www.omg.org/spec/DMN/20130901",
                "http://www.omg.org/spec/DMN/20130901/DMNDI/",
                "http://www.omg.org/spec/DMN/20130901/DI/"
        );

        var DMN11 = remapDMN12(
                "11",
                "http://www.omg.org/spec/DMN/20151101/dmn.xsd",
                "http://www.omg.org/spec/DMN/20151101/DMNDI/",
                "http://www.omg.org/spec/DMN/20151101/DI/"
        );

        var DMN13 = remapDMN12(
                "13",
                "https://www.omg.org/spec/DMN/20191111/MODEL/",
                "https://www.omg.org/spec/DMN/20191111/DMNDI/",
                "https://www.omg.org/spec/DMN/20191111/DI/"
        );

        return [].concat.apply(
                [DC, DI, DMNDI12, DMN12, KIE],
                [DMN10, DMN11, DMN13]
        );
    },

    _mappings: [],

    mappings: function initializeMappings() {
        if (this._mappings.length === 0) {
            this._mappings = this.initializeMappings();
        }
        return this._mappings;
    },

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
            return new Function("return { }");
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

        console.log("JsInterop constructors successfully generated.");
    },

    unmarshall: function (text, dynamicNamespace, callback) {

        function getNamespaces() {
            if (namespaces === undefined) {
                namespaces = toReturn.value.otherAttributes;
            }
            return namespaces;
        }

        function getNamespaceValues() {
            if (namespaceValues === undefined) {
                namespaceValues = Object.keys(getNamespaces());
            }
            return namespaceValues;
        }

        function patchObjectTypesToDMN12(obj, property) {
            // Patch namespaces from other DMN versions to DMN 1.2
            if (property === "TYPE_NAME") {
                obj[property] = obj[property]
                        .replace(/DMN(10|11|13)/, "DMN12")
                        .replace(/DMNDI(10|11|13)/, "DMNDI12");
            }
            // Patch types prefixed by namespaces
            if (property === "typeRef") {
                getNamespaceValues().forEach(function (namespace) {
                    obj[property] = obj[property].replace(new RegExp('^' + namespace.split('}')[1] + ':'), '');
                });
            }
        }

        function patchObjectNamespaceValuesToDMN12(obj, property) {
            if (typeof obj[property] === "string") {
                obj[property] = obj[property]
                        .replace(
                                "http://www.omg.org/spec/DMN/20151101/dmn.xsd",
                                "http://www.omg.org/spec/DMN/20180521/MODEL/"
                        )
                        .replace(
                                /(http|https):\/\/www\.omg\.org\/spec\/DMN\/(20130901|20151101|20191111)/,
                                "http://www.omg.org/spec/DMN/20180521"
                        );
            }
        }

        function patchParsedModelPrefixedNamespaces() {
            var namespaces = getNamespaces();
            var dmnModelNamespace = namespaces.namespace;
            for (var prop in namespaces) {
                if (namespaces.hasOwnProperty(prop) && dmnModelNamespace !== prop && namespaces[prop] === dmnModelNamespace) {
                    delete namespaces[prop];
                }
            }
        }

        function patchParsedModelProperties(obj) {
            for (var property in obj) {
                if (obj.hasOwnProperty(property)) {
                    if (obj[property] !== null && typeof obj[property] === "object") {
                        patchParsedModelProperties(obj[property]);
                    } else {
                        patchObjectTypesToDMN12(obj, property);
                        patchObjectNamespaceValuesToDMN12(obj, property);
                    }
                }
            }
        }

        // Create Jsonix context
        var context = new Jsonix.Context(this.mappings());
        var namespaces;
        var namespaceValues;

        // Create unmarshaller
        var unmarshaller = context.createUnmarshaller();
        var toReturn = unmarshaller.unmarshalString(text);
        var modelURI = toReturn.name.namespaceURI;
        var isDMN12 = modelURI.match(
                new RegExp("http://www.omg.org/spec/DMN/20180521/MODEL/", "g")
        );

        if (!isDMN12) {
            patchParsedModelProperties(toReturn);
            patchParsedModelPrefixedNamespaces();
        }

        callback(toReturn);
    },

    marshall: function (value, namespacesValues, callback) {
        // Create Jsonix context
        var context = new Jsonix.Context(this.mappings(), {
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
};
