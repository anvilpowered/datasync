package rocks.milspecsg.msdatasync.service.data;

import com.google.inject.Inject;
import rocks.milspecsg.msdatasync.api.data.*;
import rocks.milspecsg.msdatasync.api.member.MemberRepository;
import rocks.milspecsg.msdatasync.api.snapshot.SnapshotRepository;
import rocks.milspecsg.msdatasync.model.core.Member;
import rocks.milspecsg.msdatasync.model.core.Snapshot;

/**
 * @param <S> {@link Snapshot} or subclass. Default implementation by MSDataSync as {@link Snapshot}
 * @param <U> User class to get data from
 * @param <K> Key class
 */
public abstract class ApiSerializer<S extends Snapshot, K, U> implements Serializer<S, U> {

    @Inject
    protected SnapshotRepository<S, K> snapshotRepository;

}