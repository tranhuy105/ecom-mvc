$(document).ready(function() {
    $('.dropdown-menu').on('click', function (e) {
        e.stopPropagation();
    });

    let consecutiveErrors = 0;
    const maxErrors = 3;
    const notificationCountElement = $('#notificationCount');
    const notificationListElement = $('#notificationList');
    const notificationBell = $('#notificationBell');
    let eventSource;

    function getNotifications() {
        const notifications = localStorage.getItem('notifications');
        return notifications ? JSON.parse(notifications) : [];
    }

    function saveNotification(notification) {
        let notifications = getNotifications();
        notifications.unshift({ ...notification, isRead: false });

        if (notifications.length > 15) {
            notifications.pop();
        }

        localStorage.setItem('notifications', JSON.stringify(notifications));
        updateNotificationCount();
        updateNotificationList();
    }

    function markAllAsRead() {
        let notifications = getNotifications();
        notifications.forEach(notification => notification.isRead = true); // Mark all notifications as read
        localStorage.setItem('notifications', JSON.stringify(notifications));
        updateNotificationCount();
        updateNotificationList();
    }

    function updateNotificationCount() {
        const notifications = getNotifications();
        const unreadCount = notifications.filter(notification => !notification.isRead).length;
        if (unreadCount > 0) {
            notificationCountElement.text(unreadCount);
            notificationCountElement.show(); // Show count
            notificationBell.addClass("new-notification");
        } else {
            notificationCountElement.text(0);
            notificationCountElement.hide(); // Hide count if no unread notifications
            notificationBell.removeClass("new-notification");
        }
    }

    function updateNotificationList() {
        const notifications = getNotifications();
        notificationListElement.empty();
        if (notifications.length > 0) {
            notifications.forEach(notification => {
                const formattedNotification = formatNotification(notification);
                notificationListElement.append(`<li>${formattedNotification}</li>`);
            });
        } else {
            notificationListElement.append('<li>No new notifications</li>');
        }
    }

    function formatNotification(notification) {
        const timestamp = new Date(notification.timestamp).toLocaleString();
        const readStatus = notification.isRead ? 'read' : 'unread';

        const notificationMessage = notification.href
            ? `<a href="${notification.href}" class="${readStatus}">${notification.message}</a>`
            : `<p class="${readStatus}">${notification.message}</p>`;

        return `${notificationMessage}<span class="timestamp">${timestamp}</span>`;
    }


    // Initialize SSE connection
    function startSSE() {
        eventSource = new EventSource("/site/sse/connect");

        eventSource.addEventListener("notification", function(event) {
            const notificationData = JSON.parse(event.data);
            saveNotification(notificationData);
        });

        eventSource.onopen = function() {
            console.log('SSE connection established');
            consecutiveErrors = 0;
        };

        eventSource.onerror = function() {
            console.error("SSE connection error. Reconnecting...");
            consecutiveErrors++;
            eventSource.close();

            if (consecutiveErrors < maxErrors) {
                setTimeout(startSSE, 1000);
            } else {
                console.warn("Maximum reconnection attempts reached. Stopping SSE attempts.");
            }
        };
    }

    // startSSE();
    window.onunload = function() {
        if (eventSource) {
            eventSource.close();
        }
    };

    notificationBell.on('click', function() {
        markAllAsRead();
    });

    updateNotificationCount();
    updateNotificationList();
});