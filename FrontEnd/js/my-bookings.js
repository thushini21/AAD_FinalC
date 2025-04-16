/*
$(document).ready(function() {
    // Check authentication
    const token = localStorage.getItem("token");
    if (!token) {
        window.location.href = "/login";
        return;
    }

    // Load bookings on page load
    loadBookings();

    // Set up event listeners
    $('#apply-filters').click(loadBookings);
    $('#reset-filters').click(resetFilters);
    $('#refresh-btn').click(loadBookings);

  /!*  // Initialize date pickers
    const today = new Date().toISOString().split('T')[0];
    $('#date-to').val(today);*!/
});

async function getUserIdFromEmail(userEmail) {
    return new Promise((resolve, reject) => {
        console.log("inside function");
        $.ajax({
            url: 'http://localhost:8080/api/v1/users/getidbyemail',
            type: 'GET',
            data: { email: userEmail },
            success: function(response) {
                if (response.code === 200 && response.data.data) {
                    console.log(response.data.data);
                    resolve(response.data.data);
                } else {
                    console.log(response);
                    reject(new Error(response.message || "Failed to get user ID"));
                }
            },
            error: function(xhr) {
                reject(new Error(xhr.responseJSON?.message || "Failed to fetch user ID"));
            }
        });
    });
}
let userRole;

function navigateHome() {
        if (userRole === 'CUSTOMER') {
            window.location.href = '../view/index.html';
        } else if (userRole === 'SERVICE_PROVIDER') {
            window.location.href = '../view/providerdashboard.html';
        } else {
            // Default fallback if role isn't recognized
            window.location.href = 'index.html';
        }
}
async function loadBookings() {
    showLoadingState();

    const status = $('#status-filter').val();
    const dateFrom = $('#date-from').val();
    const dateTo = $('#date-to').val();

    // Determine endpoint based on user role
    userRole = getUserRoleFromToken();
    const userEmail = getUserEmailFromToken();
    console.log(userEmail);
    const userId = await getUserIdFromEmail(userEmail);
    console.log(userId);
    if (!userId) {
        throw new Error("Failed to get user ID");
    }
    const endpoint = userRole === 'SERVICE_PROVIDER'
        ? `http://localhost:8080/api/v1/bookings/provider/${userId}`
        : `http://localhost:8080/api/v1/bookings/customer/${userId}`;

    const params = {
        fromDate: dateFrom || null,
        toDate: dateTo || null
    };

// Only add status to params if it's not 'ALL'
    if (status !== 'ALL') {
        params.status = status;
    }

    $.ajax({
        url: endpoint,
        method: 'GET',
        headers: {
            "Authorization": "Bearer " + localStorage.getItem("token")
        },
        data: params,
        success: function(response) {
            if (response.code === 200 && response.data.data && response.data.data.length > 0) {
                console.log(response.data.data);
                displayBookings(response.data.data);
            } else {
                showEmptyState();
            }
        },
        error: function(xhr) {
            if (xhr.status === 401) {
                alert("Session expired. Please login again.");
                window.location.href = "/login";
            } else {
                alert("Failed to load bookings. Please try again.");
                showEmptyState();
            }
        }
    });
}

async function displayBookings(bookings) {
    const container = $('#bookings-container');
    container.empty();
    const userRole = getUserRoleFromToken();

    for (const booking of bookings) {
        try {
            const bookingDate = new Date(booking.bookingDateTime);
            const formattedDate = bookingDate.toLocaleDateString();
            const formattedTime = bookingDate.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
            const service = await getServiceFromServiceId(booking.serviceId);

            // Calculate payment amount
            const paymentAmount = calculatePaymentAmount(booking, service);

            container.append(`
                <div class="booking-card card mb-3">
                    <div class="card-body">
                        <div class="d-flex justify-content-between align-items-start mb-2">
                            <div>
                                <h5 class="card-title mb-1">${service.serviceName}</h5>
                                <p class="text-muted small mb-2">Booking #${booking.bookingId}</p>
                            </div>
                            <span class="status-badge badge ${getStatusBadgeClass(booking.status)}">
                                ${booking.status}
                            </span>
                        </div>
                        
                        <div class="row mb-3">
                            <div class="col-md-6">
                                <div class="d-flex align-items-center mb-2">
                                    <i class="bi bi-calendar-date me-2"></i>
                                    <span>${formattedDate} at ${formattedTime}</span>
                                </div>
                                <div class="d-flex align-items-center">
                                    <i class="bi bi-person me-2"></i>
                                    <span>${userRole === 'SERVICE_PROVIDER' ? 'Customer' : 'Provider'}: ${userRole === 'SERVICE_PROVIDER' ? booking.customerName || 'Customer' : service.providerName || 'Provider'}</span>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="d-flex align-items-center mb-2">
                                    <i class="bi bi-cash-coin me-2"></i>
                                    <span>Fixed Price: Rs.${service.fixedPrice.toFixed(2)}</span>
                                </div>
                                <div class="d-flex align-items-center mb-2">
                                    <i class="bi bi-clock me-2"></i>
                                    <span>Hourly Rate: Rs.${service.hourlyRate.toFixed(2)}</span>
                                </div>
                                <div class="d-flex align-items-center mb-2">
                                    <i class="bi bi-clock-history me-2"></i>
                                    <span>${booking.hoursWorked ? booking.hoursWorked + ' hours' : 'Duration not set'}</span>
                                </div>
                                ${paymentAmount ? `
                                <div class="d-flex align-items-center mb-2">
                                    <i class="bi bi-cash-coin me-2" style="color: green; font-weight: bold"></i>
                                    <span style="color: green; font-weight: bold">Total Amount: Rs.${paymentAmount.toFixed(2)}</span>
                                </div>
                                ` : ''}
                            </div>
                        </div>
                        
                        <div class="d-flex justify-content-between align-items-center">
                            <a href="/booking-details.html?id=${booking.bookingId}" class="btn btn-sm btn-outline-primary">
                                <i class="bi bi-eye"></i> View Details
                            </a>
                            ${await generateActionButtons(booking, service)}
                        </div>
                    </div>
                </div>
            `);
        } catch (error) {
            console.error(`Error displaying booking ${booking.bookingId}:`, error);
            container.append(`
                <div class="alert alert-danger mb-3">
                    Error loading booking #${booking.bookingId}
                </div>
            `);
        }
    }
}

function calculatePaymentAmount(booking, service) {
    if (booking.status === 'COMPLETED') {
        // Calculate based on hours worked if available, otherwise use fixed price
        return booking.hoursWorked
            ? booking.hoursWorked * service.hourlyRate + service.fixedPrice
            : service.fixedPrice;
    }
    return null;
}
function calculatepayPaymentAmount(booking, service) {
    if (booking.status === 'COMPLETED') {
        // Calculate based on hours worked if available, otherwise use fixed price
        return booking.hoursWorked
            ? booking.hoursWorked * service.hourlyRate
            : service.fixedPrice;
    }
    return null;
}

function getPaymentFromBookingId(bookingId) {
    return new Promise((resolve, reject) => {
        $.ajax({
            url: `http://localhost:8080/api/v1/payments/get-by-booking/${bookingId}`, // Your API endpoint
            type: 'GET',
            headers: {
                "Authorization": "Bearer " + localStorage.getItem("token")
            },
            dataType: 'json',
            success: function(response) {
                console.log(response.data.data)
                    resolve(response.data.data)
            },
            error: function(xhr) {
                let errorMsg = xhr.responseJSON?.message || "Failed to fetch payment";
                reject(new Error(errorMsg));
            }
        });
    });
}
let booking1;
let payment;
async function generateActionButtons(booking, service) {
    booking1 = booking;
    const userRole = getUserRoleFromToken();
    payment = await getPaymentFromBookingId(booking.bookingId);
    let buttons = '';

    if (userRole === 'CUSTOMER') {
        if (booking.status === 'PENDING') {
            buttons = `
                <button class="btn btn-sm btn-danger" onclick="cancelBooking(${booking.bookingId})">
                    <i class="bi bi-x-circle"></i> Cancel
                </button>
            `;
        } else if (booking.status === 'ACCEPTED') {
            if(payment == null) {
                buttons = `
                <button class="btn btn-sm btn-success" onclick="showPaymentModal(${booking.bookingId}, ${service.fixedPrice})">
                    <i class="bi bi-credit-card"></i> Pay Rs.${service.fixedPrice.toFixed(2)}
                </button>
            `;
            }
        }else if (booking.status === 'COMPLETED') {
            if(payment.status === 'DEPOSIT') {
                buttons = `
                <button class="btn btn-sm btn-success" onclick="showPaymentModal(${booking.bookingId}, ${calculatePaymentAmount(booking, service)}-${service.fixedPrice})">
                    <i class="bi bi-credit-card"></i> Pay Rs.${calculatepayPaymentAmount(booking, service).toFixed(2)}
                </button>
            `;
            }
        }
    } else if (userRole === 'SERVICE_PROVIDER') {
        if (booking.status === 'PENDING') {
            buttons = `
                <div class="btn-group">
                    <button class="btn btn-sm btn-success" onclick="updateBookingStatus(${booking.bookingId}, 'ACCEPTED')">
                        <i class="bi bi-check-circle"></i> Accept
                    </button>
                    <button class="btn btn-sm btn-danger" onclick="updateBookingStatus(${booking.bookingId}, 'REJECTED')">
                        <i class="bi bi-x-circle"></i> Reject
                    </button>
                </div>
            `;
        } else if (booking.status === 'ACCEPTED') {
            buttons = `
                <div class="btn-group">
                    <button class="btn btn-sm btn-primary" onclick="showDurationModal(${booking.bookingId})">
                        <i class="bi bi-check-all"></i> Complete
                    </button>
                </div>
                ${generateDurationModal(booking.bookingId)}
            `;
        }
    }

    return buttons;
}



/!*function generateActionButtons(booking) {
    const userRole = getUserRoleFromToken();
    let buttons = '';

    if (userRole === 'CUSTOMER') {
        if (booking.status === 'PENDING') {
            buttons = `
                <button class="btn btn-sm btn-danger" onclick="cancelBooking(${booking.bookingId})">
                    <i class="bi bi-x-circle"></i> Cancel
                </button>
            `;
        } else if (booking.status === 'ACCEPTED') {
            buttons = `
                <div class="btn-group">
                    <button class="btn btn-sm btn-success" onclick="initiatePayment(${booking.bookingId})">
                        <i class="bi bi-credit-card"></i> Pay Now ($${booking.servicePrice})
                    </button>
                    <button class="btn btn-sm btn-danger" onclick="cancelBooking(${booking.bookingId})">
                        <i class="bi bi-x-circle"></i> Cancel
                    </button>
                </div>
            `;
        }
    } else if (userRole === 'SERVICE_PROVIDER') {
        if (booking.status === 'PENDING') {
            buttons = `
                <div class="btn-group">
                    <button class="btn btn-sm btn-success" onclick="updateBookingStatus(${booking.bookingId}, 'ACCEPTED')">
                        <i class="bi bi-check-circle"></i> Accept
                    </button>
                    <button class="btn btn-sm btn-danger" onclick="updateBookingStatus(${booking.bookingId}, 'CANCELLED')">
                        <i class="bi bi-x-circle"></i> Reject
                    </button>
                </div>
            `;
        } else if (booking.status === 'ACCEPTED') {
            buttons = `
                <div class="btn-group">
                    <button class="btn btn-sm btn-primary" onclick="showDurationModal(${booking.bookingId})">
                        <i class="bi bi-check-all"></i> Complete
                    </button>
                </div>
                ${generateDurationModal(booking.bookingId)}
            `;
        }
    }

    return buttons;
}*!/

// Function to generate modal HTML
function generateDurationModal(bookingId) {
    return `
    <div class="modal fade" id="durationModal-${bookingId}" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Complete Booking</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <div class="mb-3">
                        <label class="form-label">Duration (hours)</label>
                        <input type="number" id="durationInput-${bookingId}" 
                               class="form-control" min="0.5" max="24" step="0.5" value="1">
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="button" class="btn btn-primary" 
                            onclick="updateBookingStatusWithDuration(${bookingId}, 'COMPLETED')">
                        Submit
                    </button>
                </div>
            </div>
        </div>
    </div>
    `;
}
let currentBookingId = null;

// Show duration modal for completion
function showDurationModal(bookingId) {
    currentBookingId = bookingId;
    $('#durationInput').val('1'); // Reset to default value
    $('#durationModal').modal('show');
}

// Handle completion confirmation
$('#confirmCompletion').click(function() {
    const duration = parseFloat($('#durationInput').val());

    if (!duration || duration <= 0) {
        alert('Please enter a valid duration (minimum 0.5 hours)');
        return;
    }

    updateBookingStatusWithDuration(currentBookingId, 'COMPLETED', duration);
});

// Update booking status with duration
function updateBookingStatusWithDuration(bookingId, status, duration) {
    $.ajax({
        url: `http://localhost:8080/api/v1/bookings/${bookingId}/status/duration`,
        method: 'PATCH',
        headers: {
            "Authorization": "Bearer " + localStorage.getItem("token")
        },
        data:{
            status: status,
            duration: duration
        },
        success: function() {
            $('#durationModal').modal('hide');
            loadBookings(); // Refresh the list
        },
        error: function(xhr) {
            alert(xhr.responseJSON?.message || "Failed to complete booking");
        }
    });
}

// Payment Modal Handler
// Update your showPaymentModal function
function showPaymentModal(bookingId, amount) {
    document.getElementById('paymentBookingId').value = bookingId;
    document.getElementById('paymentBookingRef').textContent = bookingId;
    document.getElementById('paymentAmount').textContent = amount.toFixed(2);
    new bootstrap.Modal(document.getElementById('paymentModal')).show();
}

// Replace your processPayment function
function processPayment() {
    const bookingId = $('#paymentBookingId').val();
    const paymentMethod = $('#paymentMethod').val();
    const amount = parseFloat($('#paymentAmount').text());

        initiatePayHerePayment(bookingId, amount);
}


// New PayHere payment function
function initiatePayHerePayment(bookingId, amount) {
    $('#paymentModal').hide();
    // Validate inputs
    if (!bookingId || isNaN(amount) || amount <= 0) {
        alert("Invalid booking ID or amount");
        return;
    }

    // Payment configuration
    const payment1 = {
        "sandbox": true,
        "merchant_id": "1229927",
        "return_url": window.location.origin + "/payment/success",
        "cancel_url": window.location.origin + "/payment/cancel",
        "notify_url": window.location.origin + "/payment/notify",
        "order_id": "BOOKING_" + bookingId + "_" + Date.now(),
        "items": "Service Booking #" + bookingId,
        "amount": "1000.00",
        "currency": "LKR",
        "first_name": "Customer",
        "last_name": "Name",
        "email": "customer@example.com",
        "phone": "0771234567",
        "address": "No.1, Street Name",
        "city": "Colombo",
        "country": "Sri Lanka",
        // "custom_1": bookingId
    };


    try {
        console.log("Before payhere.startPayment");
    // Start PayHere payment
    payhere.startPayment(
        payment1,
        function(response) {
            console.log("Payment completed:", response);

            // Verify response contains required fields
            if (!response || !response.payment_id) {
                console.error("Invalid PayHere response:", response);
                alert("Payment verification failed. Please contact support.");
                return;
            }

           console.log("erhhdh")
        },
        function(error) {
            console.error("Payment failed:", error);
            showErrorMessage("Payment failed: " +
                (error.message || "Please try again or contact support"));
        }
    );

        if (typeof payhere.close === 'function') {
            payhere.close(); // Close the PayHere checkout popup
        }
    } catch (e) {
        console.error("Payment initialization failed:", e);
    }

    console.log("efedgtege")
    if (booking1.status === 'ACCEPTED'){
    // AJAX call to save payment
    const paymentsDTO = {
        bookingId: bookingId,
        depositAmount: amount,
        finalAmount: 0.0,
        status: "DEPOSIT",
        paymentDate: new Date().toISOString()
    };
    $.ajax({
        url: 'http://localhost:8080/api/v1/payments/add',
        type: 'POST',
        contentType: 'application/json', // Explicitly set content type to JSON
        data: JSON.stringify(paymentsDTO), // Stringify the object
        headers: {
            "Authorization": "Bearer " + localStorage.getItem("token")
        },
        dataType: 'json',
        success: function(response) {
            loadBookings();
            if (response.success) {

                window.location.href = "../view/mybookings.html"; // Your cancellation URL
            } else {
                $('#payment-error').text(response.message || 'Payment failed').show();
                $('#save-payment-btn').prop('disabled', false).text('Save Payment');
            }
        },
        error: function(xhr) {
            let errorMessage = 'Error saving payment';
            if (xhr.responseJSON && xhr.responseJSON.message) {
                errorMessage = xhr.responseJSON.message;
            }
            $('#payment-error').text(errorMessage).show();
            $('#save-payment-btn').prop('disabled', false).text('Save Payment');
        }
    });
    }else if (booking1.status === 'COMPLETED'){
        console.log("+++++++++++++++++++++++++++++++++++++++",payment.paymentId)
        let paymentId =payment.paymentId;

        const paymentData = {
            finalAmount: amount,
            status: "FULL_PAYMENT",
            paymentDate: new Date().toISOString()
        };

        $.ajax({
            url: `http://localhost:8080/api/v1/payments/${paymentId}`,
            method: 'PATCH',
            headers: {
                "Authorization": "Bearer " + localStorage.getItem("token"),
                "Content-Type": "application/json"
            },
            data: JSON.stringify(paymentData),
            success: function(response) {
                if (response.code === 200) {
                    loadBookings(); // Refresh the list
                    alert('Payment updated successfully!');
                } else {
                    alert('Error: ' + response.message);
                }
            },
            error: function(xhr) {
                let errorMsg = xhr.responseJSON?.message ||
                    `Error: ${xhr.status} - ${xhr.statusText}`;
                alert('Failed to update payment: ' + errorMsg);
            }
        });
    }
}*/


