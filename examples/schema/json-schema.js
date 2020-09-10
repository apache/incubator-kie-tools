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
  type: 'object',
  properties: {
    flight: {
      type: 'object',
      properties: {
        flightNumber: {
          type: 'string'
        },
        seat: {
          type: 'string'
        },
        gate: {
          type: 'string'
        },
        departure: {
          type: 'string',
          format: 'date-time'
        },
        arrival: {
          type: 'string',
          format: 'date-time'
        }
      },
      disabled: true
    },
    hotel: {
      type: 'object',
      properties: {
        name: {
          type: 'string'
        },
        address: {
          type: 'object',
          properties: {
            street: {
              type: 'string'
            },
            city: {
              type: 'string'
            },
            zipCode: {
              type: 'string'
            },
            country: {
              type: 'string'
            }
          }
        },
        phone: {
          type: 'string'
        },
        bookingNumber: {
          type: 'string'
        },
        room: {
          type: 'string'
        }
      },
      disabled: true
    }
  },
  phases: ['complete', 'release']
};

const schemaValidator = createValidator(schema);

export default new JSONSchemaBridge(schema, schemaValidator);
