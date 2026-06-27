import React from 'react';
import { useUser, useClerk } from '@clerk/clerk-react';

export default function Dashboard() {
  const { user } = useUser();
  const { signOut } = useClerk();

  return (
    <div style={styles.page}>
      <div style={styles.card}>
        <div style={styles.header}>
          <h2 style={styles.name}>
            {user?.firstName} {user?.lastName}
          </h2>
          <button style={styles.signOut} onClick={() => signOut()}>
            Sign Out
          </button>
        </div>
        <p style={styles.email}>{user?.primaryEmailAddress?.emailAddress}</p>
        <p style={styles.clerkId}>Clerk ID: {user?.id}</p>
      </div>
    </div>
  );
}

const styles = {
  page: {
    minHeight: '100vh', display: 'flex', alignItems: 'center',
    justifyContent: 'center', background: '#f5f5f5',
  },
  card: {
    background: '#fff', borderRadius: 12, padding: 32,
    boxShadow: '0 1px 4px rgba(0,0,0,.08)', minWidth: 340,
  },
  header: { display: 'flex', justifyContent: 'space-between', alignItems: 'center' },
  name: { margin: 0, fontSize: 20, fontWeight: 600 },
  signOut: {
    padding: '6px 14px', borderRadius: 6, border: '1px solid #e5e7eb',
    background: '#fff', cursor: 'pointer', fontSize: 13, color: '#374151',
  },
  email: { color: '#6b7280', marginTop: 4, fontSize: 14 },
  clerkId: { color: '#9ca3af', fontSize: 11, marginTop: 8, wordBreak: 'break-all' },
};