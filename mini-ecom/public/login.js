document.addEventListener('DOMContentLoaded', () => {
    if (Auth.isLoggedIn()) {
        window.location.href = 'index.html';
        return;
    }

    const loginForm = document.getElementById('loginForm');

    loginForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        const email = document.getElementById('email').value;
        const password = document.getElementById('password').value;

        try {
            const response = await api.post('/auth/login', { email, password });
            
            if (response.status && response.status.statusCode === 'OK') {
                const { accessToken, user } = response.data;
                Auth.saveToken(accessToken);
                Auth.saveUser(user);
                
                alert('Login successful!');
                window.location.href = 'index.html';
            } else {
                alert('Login failed: ' + (response.status ? response.status.message : 'Unknown error'));
            }
        } catch (error) {
            console.error(error);
            alert('An error occurred during login.');
        }
    });
});
