document.addEventListener('DOMContentLoaded', async () => {
    checkAuth();

    document.body.style.display = "block";
    
    await register();
});

function checkAuth() {
    // console.log(Auth.isLoggedIn());
    if (Auth.isLoggedIn()) {
        window.location.href = 'index.html';
    }
}

async function register() {
    const registerForm = document.getElementById('register-form');
    registerForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const formData = new FormData(registerForm);
        const data = {
            name: formData.get('name'),
            email: formData.get('email'),
            password: formData.get('password'),
            gender: formData.get('gender')
        };
        try {
            const response = await api.post('/auth/register', data);
            if (response.status.statusCode == "OK") {
                window.location.href = 'login.html';
            }
        } catch (error) {
            console.error('Error registering:', error);
        }
    });
}