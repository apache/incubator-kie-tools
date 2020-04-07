import React, { useState } from 'react';
import { AutoForm } from 'uniforms-patternfly';

import schema from './schema/json-schema';
// import schema from './schema/simple-schema-2';

function App() {
  const [model, setModel] = useState();
  return (
    <div
      style={{
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        minHeight: '100vh',
        width: '100vw',
      }}
    >
      <div
        style={{ width: '60%'}}
      > 
        {
          model && 
          <div style={{ marginBottom: '1em' }}>
            <h2>Result:</h2>
            <pre style={{background: '#eee', padding: '1rem'}}>
              {(JSON.stringify(model, null, 2))}
            </pre>
          </div>
        }
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

export default App;
