package com.discordsrv.heads.services.profiles;

import java.io.IOException;
import java.util.UUID;

public interface ProfileSupplier {

    Profile resolve(String username) throws IOException;
    Profile resolve(UUID uuid) throws IOException;

}
