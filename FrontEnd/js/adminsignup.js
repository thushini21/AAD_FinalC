function goBack() {
    window.history.back(); // Go back to the previous page
}

function adminRegister() {
    const name = document.getElementById('fullName').value;
    const email = document.getElementById('email').value;
    const phone = document.getElementById('phone').value;
    const address = document.getElementById('address').value;
    const password = document.getElementById('password').value;
    const confirmPassword = document.getElementById('confirm-password').value;

    // Validate password match
    if (password !== confirmPassword) {
        document.getElementById('confirmPasswordError').style.display = 'block';
        document.getElementById('confirm-password').style.borderColor = 'red';
        return;
    } else {
        document.getElementById('confirmPasswordError').style.display = 'none';
        document.getElementById('confirm-password').style.borderColor = '#ccc';
    }

    const userData = {
        userId: 0,
        email: email,
        name: name,
        password: password,
        role: 'ADMIN',
        phoneNumber: phone,
        address: address
    };

    $.ajax({
        method: 'POST',
        contentType: 'application/json',
        url: 'http://localhost:8080/api/v1/users/register',
        async: true,
        data: JSON.stringify(userData),

        success: function(response) {
            console.log('✅ Registration Response:', response);
            alert("Admin registration successful!");
            localStorage.removeItem("token");

            // Redirect to admin login after 2 seconds
            setTimeout(function() {
                window.location.href = "../view/adminLogin.html";
            }, 2000);
        },

        error: function(xhr, status, error) {
            console.error('❌ Error:', error);
            alert("Admin registration failed! Please try again.");
        }
    });
}
