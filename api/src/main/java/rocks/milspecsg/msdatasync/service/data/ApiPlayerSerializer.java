package rocks.milspecsg.msdatasync.service.data;

import com.google.inject.Inject;
import rocks.milspecsg.msdatasync.api.data.PlayerSerializer;
import rocks.milspecsg.msdatasync.api.data.SnapshotSerializer;
import rocks.milspecsg.msdatasync.api.member.MemberRepository;
import rocks.milspecsg.msdatasync.model.core.Member;
import rocks.milspecsg.msdatasync.model.core.Snapshot;

import java.util.concurrent.CompletableFuture;

public abstract class ApiPlayerSerializer<M extends Member, S extends Snapshot, P, K, U> extends ApiSerializer<S, P, K> implements PlayerSerializer<S, P> {

    @Inject
    protected MemberRepository<M, S, U> memberRepository;

    @Inject
    protected SnapshotSerializer<S, P> snapshotSerializer;

    @Override
    public String getName() {
        return "msdatasync:player";
    }

    @Override
    public boolean serialize(S snapshot, P player) {
        return snapshotSerializer.serialize(snapshot, player);
    }

    @Override
    public boolean deserialize(S snapshot, P player) {
        return snapshotSerializer.deserialize(snapshot, player);
    }
}
