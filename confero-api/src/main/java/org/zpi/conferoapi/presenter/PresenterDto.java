package org.zpi.conferoapi.presenter;

import lombok.Value;
import org.zpi.conferoapi.proposal.Presenter;

import java.io.Serializable;

/**
 * DTO for {@link Presenter}
 */
@Value
public class PresenterDto implements Serializable {
    String orcid;
    String name;
    String surname;
}