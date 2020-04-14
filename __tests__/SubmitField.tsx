import React from 'react';
import { Button } from '@patternfly/react-core';

import { SubmitField } from 'uniforms-patternfly';
import createContext from './_createContext';
import mount from './_mount';

test('<SubmitField> - renders', () => {
  const element = <SubmitField />;
  const wrapper = mount(element, createContext());

  expect(wrapper).toHaveLength(1);
});

test('<SubmitField> - renders disabled if error', () => {
  const element = <SubmitField />;
  const wrapper = mount(element, createContext(undefined, { error: {} }));

  expect(wrapper).toHaveLength(1);
  expect(wrapper.find(Button).prop('isDisabled')).toBe(true);
});

test('<SubmitField> - renders enabled if error and enabled', () => {
  const element = <SubmitField disabled={false} />;
  const wrapper = mount(element, createContext(undefined, { error: {} }));

  expect(wrapper).toHaveLength(1);
  expect(wrapper.find(Button).prop('isDisabled')).toBe(false);
});
