$(document).ready(function() {
    // Get booking ID from URL
    const urlParams = new URLSearchParams(window.location.search);
    const bookingId = urlParams.get('id');

    if (!bookingId) {
        window.location.href = "/my-bookings";
        return;
    }

    // Load booking details
    loadBookingDetails(bookingId);

    // Status update handler
    $('#update-status-btn').click(function() {
        updateBookingStatus(bookingId);
    });
});

function loadBookingDetails(bookingId) {
    $.ajax({
        url: `http://localhost:8080/api/v1/bookings/${bookingId}`,
        method: 'GET',
        headers: {
            "Authorization": "Bearer " + localStorage.getItem("token")
        },
        success: function(response) {
            if (response.code === 200 && response.data) {
                const booking = response.data;
                displayBookingDetails(booking);
                loadProviderDetails(booking.service.serviceProviderId);
                setupActionButtons(booking);
            } else {
                throw new Error(response.message || "Failed to load booking details");
            }
        },
        error: function(xhr) {
            if (xhr.status === 401) {
                alert("Session expired. Please login again.");
                window.location.href = "/login";
            } else {
                alert("Failed to load booking details. Please try again.");
                window.location.href = "/my-bookings";
            }
        }
    });
}

function displayBookingDetails(booking) {
    // Basic booking info
    $('#booking-id').text(booking.bookingId);
    $('#booking-status').text(booking.status).addClass(getStatusBadgeClass(booking.status));
    $('#booking-date').text(new Date(booking.bookingDateTime).toLocaleDateString());
    $('#booking-time').text(new Date(booking.bookingDateTime).toLocaleTimeString());
    $('#hours-worked').text(booking.hoursWorked ? `${booking.hoursWorked} hours` : "Not specified");
    $('#created-at').text(new Date(booking.createdAt).toLocaleString());

    // Service info
    $('#service-name').text(booking.service.serviceName);
    $('#service-description').text(booking.service.description);
    $('#service-price').text(`Rs.${booking.service.fixedPrice.toFixed(2)}`);
    $('#service-duration').text(booking.service.duration ? `${booking.service.duration} mins` : "Flexible");

    // Payment info if exists
    if (booking.payment) {
        $('#payment-section').show();
        $('#payment-status').text(booking.payment.status).removeClass().addClass('badge ' + getPaymentStatusClass(booking.payment.status));
        $('#payment-amount').text(`Rs.${booking.payment.amount.toFixed(2)}`);
        $('#payment-date').text(new Date(booking.payment.paymentDate).toLocaleDateString());
    }
}

function loadProviderDetails(providerId) {
    $.ajax({
        url: `http://localhost:8080/api/v1/users/${providerId}`,
        method: 'GET',
        headers: {
            "Authorization": "Bearer " + localStorage.getItem("token")
        },
        success: function(response) {
            if (response.code === 200 && response.data) {
                const provider = response.data;
                $('#provider-name').text(provider.name);
                $('#provider-rating').html(`
                    ${generateStarRating(provider.rating)} (${provider.reviewCount} reviews)
                `);
                $('#provider-link').attr('href', `/providers/${provider.userId}`);

                if (provider.profileImage) {
                    $('#provider-image').attr('src', provider.profileImage);
                }
            }
        },
        error: function() {
            console.error("Failed to load provider details");
        }
    });
}

function setupActionButtons(booking) {
    const buttonsContainer = $('#action-buttons');
    buttonsContainer.empty();

    const userRole = getUserRoleFromToken(); // Implement this based on your JWT

    // Common buttons
    if (userRole === 'ADMIN') {
        buttonsContainer.append(`
            <button class="btn btn-outline-primary action-btn" data-bs-toggle="modal" data-bs-target="#statusModal">
                <i class="bi bi-pencil"></i> Change Status
            </button>
        `);
    }

    // Customer-specific buttons
    if (userRole === 'CUSTOMER' && booking.status === 'PENDING') {
        buttonsContainer.append(`
            <button class="btn btn-danger action-btn" onclick="cancelBooking(${booking.bookingId})">
                <i class="bi bi-x-circle"></i> Cancel Booking
            </button>
        `);
    }

    // Provider-specific buttons
    if (userRole === 'SERVICE_PROVIDER') {
        if (booking.status === 'PENDING') {
            buttonsContainer.append(`
                <button class="btn btn-success action-btn" onclick="updateBookingStatus(${booking.bookingId}, 'ACCEPTED')">
                    <i class="bi bi-check-circle"></i> Accept
                </button>
                <button class="btn btn-danger action-btn" onclick="updateBookingStatus(${booking.bookingId}, 'CANCELLED')">
                    <i class="bi bi-x-circle"></i> Reject
                </button>
            `);
        } else if (booking.status === 'ACCEPTED') {
            buttonsContainer.append(`
                <button class="btn btn-primary action-btn" onclick="updateBookingStatus(${booking.bookingId}, 'COMPLETED')">
                    <i class="bi bi-check-all"></i> Mark Completed
                </button>
                <button class="btn btn-warning action-btn" data-bs-toggle="modal" data-bs-target="#hoursModal">
                    <i class="bi bi-clock"></i> Update Hours
                </button>
            `);
        }
    }
}

function updateBookingStatus(bookingId, status = null) {
    const newStatus = status || $('#status-select').val();

    $.ajax({
        url: `http://localhost:8080/api/v1/bookings/${bookingId}/status`,
        method: 'PATCH',
        headers: {
            "Authorization": "Bearer " + localStorage.getItem("token"),
            "Content-Type": "application/json"
        },
        data: JSON.stringify({ status: newStatus }),
        success: function() {
            location.reload(); // Refresh to show updated status
        },
        error: function(xhr) {
            alert(xhr.responseJSON?.message || "Failed to update status");
        }
    });
}

function cancelBooking(bookingId) {
    if (confirm("Are you sure you want to cancel this booking?")) {
        updateBookingStatus(bookingId, 'CANCELLED');
    }
}

// Helper functions
function getStatusBadgeClass(status) {
    const classes = {
        'PENDING': 'bg-warning',
        'ACCEPTED': 'bg-primary',
        'COMPLETED': 'bg-success',
        'CANCELLED': 'bg-secondary'
    };
    return classes[status] || 'bg-light text-dark';
}

function getPaymentStatusClass(status) {
    const classes = {
        'PENDING': 'bg-warning',
        'COMPLETED': 'bg-success',
        'FAILED': 'bg-danger'
    };
    return classes[status] || 'bg-light text-dark';
}

function generateStarRating(rating) {
    const fullStars = Math.floor(rating);
    const halfStar = rating % 1 >= 0.5 ? 1 : 0;
    const emptyStars = 5 - fullStars - halfStar;

    return '★'.repeat(fullStars) +
        (halfStar ? '½' : '') +
        '☆'.repeat(emptyStars);
}

function getUserRoleFromToken() {
    const token = localStorage.getItem("token");
    if (!token) return null;

    try {
        const decoded = jwt_decode(token);
        return decoded.role; // Assuming your JWT has a 'role' claim
    } catch (e) {
        console.error("Error decoding token:", e);
        return null;
    }
}