// Mobile menu toggle
const hamburger = document.getElementById('hamburger');
const navLinks = document.querySelector('.nav-links');

hamburger.addEventListener('click', () => {
    navLinks.classList.toggle('active');
});

// Role Toggle and Modal Control
const roleToggle = document.getElementById('roleToggle');
const roleLabel = document.getElementById('roleLabel');
const providerLoginModal = document.getElementById('providerLoginModal');
const closeModal = document.querySelector('.close');
const providerLoginForm = document.getElementById('providerLoginForm');

// Toggle between customer and provider
roleToggle.addEventListener('change', function() {
    if (this.checked) {
        roleLabel.textContent = 'Provider';
        providerLoginModal.style.display = 'flex';
    } else {
        roleLabel.textContent = 'Customer';
        providerLoginModal.style.display = 'none';
    }
});

// Close modal handlers
closeModal.addEventListener('click', () => {
    providerLoginModal.style.display = 'none';
    roleToggle.checked = false;
    roleLabel.textContent = 'Customer';
});

window.addEventListener('click', (event) => {
    if (event.target === providerLoginModal) {
        providerLoginModal.style.display = 'none';
        roleToggle.checked = false;
        roleLabel.textContent = 'Customer';
    }
});

// Form submission handler
providerLoginForm.addEventListener('submit', function(e) {
    e.preventDefault();
    login();
});

// Login function
function login() {
    const email = document.getElementById('providerEmail').value;
    const password = document.getElementById('providerPassword').value;

    $.ajax({
        url: 'http://localhost:8080/api/v1/auth/authenticate',
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify({
            email: email,
            password: password
        }),
        success: function(response) {
            // Store token
            localStorage.setItem("token", response.data.token);

            // Close modal and reset toggle
            providerLoginModal.style.display = 'none';
            roleToggle.checked = false;
            roleLabel.textContent = 'Customer';

            // Decode token and redirect
            try {
                const decodedToken = jwt_decode(response.data.token);
                const role = decodedToken.role;

                switch(role) {
                    case "SERVICE_PROVIDER":
                        window.location.href = "../view/providerdashboard.html";
                        break;
                    case "CUSTOMER":
                        window.location.href = "../view/customerdashboard.html";
                        break;
                    case "ADMIN":
                        window.location.href = "../view/admindashboard.html";
                        break;
                    default:
                        alert("Unknown user role");
                }
            } catch (error) {
                console.error("Token decode error:", error);
                alert("Login error - please try again");
            }
        },
        error: function(xhr) {
            let errorMsg = "Login failed";
            if (xhr.responseJSON && xhr.responseJSON.message) {
                errorMsg += ": " + xhr.responseJSON.message;
            }
            alert(errorMsg);
        }
    });
}

// Contact Form Submission
document.getElementById('contactForm').addEventListener('submit', function(e) {
    e.preventDefault();
    const name = document.getElementById('name').value;
    const email = document.getElementById('email').value;
    const message = document.getElementById('message').value;

    if (!name || !email || !message) {
        alert('Please fill out all fields.');
        return;
    }

    alert('Message sent successfully!');
    this.reset();
});