$(document).ready(function() {
    // Check authentication
    const token = localStorage.getItem("token");
    if (!token) {
        window.location.href = "/login";
        return;
    }

    // Load bookings on page load
    loadBookings();

    // Set up event listeners
    $('#apply-filters').click(loadBookings);
    $('#reset-filters').click(resetFilters);
    $('#refresh-btn').click(loadBookings);
});

async function getUserIdFromEmail(userEmail) {
    return new Promise((resolve, reject) => {
        $.ajax({
            url: 'http://localhost:8080/api/v1/users/getidbyemail',
            type: 'GET',
            data: { email: userEmail },
            success: function(response) {
                if (response.code === 200 && response.data.data) {
                    resolve(response.data.data);
                } else {
                    reject(new Error(response.message || "Failed to get user ID"));
                }
            },
            error: function(xhr) {
                reject(new Error(xhr.responseJSON?.message || "Failed to fetch user ID"));
            }
        });
    });
}

let userRole;

function navigateHome() {
    if (userRole === 'CUSTOMER') {
        window.location.href = '../view/index.html';
    } else if (userRole === 'SERVICE_PROVIDER') {
        window.location.href = '../view/providerdashboard.html';
    } else {
        window.location.href = 'index.html';
    }
}

async function loadBookings() {
    showLoadingState();

    const status = $('#status-filter').val();
    const dateFrom = $('#date-from').val();
    const dateTo = $('#date-to').val();

    userRole = getUserRoleFromToken();
    const userEmail = getUserEmailFromToken();
    const userId = await getUserIdFromEmail(userEmail);

    if (!userId) {
        throw new Error("Failed to get user ID");
    }

    const endpoint = userRole === 'SERVICE_PROVIDER'
        ? `http://localhost:8080/api/v1/bookings/provider/${userId}`
        : `http://localhost:8080/api/v1/bookings/customer/${userId}`;

    const params = {
        fromDate: dateFrom || null,
        toDate: dateTo || null
    };

    if (status !== 'ALL') {
        params.status = status;
    }

    $.ajax({
        url: endpoint,
        method: 'GET',
        headers: {
            "Authorization": "Bearer " + localStorage.getItem("token")
        },
        data: params,
        success: function(response) {
            if (response.code === 200 && response.data.data && response.data.data.length > 0) {
                displayBookings(response.data.data);
                console.log(response.data.data)
            } else {
                showEmptyState();
            }
        },
        error: function(xhr) {
            if (xhr.status === 401) {
                alert("Session expired. Please login again.");
                window.location.href = "/login";
            } else {
                alert("Failed to load bookings. Please try again.");
                showEmptyState();
            }
        }
    });
}

