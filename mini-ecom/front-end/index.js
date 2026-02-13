document.addEventListener('DOMContentLoaded', async () => {
    checkAuth();
    await loadCategories();
    await loadProducts();
    logout();
});

function checkAuth() {
    const authLinks = document.getElementById('auth-links');
    const userLinks = document.getElementById('user-links');
    const userName = document.querySelector('.user-name');
    const adminLinks = document.getElementById('admin-links');

    if (Auth.isLoggedIn()) {
        authLinks.style.display = 'none';
        userLinks.style.display = 'flex';
        userName.textContent = Auth.getUser().name;

        if (Auth.isAdmin()) {
            adminLinks.classList.remove('hidden');
            adminLinks.style.display = 'flex';
            adminLinks.href = 'admin.html';
        }
    } else {
        authLinks.style.display = 'flex';
        userLinks.style.display = 'none';
    }
}

let currentPage = 0;
const pageSize = 20;
let currentCategoryId = null;

async function loadCategories() {
    // ?????
    try {
        const response = await api.get('/categories');
        if (response.status.statusCode == "OK") {
            const categories = response.data.result;
            const categoryList = document.getElementById('category-list');
            
            categoryList.innerHTML = '';
            
            const allLi = document.createElement('li');
            allLi.classList.add('links');
            const allLink = document.createElement('a');
            allLink.href = '#';
            allLink.id = 'all-products';
            allLink.textContent = 'All Products';
            if (currentCategoryId === null) allLink.classList.add('active');
            
            allLink.addEventListener('click', (e) => {
                e.preventDefault();
                currentCategoryId = null;
                currentPage = 0;
                loadProducts();
                document.querySelectorAll('.sidebar-categories a').forEach(a => a.classList.remove('active'));
                allLink.classList.add('active');
            });
            allLi.appendChild(allLink);
            categoryList.appendChild(allLi);

            categories.forEach(cat => {
                const li = document.createElement('li');
                li.classList.add('links');
                const link = document.createElement('a');
                link.textContent = cat.name;
                if (currentCategoryId === cat.id) link.classList.add('active');
                
                link.addEventListener('click', (e) => {
                    e.preventDefault();
                    currentCategoryId = cat.id;
                    currentPage = 0;
                    loadProducts();
                    
                    document.querySelectorAll('.sidebar-categories a').forEach(a => a.classList.remove('active'));
                    link.classList.add('active');
                });
                li.appendChild(link);
                categoryList.appendChild(li);
            });
        }
    } catch (error) {
        console.error('Error loading categories:', error);
    }
}

async function loadProducts() {
    const productGrid = document.querySelector('.product-grid');
    const prevBtn = document.getElementById('prev-page');
    const nextBtn = document.getElementById('next-page');

    if (prevBtn) prevBtn.disabled = true;
    if (nextBtn) nextBtn.disabled = true;

    let url = `/products?page=${currentPage}&size=${pageSize}`;
    if (currentCategoryId) {
        url = `/products/categorie/${currentCategoryId}?page=${currentPage}&size=${pageSize}`;
    }

    try {
        const response = await api.get(url);
        if (response.status.statusCode == "OK") {
            const products = response.data.result;
            const meta = response.data.meta;
            productGrid.innerHTML = '';

            products.forEach(product => {
                const mainAsset = product.assets && product.assets.length > 0 
                    ? product.assets[0].assest_url : 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQmVq-OmHL5H_5P8b1k306pFddOe3049-il2A&s';
                const productCard = document.createElement('div');
                productCard.classList.add('product-card');
                productCard.dataset.productId = product.id;
                productCard.innerHTML = `
                    <img class="product-image" src="${mainAsset}" alt="${product.name}">
                    <div class="product-info">
                        <h3 class="product-name">${product.name}</h3>
                        <p class="product-description">${product.description}</p>
                        <p class="product-price">${UI.formatCurrency(product.price)}</p>
                        <button class="btn btn-primary" onclick="addToCart(${product.id}, ${product.price})">Add to Cart</button>
                    </div>
                `;
                productGrid.appendChild(productCard);
            });
            updatePagination(meta);
        }
    } catch (error) {
        console.error('Error loading products:', error);
    }
}

function updatePagination(meta) {
    const prevBtn = document.getElementById('prev-page');
    const nextBtn = document.getElementById('next-page');
    const pageInfo = document.getElementById('page-info');

    currentPage = meta.page;

    pageInfo.textContent = `Page ${meta.page + 1} of ${meta.pages || 1}`;
    
    prevBtn.disabled = meta.page <= 0;
    nextBtn.disabled = meta.page >= meta.pages - 1 || meta.pages === 0;

    prevBtn.onclick = (e) => {
        e.preventDefault();
        if (currentPage > 0) {
            currentPage--;
            loadProducts();
        }
    };

    nextBtn.onclick = (e) => {
        e.preventDefault();
        if (currentPage < meta.pages - 1) {
            currentPage++;
            loadProducts();
        }
    };
}

async function addToCart(productId,priceAtTime) {
    if (!Auth.isLoggedIn()) {
        window.location.href = 'login.html';
        return;
    }

    try {
        const user = Auth.getUser();
        let response = await api.get(`/carts/active/user/${user.id}`)
        let cartId = response.data.id

        if (response.status.statusCode == "OK") {
            if (response.data.id == null) {
                // Create new cart
                response = await api.post('/carts', {
                    status: 'ACTIVE',
                    user: { id: user.id }
                })

                if (response.status.statusCode == "CREATED") {
                    cartId = response.data.id
                } else {
                    alert("Failed to create cart")
                }
            }
        }

        if (cartId) {
            const response = await api.post('/cart-items', {
                cart: { id: cartId },
                product: { id: productId },
                quantity: 1,
                price_at_time: priceAtTime
            })

            if (response.status.statusCode == "CREATED") {
                alert("Add to cart successfully")
            } else {
                alert("Failed to add to cart")
            }
        }
    } catch (error) {
        console.error('Failed to add to cart', error);
        alert("Failed to add to cart")
    }
    
    
}

function logout() {
    const logout = document.getElementById('logout');
    logout.addEventListener('click', async (e) => {
        e.preventDefault();
        Auth.logout();
        // window.location.href = '/front-end/login.html';
        window.location.reload();
    });
}