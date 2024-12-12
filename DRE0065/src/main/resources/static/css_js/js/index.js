function toggleInfo(){
    var infos = document.getElementsByClassName('additional-info');
    for (var i = 0; i < infos.length; i++) infos[i].style.display = infos[i].style.display === 'none' ? 'block' : 'none';
}

if(localStorage.getItem('justLoggedIn') === 'true'){
    localStorage.removeItem('justLoggedIn');
}
if(localStorage.getItem('justLoggedOut') === 'true'){
    localStorage.removeItem('justLoggedOut');
}

document.addEventListener('DOMContentLoaded', function(){
    const loginForm = document.getElementById('login-form');
    const loginError = document.getElementById('login-error');
    const loginButton = document.getElementById('login-button');
    const skipLoginButton = document.getElementById('skip-login-button');
    const logoutButton = document.getElementById('logout-button');
    const userIcon = document.getElementById('user-icon');
    const userRole = document.getElementById('user-role');
    const userSection = document.getElementById('user-section');

    function updateUI(user)
    {
        const loginButton = document.getElementById('login-button');
        const logoutButton = document.getElementById('logout-button');
        const userIcon = document.getElementById('user-icon');
        const userRoleSpan = document.getElementById('user-role');

        loginButton.style.display = 'none';
        logoutButton.style.display = 'inline-block';
        userIcon.style.display = 'inline-block';
        userRoleSpan.style.display = 'inline-block';

        if(user.role === 'admin') userRoleSpan.textContent = 'Admin';
        else if(user.role === 'fighter')
        {
            if(user.lastName) userRoleSpan.textContent = `Fighter: ${user.firstName} ${user.lastName}`;
            else userRoleSpan.textContent = `Fighter: ${user.firstName}`;
        }
        else if(user.role === 'coach')
        {
            if(user.lastName) userRoleSpan.textContent = `Coach: ${user.firstName} ${user.lastName}`;
            else userRoleSpan.textContent = `Coach: ${user.firstName}`;
        }
        else userRoleSpan.textContent = `${user.firstName} ${user.lastName}`;
        localStorage.setItem('user', JSON.stringify(user));
    }

    function resetUI()
    {
        loginButton.style.display = 'inline-block';
        logoutButton.style.display = 'none';
        userIcon.style.display = 'none';
        userRole.style.display = 'none';
        userRole.textContent = 'Basic User';
        localStorage.removeItem('user');
    }

    const storedUser = localStorage.getItem('user');
    if(storedUser)
    {
        const user = JSON.parse(storedUser);
        updateUI(user);
    }

    loginForm.addEventListener('submit', function(event)
    {
        event.preventDefault();
        const firstName = document.getElementById('loginfirstname').value.trim();
        const lastName = document.getElementById('loginlastname').value.trim();

        fetch('/api/login', {
            method: 'POST',
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            body: `firstName=${encodeURIComponent(firstName)}&lastName=${encodeURIComponent(lastName)}`
        })
            .then(response =>
            {
                if(!response.ok) throw new Error('Invalid credentials');
                return response.text();
            })
            .then(message =>
            {
                fetch('/api/user', {
                    method: 'GET',
                    credentials: 'include'
                })
                    .then(response =>
                    {
                        if(!response.ok) throw new Error('Failed to fetch user data');
                        return response.json();
                    })
                    .then(userData =>
                    {
                        const user = {
                            firstName: userData.firstName,
                            lastName: userData.lastName,
                            role: userData.role
                        };
                        updateUI(user);
                        $('#loginModal').modal('hide');
                        loginError.classList.add('d-none');
                        localStorage.setItem('justLoggedIn', 'true');
                        window.location.reload();
                    })
                    .catch(error =>
                    {
                        loginError.classList.remove('d-none');
                        loginError.textContent = error.message;
                    });
            })
            .catch(error =>
            {
                loginError.classList.remove('d-none');
                loginError.textContent = error.message;
            });
        loginForm.reset();
    });

    skipLoginButton.addEventListener('click', function(event)
    {
        event.preventDefault();
        const user = {
            firstName: 'Basic',
            lastName: 'User',
            role: 'basic'
        };
        updateUI(user);


        fetch('/api/basicLogin', {
            method: 'POST',
            credentials: 'include'
        })
            .then(response =>
            {
                if(!response.ok) throw new Error('Failed basic login');
                localStorage.setItem('justLoggedIn', 'true');
                $('#loginModal').modal('hide');
                loginError.classList.add('d-none');
                window.location.reload();
            })
            .catch(error =>
            {
                console.error(error);
                loginError.classList.remove('d-none');
                loginError.textContent = error.message;
            });
    });

    logoutButton.addEventListener('click', function(event)
    {
        event.preventDefault();
        fetch('/api/logout', {
            method: 'POST',
            credentials: 'include'
        })
            .then(response =>
            {
                if(!response.ok) throw new Error('Failed to logout');
                resetUI();
                localStorage.setItem('justLoggedOut', 'true');
                window.location.reload();
            })
            .catch(error => {console.error('Logout error:', error);});
    });
});