function getServiceFromServiceId(serviceId) {
    return new Promise((resolve, reject) => {
        $.ajax({
            url: `http://localhost:8080/api/v1/services/${serviceId}`,
            method: 'GET',
            success: function(response) {
                if (response.code === 200 && response.data.data) {
                    resolve(response.data.data);
                } else {
                    reject(new Error(response.msg || "Service not found"));
                }
            },
            error: function(xhr) {
                if (xhr.status === 401) {
                    alert("Session expired. Please login again.");
                    window.location.href = "/login";
                }
                reject(new Error(xhr.responseJSON?.msg || "Failed to fetch service"));
            }
        });
    });
}



async function displayBookings(bookings) {
    const container = $('#bookings-container');
    container.empty();

    for (const booking of bookings) {
        try {
            const bookingDate = new Date(booking.bookingDateTime);
            const formattedDate = bookingDate.toLocaleDateString();
            const formattedTime = bookingDate.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
            const service = await getServiceFromServiceId(booking.serviceId);
            const paymentAmount = calculatePaymentAmount(booking, service);

            container.append(`
                <div class="booking-card card mb-3" data-booking-id="${booking.bookingId}">
                    <div class="card-body">
                        <div class="d-flex justify-content-between align-items-start mb-2">
                            <div>
                                <h5 class="card-title mb-1">${service.serviceName}</h5>
                                <p class="text-muted small mb-2">Booking #${booking.bookingId}</p>
                            </div>
                            <span class="status-badge badge ${getStatusBadgeClass(booking.status)}">
                                ${booking.status}
                            </span>
                        </div>
                        
                        <div class="row mb-3">
                            <div class="col-md-6">
                                <div class="d-flex align-items-center mb-2">
                                    <i class="bi bi-calendar-date me-2"></i>
                                    <span>${formattedDate} at ${formattedTime}</span>
                                </div>
                                <div class="d-flex align-items-center">
                                    <i class="bi bi-person me-2"></i>
                                    <span>${userRole === 'SERVICE_PROVIDER' ? 'Customer' : 'Provider'}: ${userRole === 'SERVICE_PROVIDER' ? booking.customerName || 'Customer' : service.providerName || 'Provider'}</span>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="d-flex align-items-center mb-2">
                                    <i class="bi bi-cash-coin me-2"></i>
                                    <span>Fixed Price: Rs.${service.fixedPrice.toFixed(2)}</span>
                                </div>
                                <div class="d-flex align-items-center mb-2">
                                    <i class="bi bi-clock me-2"></i>
                                    <span>Hourly Rate: Rs.${service.hourlyRate.toFixed(2)}</span>
                                </div>
                                <div class="d-flex align-items-center mb-2">
                                    <i class="bi bi-clock-history me-2"></i>
                                    <span>${booking.hoursWorked ? booking.hoursWorked + ' hours' : 'Duration not set'}</span>
                                </div>
                                ${paymentAmount ? `
                                <div class="d-flex align-items-center mb-2">
                                    <i class="bi bi-cash-coin me-2" style="color: green; font-weight: bold"></i>
                                    <span style="color: green; font-weight: bold">Total Amount: Rs.${paymentAmount.toFixed(2)}</span>
                                </div>
                                ` : ''}
                            </div>
                        </div>
                        
                        <div class="d-flex justify-content-between align-items-center">
                            <a href="/booking-details.html?id=${booking.bookingId}" class="btn btn-sm btn-outline-primary">
                                <i class="bi bi-eye"></i> View Details
                            </a>
                            ${await generateActionButtons(booking, service)}
                        </div>
                    </div>
                </div>
            `);
        } catch (error) {
            console.error(`Error displaying booking ${booking.bookingId}:`, error);
            container.append(`
                <div class="alert alert-danger mb-3">
                    Error loading booking #${booking.bookingId}
                </div>
            `);
        }
    }
}

