import React, { useState } from 'react';
import { AutoForm } from 'uniforms-patternfly/src';

import { CodeBlock } from './CodeBlock';
import schema from './schema/json-schema';
// import schema from './schema/simple-schema-2';

function App() {
  const [model, setModel] = useState(undefined);
  return (
    <div style={containerStyle}>
      <div style={{ width: '60%'}}> 
        <CodeBlock model={model} />
        <AutoForm
          placeholder
          schema={schema}
          onSubmit={m => setModel(m)}
          showInlineError
        />
      </div>
    </div>
  );
}

const containerStyle = {
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
  minHeight: '100vh',
  width: '100vw',
  padding: '10em 0'
}

export default App;
