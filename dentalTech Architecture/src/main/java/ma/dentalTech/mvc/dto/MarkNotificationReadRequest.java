package ma.dentalTech.mvc.dto;

public record MarkNotificationReadRequest(
        Long notificationId,
        boolean lue
) {}