function calculatePaymentAmount(booking, service) {
    if (booking.status === 'COMPLETED') {
        return booking.hoursWorked
            ? booking.hoursWorked * service.hourlyRate + service.fixedPrice
            : service.fixedPrice;
    }
    return null;
}

function calculatepayPaymentAmount(booking, service) {
    if (booking.status === 'COMPLETED') {
        return booking.hoursWorked
            ? booking.hoursWorked * service.hourlyRate
            : service.fixedPrice;
    }
    return null;
}

function getPaymentFromBookingId(bookingId) {
    return new Promise((resolve, reject) => {
        $.ajax({
            url: `http://localhost:8080/api/v1/payments/get-by-booking/${bookingId}`,
            type: 'GET',
            headers: {
                "Authorization": "Bearer " + localStorage.getItem("token")
            },
            dataType: 'json',
            success: function(response) {
                if (response.data && response.data.data) {
                    resolve(response.data.data);
                } else {
                    resolve(null); // No payment found
                }
            },
            error: function(xhr) {
                reject(new Error(xhr.responseJSON?.message || "Failed to fetch payment"));
            }
        });
    });
}
/*
async function generateActionButtons(booking, service) {
    const payment = await getPaymentFromBookingId(booking.bookingId);
    let buttons = '';

    if (userRole === 'CUSTOMER') {
        if (booking.status === 'PENDING') {
            buttons = `
                <button class="btn btn-sm btn-danger" onclick="cancelBooking(${booking.bookingId})">
                    <i class="bi bi-x-circle"></i> Cancel
                </button>
            `;
        } else if (booking.status === 'ACCEPTED') {
            if (!payment) {
                buttons = `
                <button class="btn btn-sm btn-success" onclick="showPaymentModal(${booking.bookingId}, ${service.fixedPrice}, 'DEPOSIT')">
                    <i class="bi bi-credit-card"></i> Pay Rs.${service.fixedPrice.toFixed(2)}
                </button>
            `;
            }
        } else if (booking.status === 'COMPLETED') {
            if (payment && payment.status === 'DEPOSIT') {
                const remainingAmount = calculatepayPaymentAmount(booking, service);
                buttons = `
                <button class="btn btn-sm btn-success" onclick="showPaymentModal(${booking.bookingId}, ${remainingAmount}, 'FINAL')">
                    <i class="bi bi-credit-card"></i> Pay Rs.${remainingAmount.toFixed(2)}
                </button>
            `;
            }
        }
    } else if (userRole === 'SERVICE_PROVIDER') {
        if (booking.status === 'PENDING') {
            buttons = `
                <div class="btn-group">
                    <button class="btn btn-sm btn-success" onclick="updateBookingStatus(${booking.bookingId}, 'ACCEPTED')">
                        <i class="bi bi-check-circle"></i> Accept
                    </button>
                    <button class="btn btn-sm btn-danger" onclick="updateBookingStatus(${booking.bookingId}, 'REJECTED')">
                        <i class="bi bi-x-circle"></i> Reject
                    </button>
                </div>
            `;
        } else if (booking.status === 'ACCEPTED') {
            buttons = `
                <div class="btn-group">
                    <button class="btn btn-sm btn-primary" onclick="showDurationModal(${booking.bookingId})">
                        <i class="bi bi-check-all"></i> Complete
                    </button>
                </div>
                ${generateDurationModal(booking.bookingId)}
            `;
        }
    }

    return buttons;
}*/

