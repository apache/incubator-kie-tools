import React from 'react';

export const CodeBlock = ({ model }) => {

  if (!model) return <><span></span></>;

  return (
    <div style={{ marginBottom: '1em' }}>
      <h2>Result:</h2>
      <pre style={{background: '#eee', padding: '1rem'}}>
        {(JSON.stringify(model, null, 2))}
      </pre>
    </div>
  );

};
