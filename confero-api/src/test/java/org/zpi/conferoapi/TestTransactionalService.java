package org.zpi.conferoapi;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Supplier;

public class TestTransactionalService {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public <T> T runInNewTransaction(Supplier<T> supplier) {
        return supplier.get();
    }
}