function generateDurationModal(bookingId) {
    return `
   <div class="modal fade" id="durationModal-${bookingId}" tabindex="-1" role="dialog">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Complete Booking</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <div class="mb-3">
                        <label for="durationInput-${bookingId}" class="form-label">Duration (hours)</label>
                        <input type="number" id="durationInput-${bookingId}" 
                               class="form-control" min="0.5" max="24" step="0.5" value="1" required>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="button" class="btn btn-primary submit-duration" 
                            data-booking-id="${bookingId}">
                        Submit
                    </button>
                </div>
            </div>
        </div>
    </div>
    `;
}

function showDurationModal(bookingId) {
    const modalElement = document.getElementById(`durationModal-${bookingId}`);
    const modal = new bootstrap.Modal(modalElement);

    // Properly handle accessibility when showing
    modalElement.removeAttribute('aria-hidden');
    modalElement.setAttribute('aria-modal', 'true');

    modal.show();

    // Focus on the first input when modal opens
    const input = modalElement.querySelector('input');
    input.focus();
}

$(document).on('hidden.bs.modal', '.modal', function() {
    $(this).attr('aria-hidden', 'true');
    $(this).removeAttr('aria-modal');
});

$(document).on('click', '.complete-btn', function() {
    const bookingId = $(this).data('booking-id');
    showDurationModal(bookingId);
});

