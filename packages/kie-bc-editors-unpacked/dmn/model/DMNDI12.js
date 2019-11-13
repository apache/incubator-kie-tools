var DMNDI12_Module_Factory = function () {
  var DMNDI12 = {
    name: 'DMNDI12',
    defaultElementNamespaceURI: 'http:\/\/www.omg.org\/spec\/DMN\/20180521\/DMNDI\/',
    dependencies: ['DI', 'DC'],
    typeInfos: [{
        localName: 'DMNEdge',
        baseTypeInfo: 'DI.Edge',
        propertyInfos: [{
            name: 'otherAttributes',
            type: 'anyAttribute'
          }, {
            name: 'dmnLabel',
            elementName: 'DMNLabel',
            typeInfo: '.DMNLabel'
          }, {
            name: 'dmnElementRef',
            required: true,
            typeInfo: 'QName',
            attributeName: {
              localPart: 'dmnElementRef'
            },
            type: 'attribute'
          }]
      }, {
        localName: 'DMNLabel',
        baseTypeInfo: 'DI.Shape',
        propertyInfos: [{
            name: 'otherAttributes',
            type: 'anyAttribute'
          }, {
            name: 'text',
            elementName: 'Text'
          }]
      }, {
        localName: 'DMNDecisionServiceDividerLine',
        baseTypeInfo: 'DI.Edge',
        propertyInfos: [{
            name: 'otherAttributes',
            type: 'anyAttribute'
          }]
      }, {
        localName: 'DMNShape',
        baseTypeInfo: 'DI.Shape',
        propertyInfos: [{
            name: 'otherAttributes',
            type: 'anyAttribute'
          }, {
            name: 'dmnLabel',
            elementName: 'DMNLabel',
            typeInfo: '.DMNLabel'
          }, {
            name: 'dmnDecisionServiceDividerLine',
            elementName: 'DMNDecisionServiceDividerLine',
            typeInfo: '.DMNDecisionServiceDividerLine'
          }, {
            name: 'dmnElementRef',
            required: true,
            typeInfo: 'QName',
            attributeName: {
              localPart: 'dmnElementRef'
            },
            type: 'attribute'
          }, {
            name: 'isListedInputData',
            typeInfo: 'Boolean',
            attributeName: {
              localPart: 'isListedInputData'
            },
            type: 'attribute'
          }, {
            name: 'isCollapsed',
            typeInfo: 'Boolean',
            attributeName: {
              localPart: 'isCollapsed'
            },
            type: 'attribute'
          }]
      }, {
        localName: 'DMNDiagram',
        baseTypeInfo: 'DI.Diagram',
        propertyInfos: [{
            name: 'otherAttributes',
            type: 'anyAttribute'
          }, {
            name: 'size',
            elementName: 'Size',
            typeInfo: 'DC.Dimension'
          }, {
            name: 'dmnDiagramElement',
            minOccurs: 0,
            collection: true,
            mixed: false,
            allowDom: false,
            elementName: 'DMNDiagramElement',
            typeInfo: 'DI.DiagramElement',
            type: 'elementRef'
          }]
      }, {
        localName: 'DMNDI',
        propertyInfos: [{
            name: 'dmnDiagram',
            minOccurs: 0,
            collection: true,
            elementName: 'DMNDiagram',
            typeInfo: '.DMNDiagram'
          }, {
            name: 'dmnStyle',
            minOccurs: 0,
            collection: true,
            elementName: 'DMNStyle',
            typeInfo: '.DMNStyle'
          }]
      }, {
        localName: 'DMNStyle',
        baseTypeInfo: 'DI.Style',
        propertyInfos: [{
            name: 'otherAttributes',
            type: 'anyAttribute'
          }, {
            name: 'fillColor',
            elementName: 'FillColor',
            typeInfo: 'DC.Color'
          }, {
            name: 'strokeColor',
            elementName: 'StrokeColor',
            typeInfo: 'DC.Color'
          }, {
            name: 'fontColor',
            elementName: 'FontColor',
            typeInfo: 'DC.Color'
          }, {
            name: 'fontFamily',
            attributeName: {
              localPart: 'fontFamily'
            },
            type: 'attribute'
          }, {
            name: 'fontSize',
            typeInfo: 'Double',
            attributeName: {
              localPart: 'fontSize'
            },
            type: 'attribute'
          }, {
            name: 'fontItalic',
            typeInfo: 'Boolean',
            attributeName: {
              localPart: 'fontItalic'
            },
            type: 'attribute'
          }, {
            name: 'fontBold',
            typeInfo: 'Boolean',
            attributeName: {
              localPart: 'fontBold'
            },
            type: 'attribute'
          }, {
            name: 'fontUnderline',
            typeInfo: 'Boolean',
            attributeName: {
              localPart: 'fontUnderline'
            },
            type: 'attribute'
          }, {
            name: 'fontStrikeThrough',
            typeInfo: 'Boolean',
            attributeName: {
              localPart: 'fontStrikeThrough'
            },
            type: 'attribute'
          }, {
            name: 'labelHorizontalAlignement',
            typeInfo: 'DC.AlignmentKind',
            attributeName: {
              localPart: 'labelHorizontalAlignement'
            },
            type: 'attribute'
          }, {
            name: 'labelVerticalAlignment',
            typeInfo: 'DC.AlignmentKind',
            attributeName: {
              localPart: 'labelVerticalAlignment'
            },
            type: 'attribute'
          }]
      }],
    elementInfos: [{
        typeInfo: '.DMNEdge',
        elementName: 'DMNEdge',
        substitutionHead: 'DMNDiagramElement'
      }, {
        typeInfo: '.DMNStyle',
        elementName: 'DMNStyle',
        substitutionHead: {
          localPart: 'Style',
          namespaceURI: 'http:\/\/www.omg.org\/spec\/DMN\/20180521\/DI\/'
        }
      }, {
        typeInfo: '.DMNDI',
        elementName: 'DMNDI'
      }, {
        typeInfo: '.DMNDiagram',
        elementName: 'DMNDiagram'
      }, {
        typeInfo: '.DMNDecisionServiceDividerLine',
        elementName: 'DMNDecisionServiceDividerLine'
      }, {
        typeInfo: 'DI.DiagramElement',
        elementName: 'DMNDiagramElement'
      }, {
        typeInfo: '.DMNLabel',
        elementName: 'DMNLabel'
      }, {
        typeInfo: '.DMNShape',
        elementName: 'DMNShape',
        substitutionHead: 'DMNDiagramElement'
      }]
  };
  return {
    DMNDI12: DMNDI12
  };
};
if (typeof define === 'function' && define.amd) {
  define([], DMNDI12_Module_Factory);
}
else {
  var DMNDI12_Module = DMNDI12_Module_Factory();
  if (typeof module !== 'undefined' && module.exports) {
    module.exports.DMNDI12 = DMNDI12_Module.DMNDI12;
  }
  else {
    var DMNDI12 = DMNDI12_Module.DMNDI12;
  }
}