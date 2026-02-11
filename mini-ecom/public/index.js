document.addEventListener('DOMContentLoaded', async () => {
    // 1. Check Auth State
    updateUIForAuth();

    // 2. Load Categories
    await loadCategories();

    // 3. Load Products
    await loadProducts();

    // 4. Setup Event Listeners
    // setupPagination(); // Removed as it is undefined and logic is in updatePagination called by loadProducts
    const logoutBtn = document.getElementById('logoutBtn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', (e) => {
            e.preventDefault();
            console.log('Logout clicked');
            Auth.logout();
        });
    } else {
        console.error('Logout button not found');
    }
});

let currentPage = 0;
const pageSize = 5;
let currentCategoryId = null;

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

async function loadCategories() {
    try {
        const response = await api.get('/categories');
        if (response.status && response.status.statusCode === 'OK') {
            const categories = response.data.result;
            const categoryList = document.getElementById('categoryList');
            
            // Clear list but keep "All Products"
            categoryList.innerHTML = '<li><a href="#" onclick="loadProducts()" ' + (currentCategoryId === null ? 'class="active"' : '') + '>All Products</a></li>';
            
            categories.forEach(cat => {
                const li = document.createElement('li');
                const link = document.createElement('a');
                link.href = '#';
                link.textContent = cat.name;
                if (currentCategoryId === cat.id) link.classList.add('active');
                
                link.addEventListener('click', (e) => {
                    e.preventDefault();
                    currentCategoryId = cat.id;
                    currentPage = 0;
                    loadProducts();
                    
                    // Update active state
                    document.querySelectorAll('.sidebar-categories a').forEach(a => a.classList.remove('active'));
                    link.classList.add('active');
                });
                li.appendChild(link);
                categoryList.appendChild(li);
            });
        }
    } catch (error) {
        console.error('Failed to load categories', error);
    }
}

async function loadProducts() {
    const grid = document.getElementById('productGrid');
    grid.innerHTML = '<p>Loading products...</p>';

    try {
        let url = `/products?page=${currentPage}&size=${pageSize}`;
        if (currentCategoryId) {
            url = `/products/categorie/${currentCategoryId}?page=${currentPage}&size=${pageSize}`;
        }

        const response = await api.get(url);
        
        if (response.status && response.status.statusCode === 'OK') {
            const products = response.data.result;
            const meta = response.data.meta;
            
            grid.innerHTML = '';
            
            if (products.length === 0) {
                grid.innerHTML = '<p>No products found.</p>';
                return;
            }

            products.forEach(product => {
                const card = document.createElement('div');
                card.className = 'card';
                card.innerHTML = `
                    <div class="card-img">
                        <img src="https://via.placeholder.com/300x200?text=${encodeURIComponent(product.name)}" alt="${product.name}">
                    </div>
                    <div class="card-body">
                        <h3 class="card-title">${product.name}</h3>
                        <div class="card-price">${UI.formatCurrency(product.price)}</div>
                        <a href="product.html?id=${product.id}" class="btn btn-primary btn-block" style="text-align: center;">View Details</a>
                    </div>
                `;
                grid.appendChild(card);
            });

            updatePagination(meta);
        }
    } catch (error) {
        console.error('Failed to load products', error);
        grid.innerHTML = '<p style="color: red">Error loading products. Please try again.</p>';
    }
}

function updatePagination(meta) {
    const prevBtn = document.getElementById('prevPageBtn');
    const nextBtn = document.getElementById('nextPageBtn');
    const pageInfo = document.getElementById('pageInfo');

    pageInfo.textContent = `Page ${meta.page + 1} of ${meta.pages}`;
    
    prevBtn.disabled = meta.page === 0;
    nextBtn.disabled = meta.page >= meta.pages - 1;

    prevBtn.onclick = () => {
        if (currentPage > 0) {
            currentPage--;
            loadProducts();
        }
    };

    nextBtn.onclick = () => {
        if (currentPage < meta.pages - 1) {
            currentPage++;
            loadProducts();
        }
    };
}

// Enhance loadProducts to update sidebar active state purely if needed (optional, effectively done in click handlers)
