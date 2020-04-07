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
    name: {
      type: 'string',
      title: 'name',
      uniforms: {
        title: 'Name',
      }
    },
    surname: {
      type: 'string',
      uniforms: {
        placeholder: 'Surname',
      }
    },
    address_one: {
      type: 'string',
      title: 'Address Line One',
      uniforms: {
        label: 'Address Line One'
      }
    },
    address_two: {
      type: 'string',
      title: 'Address Line Two',
    },
    city: {
      type: 'string',
      title: 'City',
      uniforms: {
        label: 'City',
        placeholder: 'City',
        errorMessage: 'City is required'
      }
    },
    state: {
      type: 'string',
      title: 'State/Province/Region',
      uniforms: {
        label: 'State/Province/Region',
        placeholder: 'State/Province/Region',
        errorMessage: 'State/Province/Region is required'
      }
    },
    postal_code: {
      type: 'string',
      title: 'Post code',
      uniforms: {
        label: 'Post code',
        errorMessage: 'Post code is required'
      }
    },
    country: {
      type: 'string',
      title: 'Country',
      uniforms: {
        placeholder: 'Select a country',
        defaultValue: 'Ireland',
        allowedValues: [
          'Ireland', 'United Kingdom', 'Australia', 'USA', 'New Zealand'
        ]
      }
    }
  },
  required: [
    'name', 'surname', 'address_one',
    'city', 'country'
  ]
};

const schemaValidator = createValidator(schema);

export default new JSONSchemaBridge(schema, schemaValidator);
