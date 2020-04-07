import SimpleSchema2Bridge from 'uniforms-bridge-simple-schema-2';
import SimpleSchema from 'simpl-schema';

const schema = new SimpleSchema({
  date: {
    type: Date,
    defaultValue: new Date()
  },

  adult: {
    type: Boolean
  },

  size: {
    type: String,
    defaultValue: 'm',
    allowedValues: ['xs', 's', 'm', 'l', 'xl']
  },

  rating: {
    type: Number,
    allowedValues: [1, 2, 3, 4, 5],
    uniforms: {
      checkboxes: true
    }
  },

  hello: {
    type: Object
  },

  'hello.something': {
    type: String
  },

  'hello.somethingelse': {
    type: String
  },

  friends: {
    type: Array,
    minCount: 1,
  },

  'friends.$': {
    type: Object,
  },

  'friends.$.name': {
    type: String,
    min: 3
  },

  'friends.$.age': {
    type: Number,
    min: 0,
    max: 150
  }
});

export default new SimpleSchema2Bridge(schema);
