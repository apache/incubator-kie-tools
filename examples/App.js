import React from 'react';
import { AutoForm } from 'uniforms-patternfly';
import { Button } from '@patternfly/react-core';
import '@patternfly/react-core/dist/styles/base.css';

// import schema from './schema/json-schema';
// import schema from './schema/graphql-schema';
import schema from './schema/simple-schema-2';

function App() {
  return (
    <div
      style={{
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        height: '100vh',
        width: '100vw',
      }}
    >
      <AutoForm
        style={{ width: '50%', margin: '0 auto'}}
        placeholder
        schema={schema}
        onSubmit={model => alert(JSON.stringify(model, null, 2))}
      />
    </div>
  );
}

export default App;
