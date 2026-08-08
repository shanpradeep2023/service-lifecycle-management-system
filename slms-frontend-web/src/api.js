const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

export async function api(path, options = {}, token = import.meta.env.VITE_API_TOKEN || '') {
  const response = await fetch(`${API_URL}${path}`, {
    ...options,
    headers: { 'Content-Type': 'application/json', ...(token ? { Authorization: `Bearer ${token}` } : {}), ...options.headers }
  });
  const payload = await response.json().catch(() => ({}));
  if (!response.ok || payload.success === false) throw new Error(payload.message || 'Something went wrong');
  return payload.data;
}

export const endpoints = {
  commanderDashboard: () => api('/api/commander/dashboard'),
  adminDashboard: () => api('/api/admin/dashboard'),
  shops: () => api('/api/shops'),
  createShop: body => api('/api/shops/create-shop', { method: 'POST', body: JSON.stringify(body) }),
  tasks: shopId => api(`/api/tasks${shopId ? `?shopId=${shopId}` : ''}`),
  createTask: body => api('/api/tasks', { method: 'POST', body: JSON.stringify(body) }),
  updateTaskStatus: (id, body) => api(`/api/tasks/${id}/status`, { method: 'PATCH', body: JSON.stringify(body) }),
  assignTask: (id, technicianId) => api(`/api/tasks/${id}/assign-technician`, { method: 'PATCH', body: JSON.stringify({ technicianId }) }),
  users: (shopId, role) => api(`/api/user?${new URLSearchParams({ ...(shopId ? { shopId } : {}), ...(role ? { role } : {}) })}`)
};
