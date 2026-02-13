document.addEventListener('DOMContentLoaded', async () => {
    if (!Auth.isLoggedIn()) {
        window.location.href = 'login.html';
        return;
    }

    updateAuthUI();
    await loadCart();
    logout();

    document.getElementById('checkout-btn').addEventListener('click', handleCheckout);
});

let currentCartId = null;

function logout() {
    const logoutBtn = document.getElementById('logout');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', async (e) => {
            e.preventDefault();
            Auth.logout();
            window.location.reload();
        });
    }
}

function updateAuthUI() {
    const user = Auth.getUser();
    if (user && document.getElementById('userName')) {
        document.getElementById('userName').textContent = user.name;
    }
}

async function loadCart() {
    const user = Auth.getUser();
    const cartItemsContainer = document.getElementById('cart-items');
    const cartContent = document.getElementById('cart-content');
    const emptyMsg = document.getElementById('empty-cart-msg');

    try {
        // 1. Get active cart
        const response = await api.get(`/carts/active/user/${user.id}`);
        
        if (response.status && response.status.statusCode === 'OK' && response.data) {
            currentCartId = response.data.id;
            
            // 2. Get cart items
            const itemsResponse = await api.get(`/cart-items/cart/${currentCartId}`);
            
            if (itemsResponse.status && itemsResponse.status.statusCode === 'OK' && itemsResponse.data.length > 0) {
                const items = itemsResponse.data;
                renderCartItems(items);
                cartContent.style.display = 'block';
                emptyMsg.style.display = 'none';
            } else {
                cartContent.style.display = 'none';
                emptyMsg.style.display = 'block';
            }
        } else {
            cartContent.style.display = 'none';
            emptyMsg.style.display = 'block';
        }
    } catch (error) {
        console.error('Failed to load cart:', error);
        cartContent.style.display = 'none';
        emptyMsg.style.display = 'block';
    }
}

async function renderCartItems(items) {
    const cartItemsContainer = document.getElementById('cart-items');
    cartItemsContainer.innerHTML = '';
    let total = 0;

    // Fetch product details and assets for all items in parallel
    const productPromises = items.map(item => api.get(`/products/${item.product.id}`));
    const assetsPromises = items.map(item => api.get(`/product-assests/product/${item.product.id}`));
    
    const [productsResponses, assetsResponses] = await Promise.all([
        Promise.all(productPromises),
        Promise.all(assetsPromises)
    ]);
    
    const productsMap = {};
    productsResponses.forEach(res => {
        if (res.status && res.status.statusCode === 'OK') {
            const product = res.data;
            productsMap[product.id] = product;
        }
    });

    const assetsMap = {};
    assetsResponses.forEach((res, index) => {
        if (res.status && res.status.statusCode === 'OK') {
            const productId = items[index].product.id;
            assetsMap[productId] = res.data;
        }
    });

    items.forEach(item => {
        const product = productsMap[item.product.id];
        const assets = assetsMap[item.product.id] || [];
        const mainImage = assets.length > 0 ? assets[0].assest_url : `https://via.placeholder.com/60x60?text=${encodeURIComponent(product ? product.name : 'Product')}`;
        const subtotal = item.price_at_time * item.quantity;
        total += subtotal;

        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>
                <div class="cart-item-detail">
                    <img src="${mainImage}" class="cart-item-image" alt="${product ? product.name : 'Product'}">
                    <div>
                        <span class="cart-item-name">${product ? product.name : 'Unknown Product'}</span>
                        <span class="cart-item-price">${UI.formatCurrency(item.price_at_time)}</span>
                    </div>
                </div>
            </td>
            <td>${UI.formatCurrency(item.price_at_time)}</td>
            <td>
                <input type="number" class="quantity-input" value="${item.quantity}" min="1" 
                    onchange="updateItemQuantity(${item.id}, this.value, ${item.price_at_time}, ${item.product.id})">
            </td>
            <td>${UI.formatCurrency(subtotal)}</td>
            <td>
                <button class="btn-remove" onclick="removeItem(${item.id})">Remove</button>
            </td>
        `;
        cartItemsContainer.appendChild(tr);
    });

    document.getElementById('subtotal').textContent = UI.formatCurrency(total);
    document.getElementById('total-price').textContent = UI.formatCurrency(total);
}

window.updateItemQuantity = async (itemId, quantity, price, productId) => {
    if (quantity < 1) return;
    
    try {
        await api.put(`/cart-items/${itemId}`, {
            id: itemId,
            quantity: parseInt(quantity),
            price_at_time: price,
            cart: { id: currentCartId },
            product: { id: productId }
        });
        loadCart();
    } catch (error) {
        console.error('Failed to update quantity:', error);
        alert('Failed to update quantity');
    }
};

window.removeItem = async (itemId) => {
    if (!confirm('Remove this item from your cart?')) return;
    
    try {
        await api.delete(`/cart-items/${itemId}`);
        loadCart();
    } catch (error) {
        console.error('Failed to remove item:', error);
        alert('Failed to remove item');
    }
};

async function handleCheckout() {
    if (!currentCartId) return;
    
    const totalText = document.getElementById('total-price').textContent;
    const total = parseFloat(totalText.replace(/[^0-9.-]+/g, ""));
    const user = Auth.getUser();

    if (!confirm(`Confirm order for ${totalText}?`)) return;

    try {
        // Create Order - Backend automatically archives cart and creates new one
        const response = await api.post('/orders', {
            user: { id: user.id },
            total_price: total,
            status: 'PENDING'
        });

        if (response.status && response.status.statusCode === 'CREATED') {
            alert('Order placed successfully!');
            window.location.href = 'index.html';
        } else {
            alert('Checkout failed: ' + (response.status ? response.status.message : 'Unknown error'));
        }
    } catch (error) {
        console.error('Checkout error:', error);
        alert('An error occurred during checkout.');
    }
}
