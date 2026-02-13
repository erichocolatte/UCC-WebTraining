document.addEventListener('DOMContentLoaded', async () => {
    if (Auth.isLoggedIn()) {
        window.location.href = 'index.html'
    }

    document.body.style.display = "block"

    const loginForm = document.getElementById('login-form')
    loginForm.addEventListener('submit', async (e) => {
        e.preventDefault()

        const email = document.getElementById('email').value
        const password = document.getElementById('password').value
        try {
            const response = await api.post('/auth/login', { email, password })
            if (response.status.statusCode == "OK") {
                const { accessToken, user } = response.data
                Auth.saveToken(accessToken)
                Auth.saveUser(user)
                
                alert('Login successful!')
                window.location.href = '/front-end/index.html'
            } else {
                console.log(response)
                alert('Login failed!')
            }
        } catch (error) {
            console.error('Error logging in:', error)
        }
    })
})
