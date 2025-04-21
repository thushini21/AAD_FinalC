// Toggle mobile menu
const hamburger = document.getElementById('hamburger');
const navLinks = document.querySelector('.navbar .nav-links');

if (hamburger && navLinks) {
    hamburger.addEventListener('click', () => {
        navLinks.classList.toggle('active');
    });
}

// Form Validation and Login Function
function login() {
    const email = document.getElementById('email').value.trim();
    const password = document.getElementById('password').value.trim();

    // Basic validation
    if (!email || !password) {
        Swal.fire({
            icon: 'error',
            title: 'Validation Error',
            text: 'Please fill in all fields',
            confirmButtonColor: '#3085d6'
        });
        return;
    }

    // Email format validation
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
        Swal.fire({
            icon: 'error',
            title: 'Invalid Email',
            text: 'Please enter a valid email address',
            confirmButtonColor: '#3085d6'
        });
        return;
    }

    // Password length validation
    if (password.length < 6) {
        Swal.fire({
            icon: 'error',
            title: 'Weak Password',
            text: 'Password should be at least 6 characters long',
            confirmButtonColor: '#3085d6'
        });
        return;
    }

    // Show loading indicator
    Swal.fire({
        title: 'Logging in...',
        allowOutsideClick: false,
        didOpen: () => {
            Swal.showLoading();
        }
    });

    $.ajax({
        url: 'http://localhost:8080/api/v1/auth/authenticate',
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify({
            email: email,
            password: password
        }),
        success: function(response) {
            Swal.close();
            localStorage.setItem("token", response.data.token);

            const token = localStorage.getItem("token");

            if (token) {
                try {
                    const decodedToken = jwt_decode(token);
                    const role = decodedToken.role;

                    console.log("Decoded Token:", decodedToken);
                    console.log("User Role:", role);

                    Swal.fire({
                        icon: 'success',
                        title: 'Login Successful',
                        showConfirmButton: false,
                        timer: 1500
                    }).then(() => {
                        if (role === "SERVICE_PROVIDER") {
                            window.location.href = "../view/providerdashboard.html";
                        } else if (role === "CUSTOMER") {
                            window.location.href = "../view/index.html";
                        } else if (role === "ADMIN") {
                            window.location.href = "../view/admindashboard.html";
                        } else {
                            Swal.fire({
                                icon: 'warning',
                                title: 'Unknown Role',
                                text: 'You will be redirected to the home page',
                                confirmButtonColor: '#3085d6'
                            }).then(() => {
                                window.location.href = "../view/index.html";
                            });
                        }
                    });

                } catch (error) {
                    console.error("Failed to decode token:", error);
                    Swal.fire({
                        icon: 'error',
                        title: 'Token Error',
                        text: 'There was an issue processing your login',
                        confirmButtonColor: '#3085d6'
                    });
                }
            } else {
                Swal.fire({
                    icon: 'error',
                    title: 'Authentication Error',
                    text: 'No token received',
                    confirmButtonColor: '#3085d6'
                });
            }
        },
        error: function(xhr, status, error) {
            Swal.fire({
                icon: 'error',
                title: 'Login Failed',
                text: xhr.responseJSON?.message || 'Invalid credentials or server error',
                confirmButtonColor: '#3085d6'
            });
        }
    });
}

// Add event listener for form submission
document.addEventListener('DOMContentLoaded', function() {
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', function(e) {
            e.preventDefault();
            login();
        });
    }
});