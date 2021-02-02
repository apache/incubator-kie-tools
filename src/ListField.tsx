import React, {
  Children,
  ReactNode,
  isValidElement,
  cloneElement,
} from 'react';
import { Tooltip, Split, SplitItem } from '@patternfly/react-core';
import { OutlinedQuestionCircleIcon } from '@patternfly/react-icons';
import { HTMLFieldProps, connectField, filterDOMProps } from 'uniforms/es5';

import ListItemField from './ListItemField';
import ListAddField from './ListAddField';
import ListDelField from './ListDelField';

export type ListFieldProps = HTMLFieldProps<
  unknown[],
  HTMLDivElement,
  {
    children?: ReactNode;
    info?: string;
    error?: boolean;
    initialCount?: number;
    itemProps?: object;
    showInlineError?: boolean;
  }
>;

declare module 'uniforms' {
  interface FilterDOMProps {
    wrapperCol: never;
    labelCol: never;
  }
}

filterDOMProps.register('minCount', 'wrapperCol', 'labelCol');

function ListField({
  children = <ListItemField name="$" />,
  error,
  errorMessage,
  info,
  initialCount,
  itemProps,
  label,
  name,
  value,
  showInlineError,
  ...props
}: ListFieldProps) {
  return (
    <div {...filterDOMProps(props)}>
      <Split hasGutter>
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
        {value?.map((item, itemIndex) =>
          Children.map(children, (child, childIndex) =>
            isValidElement(child)
              ? cloneElement(child, {
                  key: `${itemIndex}-${childIndex}`,
                  name: child.props.name?.replace('$', '' + itemIndex),
                  ...itemProps,
                })
              : child
          )
        )}
      </div>
    </div>
  );
}

export default connectField(ListField);
