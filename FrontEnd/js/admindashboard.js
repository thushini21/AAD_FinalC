$(document).ready(function() {
    // Mobile menu toggle
    $('#hamburger').click(function() {
        $('.nav-links').toggleClass('active');
    });

    // Simulate loading data
    function loadStatsData() {
        // In a real app, you would fetch this from an API
        const statsData = {
            totalUsers: 1245,
            totalProviders: 328,
            totalBookings: 567,
            totalRevenue: 24890
        };

        $('.stat-number').eq(0).text(statsData.totalUsers.toLocaleString());
        $('.stat-number').eq(1).text(statsData.totalProviders.toLocaleString());
        $('.stat-number').eq(2).text(statsData.totalBookings.toLocaleString());
        $('.stat-number').eq(3).text('$' + statsData.totalRevenue.toLocaleString());
    }

    // Load activities data
    function loadActivitiesData() {
        // In a real app, you would fetch this from an API
        const activities = [
            {
                id: 1256,
                activity: "New booking - Plumbing",
                user: "user@example.com",
                time: "10 mins ago",
                status: "pending"
            },
            {
                id: 1255,
                activity: "Provider registration",
                user: "newprovider@example.com",
                time: "25 mins ago",
                status: "approved"
            },
            {
                id: 1254,
                activity: "Service completed",
                user: "provider@example.com",
                time: "1 hour ago",
                status: "completed"
            },
            {
                id: 1253,
                activity: "User account created",
                user: "newuser@example.com",
                time: "2 hours ago",
                status: "active"
            }
        ];

        const tableBody = $('.activities-table tbody');
        tableBody.empty();

        activities.forEach(activity => {
            const statusClass = activity.status === 'pending' ? 'pending' :
                activity.status === 'approved' ? 'approved' :
                    activity.status === 'completed' ? 'completed' : 'active';

            const statusText = activity.status === 'pending' ? 'Pending' :
                activity.status === 'approved' ? 'Approved' :
                    activity.status === 'completed' ? 'Completed' : 'Active';

            const row = `
        <tr>
          <td>#${activity.id}</td>
          <td>${activity.activity}</td>
          <td>${activity.user}</td>
          <td>${activity.time}</td>
          <td><span class="status ${statusClass}">${statusText}</span></td>
        </tr>
      `;

            tableBody.append(row);
        });
    }

    // Quick action buttons functionality
    $('.action-btn').click(function() {
        const actionText = $(this).text().trim();
        alert(`Action "${actionText}" would be performed here. In a real app, this would open a modal or navigate to a new page.`);
    });

    // Initialize data
    loadStatsData();
    loadActivitiesData();

    // Simulate real-time updates (in a real app, you'd use WebSockets or polling)
    setInterval(() => {
        // Randomly update some stats to simulate real-time changes
        const randomIncrement = Math.floor(Math.random() * 10) + 1;
        const currentBookings = parseInt($('.stat-number').eq(2).text().replace(/,/g, ''));
        $('.stat-number').eq(2).text((currentBookings + randomIncrement).toLocaleString());
    }, 10000);
});