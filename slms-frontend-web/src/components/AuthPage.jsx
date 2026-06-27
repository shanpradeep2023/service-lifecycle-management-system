import React, { useState } from 'react';
import { SignIn, SignUp } from '@clerk/clerk-react';

export default function AuthPage() {
  const [mode, setMode] = useState('sign-in');

  return (
    <div style={styles.container}>
      <h1 style={styles.title}>FieldOps</h1>

      <div style={styles.toggle}>
        <button
          style={mode === 'sign-in' ? styles.activeTab : styles.tab}
          onClick={() => setMode('sign-in')}
        >
          Sign In
        </button>
        <button
          style={mode === 'sign-up' ? styles.activeTab : styles.tab}
          onClick={() => setMode('sign-up')}
        >
          Sign Up
        </button>
      </div>

      <div style={styles.clerkWidget}>
        {mode === 'sign-in' ? (
          <SignIn routing="hash" afterSignInUrl="/dashboard" />
        ) : (
          <SignUp routing="hash" afterSignUpUrl="/dashboard" />
        )}
      </div>
    </div>
  );
}

const styles = {
  container: {
    minHeight: '100vh', display: 'flex', flexDirection: 'column',
    alignItems: 'center', justifyContent: 'center',
    background: '#f5f5f5', fontFamily: 'Inter, sans-serif',
  },
  title: { fontSize: 28, fontWeight: 700, marginBottom: 24, color: '#1a1a1a' },
  toggle: { display: 'flex', gap: 8, marginBottom: 20 },
  tab: {
    padding: '8px 20px', borderRadius: 6, border: '1px solid #d1d5db',
    background: '#fff', cursor: 'pointer', fontSize: 14, color: '#6b7280',
  },
  activeTab: {
    padding: '8px 20px', borderRadius: 6, border: '1px solid #4f46e5',
    background: '#4f46e5', cursor: 'pointer', fontSize: 14, color: '#fff',
  },
  clerkWidget: { width: '100%', maxWidth: 480 },
};