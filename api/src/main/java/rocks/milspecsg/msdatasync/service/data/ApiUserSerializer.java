package rocks.milspecsg.msdatasync.service.data;

import com.google.inject.Inject;
import rocks.milspecsg.msdatasync.api.data.SnapshotSerializer;
import rocks.milspecsg.msdatasync.api.data.UserSerializer;
import rocks.milspecsg.msdatasync.api.member.MemberRepository;
import rocks.milspecsg.msdatasync.model.core.Member;
import rocks.milspecsg.msdatasync.model.core.Snapshot;

public abstract class ApiUserSerializer<M extends Member, S extends Snapshot, K, U> extends ApiSerializer<S, K, U> implements UserSerializer<S, U> {

    @Inject
    protected MemberRepository<M, S, U> memberRepository;

    @Inject
    protected SnapshotSerializer<S, U> snapshotSerializer;

    @Override
    public String getName() {
        return "msdatasync:player";
    }

    @Override
    public boolean serialize(S snapshot, U user) {
        return snapshotSerializer.serialize(snapshot, user);
    }

    @Override
    public boolean deserialize(S snapshot, U user) {
        return snapshotSerializer.deserialize(snapshot, user);
    }
}
