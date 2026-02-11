document.addEventListener('DOMContentLoaded', async () => {
    if (!Auth.isLoggedIn()) {
        window.location.href = 'login.html';
        return;
    }

    updateUIForAuth();
    loadProfile();
    loadOrders();

    const logoutBtn = document.getElementById('logoutBtn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', (e) => {
            e.preventDefault();
            Auth.logout();
        });
    }
});

function updateUIForAuth() {
    const authLinks = document.getElementById('authLinks');
    const userLinks = document.getElementById('userLinks');
    const userName = document.getElementById('userName');
    const adminLink = document.getElementById('adminLink');

    if (Auth.isLoggedIn()) {
        const user = Auth.getUser();
        authLinks.style.display = 'none';
        userLinks.style.display = 'flex';
        userName.textContent = user.name;
        
        if (Auth.isAdmin()) {
            adminLink.classList.remove('hidden');
            adminLink.href = 'admin.html';
        }
    } else {
        authLinks.style.display = 'flex';
        userLinks.style.display = 'none';
    }
}

async function loadProfile() {
    try {
        // Fetch fresh data from account endpoint if available, or just use stored user
        // We have /auth/account
        const response = await api.get('/auth/account');
        if (response.status && response.status.statusCode === 'OK') {
            const user = response.data;
            document.getElementById('profileName').textContent = user.name;
            document.getElementById('profileEmail').textContent = user.email;
            document.getElementById('profileRole').textContent = user.role;
            document.getElementById('profileGender').textContent = user.gender || 'N/A';
            
            // Update local storage if needed
            Auth.saveUser(user);
        }
    } catch (error) {
        console.error('Failed to load profile', error);
        // Fallback to local storage
        const user = Auth.getUser();
        if (user) {
            document.getElementById('profileName').textContent = user.name;
            document.getElementById('profileEmail').textContent = user.email;
            document.getElementById('profileRole').textContent = user.role;
            document.getElementById('profileGender').textContent = user.gender || 'N/A';
        }
    }
}

async function loadOrders() {
    const tbody = document.getElementById('ordersTableBody');
    const user = Auth.getUser();
    
    try {
        const response = await api.get(`/orders/user/${user.id}`);
        if (response.status && response.status.statusCode === 'OK') {
            const orders = response.data;
            tbody.innerHTML = '';
            
            if (orders.length === 0) {
                tbody.innerHTML = '<tr><td colspan="5">No orders found.</td></tr>';
                return;
            }

            // Sort by ID desc (newest first) since we don't have created_at in DTO easily accessible?
            // Or just assume backend sends them in order (it was mapped to list, maybe valid).
            orders.sort((a, b) => b.id - a.id);

            orders.forEach(order => {
                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td>#${order.id}</td>
                    <td>${new Date().toLocaleDateString()}</td> <!-- Date missing in OrderResponseDTO -->
                    <td>${UI.formatCurrency(order.total_price)}</td>
                    <td><span class="badge ${getStatusBadgeClass(order.status)}">${order.status}</span></td>
                    <td>
                        <button onclick="viewOrderDetails(${order.id})" class="btn btn-outline" style="padding: 4px 8px; font-size: 0.8rem;">View Items</button>
                    </td>
                `;
                tbody.appendChild(tr);
            });
        }
    } catch (error) {
        console.error('Failed to load orders', error);
        tbody.innerHTML = '<tr><td colspan="5" style="color: red;">Failed to load orders.</td></tr>';
    }
}

function getStatusBadgeClass(status) {
    if (status === 'PENDING') return 'badge-pending';
    if (status === 'COMPLETED' || status === 'PAID') return 'badge-success';
    if (status === 'CANCELLED') return 'badge-danger';
    return '';
}

async function viewOrderDetails(orderId) {
    const modal = document.getElementById('orderModal');
    const list = document.getElementById('orderItemsList');
    list.innerHTML = 'Loading items...';
    modal.style.display = 'block';

    try {
        const response = await api.get(`/order-items/orders/${orderId}`);
        if (response.status && response.status.statusCode === 'OK') {
            const items = response.data;
            list.innerHTML = `
                <table class="table">
                    <thead>
                        <tr>
                            <th>Product</th>
                            <th>Price</th>
                            <th>Qty</th>
                            <th>Subtotal</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${items.map(item => `
                            <tr>
                                <td>Product #${item.product.id}</td> <!-- Name missing in OrderItem DTO -->
                                <td>${UI.formatCurrency(item.price)}</td>
                                <td>${item.quantity}</td>
                                <td>${UI.formatCurrency(item.price * item.quantity)}</td>
                            </tr>
                        `).join('')}
                    </tbody>
                </table>
            `;
        }
    } catch (error) {
        list.textContent = 'Failed to load details.';
    }
}

window.closeOrderModal = () => document.getElementById('orderModal').style.display = 'none';
window.onclick = (e) => {
    if (e.target.classList.contains('modal')) e.target.style.display = 'none';
};
