package ru.vladislemon.yggdrasilserver.profile;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
interface ProfileRepository extends CrudRepository<Profile, UUID> {

    Iterable<Profile> findByUserId(UUID userId);

    Optional<Profile> findByName(String name);

    int countByUserId(UUID userId);
}
