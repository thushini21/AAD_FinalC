function goBack() {
    window.history.back(); // Go back to the previous page
}
function register() {
    const name = document.getElementById('fullName').value;
    const email = document.getElementById('email').value;
    const phone = document.getElementById('phone').value;
    const address = document.getElementById('address').value;
    const password = document.getElementById('password').value;
    const confirmPassword = document.getElementById('confirm-password').value;

    if (password !== confirmPassword) {
        document.getElementById('confirmPasswordError').style.display = 'block';
        document.getElementById('confirm-password').style.borderColor = 'red';
    } else {
        document.getElementById('confirmPasswordError').style.display = 'none';
        document.getElementById('confirm-password').style.borderColor = '#ccc';
    }

    const userData = {
        userId: 0,
        email: email,
        name: name,
        password: password,
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
            console.log(response);
            console.log(response.data.email);
            console.log(response.data.token);
            localStorage.removeItem("token");
            alert("Registration successful!...........");
        },

        error: function(xhr, status, error) {
            console.error('Error:', error);
            alert("Registration failed! Please try again.");
        }
    });
}