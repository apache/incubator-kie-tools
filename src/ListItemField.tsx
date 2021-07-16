import React, { ReactNode } from 'react';
import { connectField } from 'uniforms';

import AutoField from './AutoField';
import ListDelField from './ListDelField';

export type ListItemFieldProps = {
  children?: ReactNode;
  value?: unknown;
};

function ListItem({
  children = <AutoField label={null} name={''} />,
}: ListItemFieldProps) {
  return (
    <div
      style={{
        marginBottom: '1rem',
        display: 'flex',
        justifyContent: 'space-between',
      }}
    >
      <div style={{ width: '100%', marginRight: '10px' }}>{children}</div>
      <div>
        <ListDelField name={''} />
      </div>
    </div>
  );
}

export default connectField<ListItemFieldProps>(ListItem, {
  initialValue: false,
});
