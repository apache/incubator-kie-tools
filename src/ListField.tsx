import React, { Children, HTMLProps, ReactNode } from 'react';
import { Tooltip, Split, SplitItem, Divider } from '@patternfly/react-core';
import { OutlinedQuestionCircleIcon } from '@patternfly/react-icons';
import { connectField, filterDOMProps, joinName } from 'uniforms/es5';

import ListItemField from './ListItemField';
import ListAddField from './ListAddField';
import { ListDelField } from '.';

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

filterDOMProps.register('minCount');

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
      <Split gutter="md">
        <SplitItem>
          {label && (
            <label>
              {label}
              {!!info && (
                <span>
                  &nbsp;
                  <Tooltip content={info}>
                    <OutlinedQuestionCircleIcon />
                  </Tooltip>
                </span>
              )}
            </label>
          )}
        </SplitItem>
        <SplitItem isFilled />
        <SplitItem>
          <ListAddField name={`${name}.$`} initialCount={initialCount} />{' '}
          <ListDelField name={`${name}.$`} />
        </SplitItem>
      </Split>

      <div>
        {children
          ? value.map((item: any, index: number) =>
              Children.map(children as JSX.Element, child =>
                React.cloneElement(child, {
                  key: index,
                  label: '',
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
                label={null}
                name={joinName(name, index)}
                {...itemProps}
              />
            ))}
      </div>
    </div>
  );
}

export default connectField<ListFieldProps<any>>(ListField, {
  includeInChain: false,
});
