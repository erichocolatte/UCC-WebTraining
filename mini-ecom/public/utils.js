const API_BASE_URL = 'http://localhost:8080/api/v1';

// Auth Utilities
const Auth = {
    saveToken: (token) => localStorage.setItem('accessToken', token),
    getToken: () => localStorage.getItem('accessToken'),
    removeToken: () => localStorage.removeItem('accessToken'),
    
    saveUser: (user) => localStorage.setItem('user', JSON.stringify(user)),
    getUser: () => JSON.parse(localStorage.getItem('user')),
    removeUser: () => localStorage.removeItem('user'),
    
    isLoggedIn: () => !!localStorage.getItem('accessToken'),
    
    logout: async (skipApi = false) => {
        if (!skipApi) {
            try {
                // Using fetch directly to avoid recursion issues or dependency order if api is not ready (though it should be)
                // Also explicitly including credentials
                const token = Auth.getToken();
                await fetch(`${API_BASE_URL}/auth/logout`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token}`
                    },
                    credentials: 'include'
                });
            } catch (error) {
                console.error('Logout backend call failed:', error);
            }
        }
        
        Auth.removeToken();
        Auth.removeUser();
        window.location.href = '/login.html';
    },

    isAdmin: () => {
        const user = Auth.getUser();
        // Assuming role name is strictly checked. Adjust if role is an object or has 'ROLE_' prefix
        return user && (user.role === 'SELLER' || user.role === 'ROLE_SELLER');
    }
};

// Fetch Wrapper
const api = {
    get: async (endpoint) => {
        return api.request(endpoint, 'GET');
    },

    post: async (endpoint, data) => {
        return api.request(endpoint, 'POST', data);
    },

    put: async (endpoint, data) => {
        return api.request(endpoint, 'PUT', data);
    },

    delete: async (endpoint) => {
        return api.request(endpoint, 'DELETE');
    },

    request: async (endpoint, method, data = null) => {
        const headers = {
            'Content-Type': 'application/json',
        };

        const token = Auth.getToken();
        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }

        const config = {
            method,
            headers,
            credentials: 'include',
        };

        if (data) {
            config.body = JSON.stringify(data);
        }

        try {
            const response = await fetch(`${API_BASE_URL}${endpoint}`, config);
            
            // Handle 401 Unauthorized
            if (response.status === 401) {
                // Ideally try to refresh token here, for now just logout
                // Pass true to skip API call to avoid infinite loop if logout itself returns 401
                Auth.logout(true);
                throw new Error('Unauthorized');
            }

            const result = await response.json();
            return result;
        } catch (error) {
            console.error('API Request Error:', error);
            throw error;
        }
    }
};

// UI Helpers
const UI = {
    formatCurrency: (amount) => {
        return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(amount);
    },
    
    showToast: (message, type = 'info') => {
        // Simple alert for now, can be improved to a custom toast
        alert(message);
    }
};
