// Sample data for sub-services
const subServicesData = {
    all: [
        { name: "Service 1", description: "Description for Service 1" },
        { name: "Service 2", description: "Description for Service 2" },
        { name: "Service 3", description: "Description for Service 3" }
    ],
    category1: [
        { name: "Service 1", description: "Description for Service 1" },
        { name: "Service 2", description: "Description for Service 2" }
    ],
    category2: [
        { name: "Service 3", description: "Description for Service 3" }
    ]
};

// Function to display sub-services based on category
function showSubServices(category) {
    const subServicesList = document.getElementById('sub-services-list');
    subServicesList.innerHTML = ''; // Clear previous content

    const services = subServicesData[category] || [];

    services.forEach(service => {
        const serviceElement = document.createElement('div');
        serviceElement.className = 'sub-service';
        serviceElement.innerHTML = `
            <h3>${service.name}</h3>
            <p>${service.description}</p>
        `;
        subServicesList.appendChild(serviceElement);
    });
}

// Function to add a new category (only for admin)
function addCategory() {
    alert("Add new category functionality here.");
}

// Check if the user is an admin (you can replace this with your actual admin check logic)
const isAdmin = true; // Change this based on user role

if (isAdmin) {
    document.querySelector('.admin-only').style.display = 'flex';
}

// Initially display all services
showSubServices('all');

// Toggle mobile menu
const hamburger = document.getElementById('hamburger');
const navLinks = document.querySelector('.nav-links');

hamburger.addEventListener('click', () => {
    navLinks.classList.toggle('active');
});
function profile() {
    window.location.href = "../view/profile.html"; // Redirect to login page
}

// Add event listener to the logout link
document.querySelector('a[href="#logout"]').addEventListener('click', function(e) {
    e.preventDefault();
    profile();
});
