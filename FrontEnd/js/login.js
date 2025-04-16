
// Toggle mobile menu
const hamburger = document.getElementById('hamburger');
const navLinks = document.querySelector('.navbar .nav-links');

hamburger.addEventListener('click', () => {
    navLinks.classList.toggle('active');
});

// Form Validation
function login() {
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;

    $.ajax({
        url: 'http://localhost:8080/api/v1/auth/authenticate',
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify({
            email: email,
            password: password
        }),
        success: function (response) {
            alert("Login successful");
            localStorage.setItem("token", response.data.token);
            console.log(response.data.token)
            // Assuming the token is stored in localStorage
            const token = localStorage.getItem("token");

            if (token) {
                try {
                    // Decode the token
                    const decodedToken = jwt_decode(token);

                    // Extract the role from the token payload
                    const role = decodedToken.role; // Assuming the role is stored in the "role" claim

                    console.log("Decoded Token:", decodedToken);
                    console.log("User Role:", role);

                    // Perform actions based on the role
                    if (role === "SERVICE_PROVIDER") {
                        console.log("User is a Service Provider");
                        window.location.href = "../view/providerdashboard.html";
                        // Redirect or show provider-specific content
                    } else if (role === "CUSTOMER") {
                        console.log("User is a Customer");
                        setTimeout(function() {
                            window.location.href = "../view/index.html"; // Replace with your login page URL
                        }, 500);
                        // Redirect or show customer-specific content
                    }  else if (role === "ADMIN") {
                        console.log("User is a Customer");
                        window.location.href = "../view/admindashboard.html";
                        // Redirect or show customer-specific content
                    } else {
                        console.log("Unknown role");
                    }
                } catch (error) {
                    console.error("Failed to decode token:", error);
                }
            } else {
                console.error("No token found in localStorage");
            }

        },

        error: function (xhr, status, error) {
            alert("Login failed")
        }
    });
}