// Delegate event for submit buttons
$(document).on('click', '.submit-duration', function() {
    const bookingId = $(this).data('booking-id');
    updateBookingStatusWithDuration(bookingId, 'COMPLETED');
});

function updateBookingStatusWithDuration(bookingId, status) {
    console.log("f==============================================================")
    const duration = parseFloat($(`#durationInput-${bookingId}`).val());

    if (!duration || duration <= 0) {
        alert('Please enter a valid duration (minimum 0.5 hours)');
        return;
    }

    $.ajax({
        url: `http://localhost:8080/api/v1/bookings/${bookingId}/status/duration`,
        method: 'PATCH',
        headers: {
            "Authorization": "Bearer " + localStorage.getItem("token")
        },
        data: {
            status: status,
            duration: duration
        },
        success: function() {
            $(`#durationModal-${bookingId}`).modal('hide');
            loadBookings();
        },
        error: function(xhr) {
            alert(xhr.responseJSON?.message || "Failed to complete booking");
        }
    });
}
/*
function showPaymentModal(bookingId, amount, paymentType) {
    $('#paymentBookingId').val(bookingId);
    $('#paymentBookingRef').text(bookingId);
    $('#paymentAmount').text(amount.toFixed(2));
    $('#paymentType').val(paymentType);
    $('#paymentModal').modal('show');
}

function processPayment() {
    const bookingId = $('#paymentBookingId').val();
    const amount = parseFloat($('#paymentAmount').text());
    const paymentType = $('#paymentType').val();

    initiatePayHerePayment(bookingId, amount, paymentType);
}

function initiatePayHerePayment(bookingId, amount, paymentType) {
    $('#paymentModal').modal('hide');

    // Payment configuration
    const payment = {
        "sandbox": true,
        "merchant_id": "1229927",
        "return_url": window.location.origin + "/payment/success",
        "cancel_url": window.location.origin + "/payment/cancel",
        "notify_url": window.location.origin + "/payment/notify",
        "order_id": "BOOKING_" + bookingId + "_" + Date.now(),
        "items": "Service Booking #" + bookingId,
        "amount": amount.toFixed(2),
        "currency": "LKR",
        "first_name": "Customer",
        "last_name": "Name",
        "email": "customer@example.com",
        "phone": "0771234567",
        "address": "No.1, Street Name",
        "city": "Colombo",
        "country": "Sri Lanka",
        "custom_1": bookingId,
        "custom_2": paymentType
    };

    payhere.startPayment(
        payment,
        function(response) {
            console.log("Payment completed:", response);
            if (response && response.payment_id) {
                savePayment(bookingId, amount, paymentType, response.payment_id);
            } else {
                alert("Payment verification failed. Please contact support.");
            }
        },
        function(error) {
            console.error("Payment failed:", error);
            alert("Payment failed: " + (error.message || "Please try again or contact support"));
        }
    );
}

function savePayment(bookingId, amount, paymentType, paymentReference) {
    let paymentData;

    if (paymentType === 'DEPOSIT') {
        paymentData = {
            bookingId: bookingId,
            depositAmount: amount,
            finalAmount: 0.0,
            status: "DEPOSIT",
            paymentDate: new Date().toISOString(),
            paymentReference: paymentReference
        };
    } else {
        paymentData = {
            finalAmount: amount,
            status: "FULL_PAYMENT",
            paymentDate: new Date().toISOString(),
            paymentReference: paymentReference
        };
    }

    const url = paymentType === 'DEPOSIT'
        ? 'http://localhost:8080/api/v1/payments/add'
        : `http://localhost:8080/api/v1/payments/update-by-booking/${bookingId}`;

    $.ajax({
        url: url,
        type: paymentType === 'DEPOSIT' ? 'POST' : 'PATCH',
        contentType: 'application/json',
        data: JSON.stringify(paymentData),
        headers: {
            "Authorization": "Bearer " + localStorage.getItem("token")
        },
        success: function(response) {
            if (response.code === 200) {
                loadBookings();
                alert('Payment processed successfully!');
            } else {
                alert('Payment processed but record not saved: ' + response.message);
            }
        },
        error: function(xhr) {
            let errorMessage = 'Error saving payment';
            if (xhr.responseJSON && xhr.responseJSON.message) {
                errorMessage = xhr.responseJSON.message;
            }
            alert(errorMessage);
        }
    });
}*/


