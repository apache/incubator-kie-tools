import React, { Children, HTMLProps, ReactNode } from 'react';
import { connectField, filterDOMProps, joinName } from 'uniforms';
import { List } from '@patternfly/react-core';

import ListItemField from './ListItemField';
import ListAddField from './ListAddField';
import { Tooltip } from '@patternfly/react-core';
import { OutlinedQuestionCircleIcon } from '@patternfly/react-icons';

export type ListFieldProps<T> = {
  value: T[];
  children?: ReactNode;
  addIcon?: any;
  error?: boolean;
  info?: boolean;
  errorMessage?: string;
  initialCount?: number;
  itemProps?: {};
  labelCol?: any;
  label: string;
  wrapperCol?: any;
  name: string;
  showInlineError?: boolean;
} & Omit<HTMLProps<HTMLDivElement>, 'children' | 'name'>;

function ListField<T>({
  children,
  error,
  errorMessage,
  info,
  initialCount,
  itemProps,
  label,
  labelCol,
  name,
  showInlineError,
  value,
  wrapperCol,
  ...props
}: ListFieldProps<T>) {
  return (
    <div {...filterDOMProps(props)}>
      {label && (
        <div>
          {label}
          {!!info && (
            <span>
              &nbsp;
              <Tooltip content={info}>
                <OutlinedQuestionCircleIcon />
              </Tooltip>
            </span>
          )}
        </div>
      )}

      {!!(error && showInlineError) && <div>{errorMessage}</div>}
      
      <List component="ul"> 
        {
          children
          ? value.map((item: any, index: number) =>
              Children.map(children as JSX.Element, child =>
                React.cloneElement(child, {
                  key: index,
                  label: null,
                  name: joinName(
                    name,
                    child.props.name && child.props.name.replace('$', index),
                  ),
                }),
              ),
            )
          : value.map((item: any, index: number) => (
              <ListItemField
                key={index}
                label={undefined}
                name={joinName(name, index)}
                {...itemProps}
              />
            ))
        }
      </List>

      <ListAddField name={`${name}.$`} initialCount={initialCount} />
    </div>
  );
};

export default connectField(ListField, {
  includeInChain: false,
});
