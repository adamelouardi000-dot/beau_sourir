package ma.dentalTech.service.modules.users.api;

import ma.dentalTech.mvc.dto.*;

import java.util.List;

public interface NotificationService {

    List<NotificationDto> findByUser(Long userId);
    NotificationDto create(NotificationCreateRequest request);

    void markRead(MarkNotificationReadRequest request);
    void deleteById(Long id);
}
