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

    // Initialize modals container
    if (!$('#modals-container').length) {
        $('body').append('<div id="modals-container"></div>');
    }

    // Event delegation for dynamic elements
    $(document).on('click', '.complete-btn', function() {
        const bookingId = $(this).data('booking-id');
        showDurationModal(bookingId);
    });

    $(document).on('click', '.submit-duration', function() {
        const bookingId = $(this).data('booking-id');
        updateBookingStatusWithDuration(bookingId, 'COMPLETED');
    });

    // Payment modal handler
    $('#process-payment').click(processPayment);
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

    const modalsContainer = $('#modals-container');
    modalsContainer.empty();

    for (const booking of bookings) {
        try {
            const bookingDate = new Date(booking.bookingDateTime);
            const formattedDate = bookingDate.toLocaleDateString();
            const formattedTime = bookingDate.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
            const service = await getServiceFromServiceId(booking.serviceId);
            const paymentAmount = calculatePaymentAmount(booking, service);

            // Generate and append booking card
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
                                    <span>Additional Charge: Rs.${service.hourlyRate.toFixed(2)}</span>
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

            // Generate and append modal if needed
            if (userRole === 'SERVICE_PROVIDER' && booking.status === 'ACCEPTED') {
                modalsContainer.append(generateDurationModal(booking.bookingId));
            }
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

function generateDurationModal(bookingId) {
    return `
    <div class="modal fade" id="durationModal-${bookingId}" tabindex="-1" aria-labelledby="durationModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="durationModalLabel">Complete Booking</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <div class="mb-3">
                        <label for="durationInput-${bookingId}" class="form-label">Duration (hours)</label>
                        <input type="number" id="durationInput-${bookingId}" 
                               class="form-control" min="0.5" max="24" step="0.5" value="1" required>
                        <div class="invalid-feedback">Please enter a valid duration (0.5-24 hours)</div>
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
    if (modalElement) {
        const modal = new bootstrap.Modal(modalElement);
        modal.show();

        // Reset validation
        $(`#durationInput-${bookingId}`).removeClass('is-invalid');
    } else {
        console.error(`Modal element with ID durationModal-${bookingId} not found`);
    }
}

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
            } else {
                buttons = `
                <button class="btn btn-sm btn-danger" onclick="showReviewModal(${booking.bookingId})">
                    <i class="bi bi-plus">add Review</i>
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
                <button class="btn btn-sm btn-primary complete-btn" data-booking-id="${booking.bookingId}">
                    <i class="bi bi-check-all"></i> Complete
                </button>
            `;
        }
    }

    return buttons;
}

function updateBookingStatusWithDuration(bookingId, status) {
    const durationInput = $(`#durationInput-${bookingId}`);
    const duration = parseFloat(durationInput.val());

    // Validate input
    if (isNaN(duration) || duration <= 0) {
        durationInput.addClass('is-invalid');
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
    let paid
    if (paymentType != null){
        console.log(paymentType.paymentId);
        paid = paymentType.paymentId;
    }


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
                Swal.fire({
                    icon: 'success',
                    title: 'Payment processed successfully!',
                    showConfirmButton: false,
                    timer: 9500
                }).then(() => {
                    loadBookings(); // Refresh the list
                });


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

// Review Modal Functions
function showReviewModal(bookingId) {
    $('#bookingId').val(bookingId);
    $('#rating').val('');
    $('#comment').val('');
    $('#reviewModal').modal('show');
}

function submitReview() {
    const bookingId = $('#bookingId').val();
    const rating = $('#rating').val();
    const comment = $('#comment').val();

    if (!rating) {
        alert('Please select a rating');
        return;
    }

    const reviewData = {
        bookingId: bookingId,
        rating: rating,
        comment: comment
    };

    $.ajax({
        url: 'http://localhost:8080/api/v1/reviews/add',
        method: 'POST',
        headers: {
            "Authorization": "Bearer " + localStorage.getItem("token"),
            "Content-Type": "application/json"
        },
        data: JSON.stringify(reviewData),
        success: function() {
            $('#reviewModal').modal('hide');
            Swal.fire({
                icon: 'success',
                title: 'Review submitted successfully!',
                showConfirmButton: false,
                timer: 1500
            }).then(() => {
                loadBookings();
            });
        },
        error: function(xhr) {
            Swal.fire({
                icon: 'error',
                title: 'Failed to submit review',
                text: xhr.responseJSON?.message || 'Please try again'
            });
        }
    });
}

// Status Update Functions
function updateBookingStatus(bookingId, status) {
    if (status === 'CANCELLED') {
        Swal.fire({
            title: 'Are you sure?',
            text: "You want to cancel this booking!",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#3085d6',
            cancelButtonColor: '#d33',
            confirmButtonText: 'Yes, cancel it!'
        }).then((result) => {
            if (result.isConfirmed) {
                performStatusUpdate(bookingId, status);
            }
        });
    } else {
        performStatusUpdate(bookingId, status);
    }
}

function performStatusUpdate(bookingId, status) {
    $.ajax({
        url: `http://localhost:8080/api/v1/bookings/${bookingId}/status`,
        method: 'PATCH',
        headers: {
            "Authorization": "Bearer " + localStorage.getItem("token")
        },
        data: { status: status },
        success: function() {
            Swal.fire({
                title: 'Success!',
                text: 'Booking status updated successfully',
                icon: 'success',
                confirmButtonText: 'OK'
            }).then(() => {
                loadBookings();
            });
        },
        error: function(xhr) {
            Swal.fire({
                title: 'Error!',
                text: xhr.responseJSON?.message || "Failed to update status",
                icon: 'error',
                confirmButtonText: 'OK'
            });
        }
    });
}

function cancelBooking(bookingId) {
    updateBookingStatus(bookingId, 'CANCELLED');
}

// Helper Functions
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

function getStatusBadgeClass(status) {
    const classes = {
        'PENDING': 'bg-warning',
        'ACCEPTED': 'bg-primary',
        'COMPLETED': 'bg-success',
        'CANCELLED': 'bg-secondary',
        'REJECTED': 'bg-danger'
    };
    return classes[status] || 'bg-light text-dark';
}

function getUserRoleFromToken() {
    const token = localStorage.getItem("token");
    if (!token) return null;

    try {
        const decoded = jwt_decode(token);
        return decoded.role;
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
        return decoded.sub;
    } catch (e) {
        console.error("Error decoding token:", e);
        return null;
    }
}