document.addEventListener('DOMContentLoaded', async () => {
    if (!Auth.isLoggedIn()) {
        window.location.href = 'login.html';
        return;
    }

    await loadCart();

    document.getElementById('checkoutBtn').addEventListener('click', checkout);
});

let currentCartId = null;

async function loadCart() {
    const tbody = document.getElementById('cartBody');
    tbody.innerHTML = '<tr><td colspan="5">Loading...</td></tr>';

    try {
        const user = Auth.getUser();
        const cartsResponse = await api.get(`/carts/user/${user.id}`);
        
        if (cartsResponse.status && cartsResponse.status.statusCode === 'OK' && cartsResponse.data.length > 0) {
            currentCartId = cartsResponse.data[0].id;
            
            // Get Cart Items
            // CartItemController: GET /api/v1/cart-items/cart/{cartId}
            const itemsResponse = await api.get(`/cart-items/cart/${currentCartId}`);
            
            if (itemsResponse.status && itemsResponse.status.statusCode === 'OK') {
                const items = itemsResponse.data;
                renderCartItems(items);
            } else {
                 tbody.innerHTML = '<tr><td colspan="5">Your cart is empty.</td></tr>';
            }

        } else {
            tbody.innerHTML = '<tr><td colspan="5">Your cart is empty.</td></tr>';
        }

    } catch (error) {
        console.error('Load cart error', error);
        tbody.innerHTML = '<tr><td colspan="5" style="color: red">Error loading cart.</td></tr>';
    }
}

async function renderCartItems(items) {
    const tbody = document.getElementById('cartBody');
    tbody.innerHTML = '';
    
    let total = 0;

    // We need to fetch product details for names if not included in item response
    // CartItemResponseDTO: product is CartItemProductDTO (only id?)
    // CartItemProductDTO seems to only have ID based on CartItemController code.
    // So we must fetch product info for each item. This is N+1, but okay for frontend for now.
    
    // Check CartItemController:
    // .product(CartItemProductDTO.builder().id(...).build())
    // Yes, only ID. 
    
    // We can use Promise.all to fetch product details in parallel
    const productPromises = items.map(item => api.get(`/products/${item.product.id}`));
    const productsResponses = await Promise.all(productPromises);
    
    // Map product data
    const productsMap = {};
    productsResponses.forEach(res => {
        if(res.status && res.status.statusCode === 'OK') {
           const p = res.data; // or res.data.result
           productsMap[p.id] = p; // Assuming p is object with id, name etc.
           // Wait, ProductController returns GetProductResponseDTO which HAS ID now.
        }
    });

    if (items.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5">Your cart is empty.</td></tr>';
        document.getElementById('cartTotal').textContent = UI.formatCurrency(0);
        return;
    }

    items.forEach(item => {
        const product = productsMap[item.product.id];
        const productName = product ? product.name : `Product #${item.product.id}`;
        // Use price_at_time for calculation? Or current price?
        // Usually cart shows current price? user logic says price_at_time in cart item.
        // Let's use item.price_at_time.
        const itemTotal = item.price_at_time * item.quantity;
        total += itemTotal;

        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td><a href="product.html?id=${item.product.id}">${productName}</a></td>
            <td>${UI.formatCurrency(item.price_at_time)}</td>
            <td>
                <input type="number" min="1" value="${item.quantity}" onchange="updateQuantity(${item.id}, this.value, ${item.price_at_time}, ${item.product.id})" style="width: 60px;">
            </td>
            <td>${UI.formatCurrency(itemTotal)}</td>
            <td>
                <button onclick="removeCartItem(${item.id})" class="btn btn-delete action-btn">Remove</button>
            </td>
        `;
        tbody.appendChild(tr);
    });

    document.getElementById('cartTotal').textContent = UI.formatCurrency(total);
}

// Make functions global for onclick
window.updateQuantity = async (itemId, newQuantity, price, productId) => {
    if (newQuantity < 1) return;
    try {
        await api.put(`/cart-items/${itemId}`, {
            id: itemId, // Check if ID is needed in body (CartItemController might need it or ignore)
            quantity: parseInt(newQuantity),
            price_at_time: price,
            cart: { id: currentCartId },
            product: { id: productId }
        });
        loadCart(); // Reload to refresh totals safely
    } catch (error) {
        console.error('Update quantity error', error);
        alert('Failed to update quantity');
    }
};

window.removeCartItem = async (itemId) => {
    if(!confirm('Are you sure?')) return;
    try {
        await api.delete(`/cart-items/${itemId}`);
        loadCart();
    } catch (error) {
        console.error('Remove item error', error);
        alert('Failed to remove item');
    }
};

async function checkout() {
    if (!currentCartId) return;
    
    if(!confirm('Proceed to checkout?')) return;

    // OrderController: POST /api/v1/orders
    // RequestBody: Order newOrder
    // Order model needs: user, total_price, status
    
    // We need to calculate total price again or backend does it?
    // OrderService.handleCreateOrder(newOrder) -> Order
    // Let's see OrderController.
    // It takes Order object.
    
    try {
        // Calculate total from UI text or internal logic
        // Better to send what backend expects.
        // Assuming backend calculates? No, usually frontend sends or backend calculates from cart.
        // But here we are creating an Order directly.
        // Does OrderController convert Cart to Order? 
        // No, it just creates an Order.
        // So we need to:
        // 1. Create Order
        // 2. Create OrderItems from CartItems?
        // 3. Clear Cart (or archive it)?
        
        // This flow seems manual in this mini-ecom.
        // Let's implement basic checkout: Create Order.
        
        // Fetch cart items to calculate total for Order
         const tbody = document.getElementById('cartBody'); // Hacky.
         // Better: we already computed total.
         const totalText = document.getElementById('cartTotal').textContent;
         const total = parseFloat(totalText.replace(/[^0-9.-]+/g,"")); // Remove currency symbol
         
         const user = Auth.getUser();

         const response = await api.post('/orders', {
             user: { id: user.id },
             total_price: total,
             status: 'PENDING'
         });

         if (response.status && response.status.statusCode === 'CREATED') {
             const orderId = response.data.id || response.data.user.id; // Check response DTO structure
             // Response is OrderResponseDTO... wait, it doesn't have ID!
             // OrderResponseDTO: total_price, status, user (OrderUserDTO(id)).
             // It seems OrderResponseDTO is missing ID of the order itself?
             // Checking OrderController.java...
             // .data(OrderResponseDTO.builder()... no ID field for Order itself!?
             // This is another DTO issue.
             
             alert('Order placed successfully! (Note: Order ID missing in response)');
             
             // Optionally: Clear cart items?
             // Since we have no "Cart to Order" backend logic, we manually clear cart items?
             // Or just leave them?
             // For a real demo, we should probably clear them.
             
             // Get attributes for delete
            //  const itemsResponse = await api.get(`/cart-items/cart/${currentCartId}`);
            //  if (itemsResponse.data) {
            //      for(const item of itemsResponse.data) {
            //          await api.delete(`/cart-items/${item.id}`);
            //      }
            //  }
             
             // Or archive the cart?
             // CartController updateCart
             await api.put(`/carts/${currentCartId}`, {
                 status: 'ARCHIVED',
                 user: { id: user.id }
             });
             
             window.location.href = 'index.html';
         } else {
             alert('Checkout failed.');
         }

    } catch (error) {
        console.error('Checkout error', error);
        alert('Checkout error');
    }
}