// Payment Modal Handler
function showPaymentModal(bookingId, amount, paymentType) {
    $('#paymentBookingId').val(bookingId);
    $('#paymentBookingRef').text(bookingId);
    $('#paymentAmount').text(amount.toFixed(2));
    $('#paymentType').val(paymentType);
    $('#paymentModal').modal('show');
}

function processPayment() {
    const bookingId = $('#paymentBookingId').val();
    const amount = parseFloat($('#paymentAmount').text());
    const paymentType = $('#paymentType').val();

    $('#paymentModal').modal('hide');
    initiatePayHerePayment(bookingId, amount);
}

function initiatePayHerePayment(bookingId, amount) {
    // Payment configuration
    const paymentConfig = {
        "sandbox": true,
        "merchant_id": "1229927",
        "return_url": "javascript:void(0)", // We'll handle this ourselves
        "cancel_url": "javascript:void(0)", // We'll handle this ourselves
        "notify_url": window.location.origin + "/payment/notify",
        "order_id": "BOOKING_" + bookingId + "_" + Date.now(),
        "items": "Service Booking #" + bookingId,
        "amount": amount.toFixed(2),
        "currency": "LKR",
        "first_name": "Customer",
        "last_name": "Name",
        "email": "customer@example.com",
        "phone": "0771234567",
        "address": "No.1, Street Name",
        "city": "Colombo",
        "country": "Sri Lanka",
        "custom_1": bookingId,
    };

    // Open PayHere checkout
    payhere.startPayment(paymentConfig);

    // Immediately close the PayHere checkout and process payment
    setTimeout(() => {
        if (typeof payhere.close === 'function') {
            payhere.close();
        }
        processPaymentAfterCheckout(bookingId, amount);
    }, 1000); // Small delay to ensure checkout opens before closing
}

async function processPaymentAfterCheckout(bookingId, amount) {
    const paymentType = await getPaymentFromBookingId(bookingId);
    console.log(paymentType)
    console.log(paymentType.paymentId);
    const paid = paymentType.paymentId;

    // Create payment data based on type
    const paymentData = paymentType === null ? {
        bookingId: bookingId,
        depositAmount: amount,
        finalAmount: 0.0,
        status: "DEPOSIT",
        paymentDate: new Date().toISOString()
    } : {
        finalAmount: amount,
        status: "FULL_PAYMENT",
        paymentDate: new Date().toISOString()
    };

    // Determine endpoint and method
    const url = paymentType === null
        ? 'http://localhost:8080/api/v1/payments/add'
        : `http://localhost:8080/api/v1/payments/${paid}`;

    const method = paymentType === null ? 'POST' : 'PATCH';

    // Process payment
    $.ajax({
        url: url,
        type: method,
        contentType: 'application/json',
        data: JSON.stringify(paymentData),
        headers: {
            "Authorization": "Bearer " + localStorage.getItem("token")
        },
        success: function (response) {
            if (response.code === 200) {
                console.log(response)

                alert('Payment processed successfully!');
                loadBookings(); // Refresh the list
            } else {
                alert('Error: ' + (response.message || 'Payment failed'));
            }
        },
        error: function (xhr) {
            alert('Payment failed: ' +
                (xhr.responseJSON?.message || 'Please try again'));
        }
    });
}

