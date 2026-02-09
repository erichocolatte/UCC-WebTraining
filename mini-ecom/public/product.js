document.addEventListener('DOMContentLoaded', async () => {
    const urlParams = new URLSearchParams(window.location.search);
    const productId = urlParams.get('id');

    if (!productId) {
        alert('Product not found!');
        window.location.href = 'index.html';
        return;
    }

    // Load product details to get price
    const product = await loadProductDetails(productId);
    await loadProductAssets(productId);

    document.getElementById('addToCartBtn').addEventListener('click', () => {
        if (product) {
            addToCart(product);
        }
    });
});

async function loadProductDetails(productId) {
    try {
        const response = await api.get(`/products/${productId}`);
        if (response.status && response.status.statusCode === 'OK') {
            const product = response.data.result || response.data; // Handle pagination wrapper or direct
            // API getProductById returns directly the object in data
            
            document.title = product.name + ' - MiniEcom';
            document.getElementById('productName').textContent = product.name;
            document.getElementById('productPrice').textContent = UI.formatCurrency(product.price);
            document.getElementById('productDescription').textContent = product.description;
            if (product.stock) {
                 document.getElementById('quantity').max = product.stock;
            }
            return product;
        }
    } catch (error) {
        console.error('Failed to load product details', error);
    }
    return null;
}

async function loadProductAssets(productId) {
    try {
        const response = await api.get(`/product-assests/product/${productId}`);
        if (response.status && response.status.statusCode === 'OK') {
            const assets = response.data;
            if (assets.length > 0) {
                const mainAsset = assets.find(a => a.is_main) || assets[0];
                document.getElementById('mainImage').src = mainAsset.assest_url;

                const container = document.getElementById('thumbnailContainer');
                container.innerHTML = '';
                assets.forEach(asset => {
                    const thumb = document.createElement('img');
                    thumb.src = asset.assest_url;
                    thumb.style.width = '60px';
                    thumb.style.height = '60px';
                    thumb.style.objectFit = 'cover';
                    thumb.style.marginRight = '10px';
                    thumb.style.cursor = 'pointer';
                    thumb.style.border = '1px solid #ddd';
                    
                    thumb.onclick = () => {
                        document.getElementById('mainImage').src = asset.assest_url;
                    };
                    
                    container.appendChild(thumb);
                });
            }
        }
    } catch (error) {
        console.error('Failed to load product assets', error);
    }
}

async function addToCart(product) {
    if (!Auth.isLoggedIn()) {
        alert('Please login to add items to cart.');
        window.location.href = 'login.html';
        return;
    }

    const quantity = parseInt(document.getElementById('quantity').value);
    
    try {
        const user = Auth.getUser();
        
        // 1. Get existing cart
        const cartsResponse = await api.get(`/carts/user/${user.id}`);
        let cartId = null;

        if (cartsResponse.status && cartsResponse.status.statusCode === 'OK') {
            const carts = cartsResponse.data;
             if (carts.length > 0) {
                 cartId = carts[0].id;
             }
        }

        // 2. Create cart if not exists
        if (!cartId) {
            const createCartResponse = await api.post('/carts', {
                status: 'ACTIVE',
                user: { id: user.id }
            });
            if (createCartResponse.status && createCartResponse.status.statusCode === 'CREATED') {
                cartId = createCartResponse.data.id;
            } else {
                alert('Failed to create cart.');
                return;
            }
        }

        // 3. Add item to cart
        if (cartId) {
            const addItemResponse = await api.post('/cart-items', {
                cart: { id: cartId },
                product: { id: product.id }, // Assuming product object has id, check loadProductDetails
                // Wait, loadProductDetails response data has id? Check Controller.
                // ProductController getProductById returns GetProductResponseDTO.
                // We added 'id' to GetProductResponseDTO. So yes.
                quantity: quantity,
                price_at_time: product.price 
            });

            if (addItemResponse.status && addItemResponse.status.statusCode === 'CREATED') {
                alert('Added to cart!');
            } else {
                alert('Failed to add to cart: ' + addItemResponse.status.message);
            }
        }

    } catch (error) {
        console.error('Add to cart error', error);
        alert('Error adding to cart.');
    }
}
