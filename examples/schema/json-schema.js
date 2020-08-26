import Ajv from 'ajv';
import { JSONSchemaBridge } from 'uniforms-bridge-json-schema';

const ajv = new Ajv({ allErrors: true, useDefaults: true });

function createValidator(schema) {
  const validator = ajv.compile(schema);
  return model => {
    validator(model);
    if (validator.errors && validator.errors.length) {
      throw { details: validator.errors };
    }
  };
}

const schema = {
  'phases': [
    'complete',
    'release'
  ],
  'properties': {
    'traveller': {
      'properties': {
        'address': {
          'properties': {
            'city': {
              'type': 'string'
            },
            'country': {
              'type': 'string'
            },
            'street': {
              'type': 'string'
            },
            'zipCode': {
              'type': 'string'
            }
          },
          'type': 'object'
        },
        'email': {
          'format': 'email',
          'type': 'string'
        },
        'firstName': {
          'type': 'string'
        },
        'lastName': {
          'type': 'string'
        },
        'nationality': {
          'type': 'string'
        }
      },
      'required': [
        'firstName',
        'lastName'
      ],
      'type': 'object'
    },
    'trip': {
      'properties': {
        'begin': {
          'format': 'date-time',
          'type': 'string'
        },
        'city': {
          'type': 'string'
        },
        'country': {
          'type': 'string'
        },
        'end': {
          'format': 'date-time',
          'type': 'string'
        },
        'visaRequired': {
          'type': 'boolean'
        }
      },
      'type': 'object',
      'uniforms': {
        'disabled': true
      }
    },
    'visaApplication': {
      'properties': {
        'city': {
          'type': 'string'
        },
        'country': {
          'type': 'string'
        },
        'duration': {
          'type': 'integer'
        },
        'firstName': {
          'type': 'string'
        },
        'lastName': {
          'type': 'string'
        },
        'nationality': {
          'type': 'string'
        },
        'passportNumber': {
          'type': 'string'
        }
      },
      'type': 'object',
      'uniforms': {
        'disabled': true
      }
    }
  },
  'type': 'object'
};

const schemaValidator = createValidator(schema);

export default new JSONSchemaBridge(schema, schemaValidator);
