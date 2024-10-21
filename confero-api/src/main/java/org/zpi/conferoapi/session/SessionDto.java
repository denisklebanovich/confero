package org.zpi.conferoapi.session;

import lombok.Value;
import org.zpi.conferoapi.presenter.PresenterDto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for {@link Session}
 */
@Value
public class SessionDto implements Serializable {
    Long id;
    Integer duration;
    String title;
    String description;
    PresenterDto presenter;
    LocalDateTime startTime;
    LocalDateTime endTime;
    String streamUrl;
    List<AttachmentDto> attachments;
}