import React from 'react';

function HomePage() {
  const oauthLoginUrl = (provider) =>
    `http://localhost:8080/oauth2/authorization/${provider}`;

  return (
    <div style={{ textAlign: 'center', marginTop: '80px' }}>
      <h1>Auth Login/Register</h1>

      <div style={{ margin: '30px' }}>
        <a href="/signup">
            <button style={{ ...buttonStyle, backgroundColor: '#4CAF50' }}>
            Sign Up with Email
            </button>
        </a>
      </div>

      <div style={{ margin: '10px' }}>
        <a href={oauthLoginUrl('google')}>
          <button style={{ ...buttonStyle, backgroundColor: '#DB4437' }}>
            Sign in with Google
          </button>
        </a>
      </div>

      <div style={{ margin: '10px' }}>
        <a href={oauthLoginUrl('github')}>
          <button style={{ ...buttonStyle, backgroundColor: '#333' }}>
            Sign in with GitHub
          </button>
        </a>
      </div>
    </div>
  );
}

const buttonStyle = {
  padding: '12px 24px',
  fontSize: '16px',
  border: 'none',
  borderRadius: '6px',
  color: 'white',
  cursor: 'pointer'
};

export default HomePage;
