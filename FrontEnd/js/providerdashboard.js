// Toggle mobile menu
const hamburger = document.getElementById('hamburger');
const navLinks = document.querySelector('.nav-links');

hamburger.addEventListener('click', () => {
    navLinks.classList.toggle('active');
});
    function logout() {
    localStorage.removeItem("token"); // Clear the token
    window.location.href = "login.html"; // Redirect to login page
}

    // Add event listener to the logout link
    document.querySelector('a[href="#logout"]').addEventListener('click', function(e) {
    e.preventDefault();
    logout();
});
