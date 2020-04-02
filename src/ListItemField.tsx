import React, { Children, ReactNode } from 'react';
import { joinName } from 'uniforms';

import AutoField from './AutoField';
import ListDelField from './ListDelField';
import { ListItem } from '@patternfly/react-core';

export type ListItemFieldProps = {
  children?: ReactNode;
  name: string;
  labelCol?: string;
  label?: any;
  wrapperCol?: any;
};

export default function ListItemField(props: ListItemFieldProps) {
  return <ListItem>
    {
      props.children ? (
        Children.map(props.children as JSX.Element, child =>
          React.cloneElement(child, {
            name: joinName(props.name, child.props.name),
            label: null,
          }),
        )
      ) : (
        <AutoField {...props} />
      )
    }
    <ListDelField name={props.name} />
  </ListItem>
};