// Update generateActionButtons to use the new flow
async function generateActionButtons(booking, service) {
    const payment = await getPaymentFromBookingId(booking.bookingId);
    let buttons = '';

  /*  if (userRole === 'CUSTOMER') {
        if (booking.status === 'PENDING') {
            buttons = `<button class="btn btn-sm btn-danger" onclick="cancelBooking(${booking.bookingId})">
                <i class="bi bi-x-circle"></i> Cancel</button>`;
        }
        else if (booking.status === 'ACCEPTED') {
            if (!payment) {
                buttons = `<button class="btn btn-sm btn-success" 
                    onclick="showPaymentModal(${booking.bookingId}, ${service.fixedPrice}, 'DEPOSIT')">
                    <i class="bi bi-credit-card"></i> Pay Rs.${service.fixedPrice.toFixed(2)}</button>`;
            }
        }
        else if (booking.status === 'COMPLETED') {
            if (payment && payment.status === 'DEPOSIT') {
                const remainingAmount = calculatepayPaymentAmount(booking, service);
                buttons = `<button class="btn btn-sm btn-success" 
                    onclick="showPaymentModal(${booking.bookingId}, ${remainingAmount}, 'FINAL')">
                    <i class="bi bi-credit-card"></i> Pay Rs.${remainingAmount.toFixed(2)}</button>`;
            }
        }
    }*/
    if (userRole === 'CUSTOMER') {
        if (booking.status === 'PENDING') {
            buttons = `
                <button class="btn btn-sm btn-danger" onclick="cancelBooking(${booking.bookingId})">
                    <i class="bi bi-x-circle"></i> Cancel
                </button>
            `;
        } else if (booking.status === 'ACCEPTED') {
            if (!payment) {
                buttons = `
                <button class="btn btn-sm btn-success" onclick="showPaymentModal(${booking.bookingId}, ${service.fixedPrice}, 'DEPOSIT')">
                    <i class="bi bi-credit-card"></i> Pay Rs.${service.fixedPrice.toFixed(2)}
                </button>
            `;
            }
        } else if (booking.status === 'COMPLETED') {
            if (payment && payment.status === 'DEPOSIT') {
                const remainingAmount = calculatepayPaymentAmount(booking, service);
                buttons = `
                <button class="btn btn-sm btn-success" onclick="showPaymentModal(${booking.bookingId}, ${remainingAmount}, 'FINAL')">
                    <i class="bi bi-credit-card"></i> Pay Rs.${remainingAmount.toFixed(2)}
                </button>
            `;
            }
        }
    } else if (userRole === 'SERVICE_PROVIDER') {
        if (booking.status === 'PENDING') {
            buttons = `
                <div class="btn-group">
                    <button class="btn btn-sm btn-success" onclick="updateBookingStatus(${booking.bookingId}, 'ACCEPTED')">
                        <i class="bi bi-check-circle"></i> Accept
                    </button>
                    <button class="btn btn-sm btn-danger" onclick="updateBookingStatus(${booking.bookingId}, 'REJECTED')">
                        <i class="bi bi-x-circle"></i> Reject
                    </button>
                </div>
            `;
        } else if (booking.status === 'ACCEPTED') {
            buttons = `
            <div class="btn-group">
                <button class="btn btn-sm btn-primary complete-btn" 
                        data-booking-id="${booking.bookingId}">
                    <i class="bi bi-check-all"></i> Complete
                </button>
            </div>
            ${generateDurationModal(booking.bookingId)}
        `;
        }
    }

    // ... rest of your button generation logic ...
    return buttons;
}

// Rest of your existing helper functions (updateBookingStatus, cancelBooking, resetFilters, etc.)
// ... keep all the remaining functions unchanged ...

// Helper functions
function showSuccessMessage(msg) {
    // Replace with your preferred notification system
    alert(msg);
}

function showErrorMessage(msg) {
    // Replace with your preferred error display
    alert(msg);
}

function handlePaymentSuccessButSaveFailed(errorMsg) {
    console.error("Payment succeeded but save failed:", errorMsg);
    showErrorMessage("Payment processed but record not saved. Reference: " +
        errorMsg + ". Please contact support with this message.");
}

function updateBookingStatus(bookingId, status) {
    if (status === 'CANCELLED' && !confirm("Are you sure you want to cancel this booking?")) {
        return;
    }

    $.ajax({
        url: `http://localhost:8080/api/v1/bookings/${bookingId}/status`,
        method: 'PATCH',
        headers: {
            "Authorization": "Bearer " + localStorage.getItem("token")
        },
        data: { status: status },
        success: function() {
            loadBookings(); // Refresh the list
        },
        error: function(xhr) {
            alert(xhr.responseJSON?.message || "Failed to update status");
        }
    });
}

function cancelBooking(bookingId) {
    updateBookingStatus(bookingId, 'CANCELLED');
}

function resetFilters() {
    $('#status-filter').val('ALL');
    $('#date-from').val('');
    $('#date-to').val('');
    loadBookings();
}

function showLoadingState() {
    $('#bookings-container').html(`
        <div class="text-center py-5">
            <div class="spinner-border text-primary" role="status">
                <span class="visually-hidden">Loading...</span>
            </div>
            <p class="mt-2">Loading your bookings...</p>
        </div>
    `);
    $('#empty-state').hide();
}

function showEmptyState() {
    $('#bookings-container').empty();
    $('#empty-state').show();
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
function getUserEmailFromToken() {
    const token = localStorage.getItem("token");
    if (!token) return null;

    try {
        const decoded = jwt_decode(token);
        return decoded.sub; // Assuming your JWT has a 'role' claim
    } catch (e) {
        console.error("Error decoding token:", e);
        return null;
    }


}