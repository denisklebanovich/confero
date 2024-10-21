package org.zpi.conferoapi.session;

import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link Attachment}
 */
@Value
public class AttachmentDto implements Serializable {
    String id;
    String name;
    String url;
}