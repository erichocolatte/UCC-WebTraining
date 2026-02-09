document.addEventListener('DOMContentLoaded', () => {
    if (Auth.isLoggedIn()) {
        window.location.href = 'index.html';
        return;
    }

    const registerForm = document.getElementById('registerForm');

    registerForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        const name = document.getElementById('name').value;
        const email = document.getElementById('email').value;
        const password = document.getElementById('password').value;
        const role = document.getElementById('role').value;
        const gender = document.getElementById('gender').value;

        const data = {
            name,
            email,
            password,
            role,
            gender
        };

        try {
            const response = await api.post('/auth/register', data);
            
            if (response.status && response.status.statusCode === 'CREATED') {
                alert('Registration successful! Please login.');
                window.location.href = 'login.html';
            } else {
                alert('Registration failed: ' + (response.status ? response.status.message : 'Unknown error'));
            }
        } catch (error) {
            console.error(error);
            alert('An error occurred during registration.');
        }
    });
});
