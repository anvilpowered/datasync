package rocks.milspecsg.msdatasync.service.tasks;

import com.google.inject.Inject;
import rocks.milspecsg.msdatasync.api.data.UserSerializer;
import rocks.milspecsg.msdatasync.api.member.MemberRepository;
import rocks.milspecsg.msdatasync.api.tasks.SerializationTaskService;
import rocks.milspecsg.msdatasync.model.core.Member;
import rocks.milspecsg.msdatasync.model.core.Snapshot;

public abstract class ApiSerializationTaskService<M extends Member, S extends Snapshot, U> implements SerializationTaskService {

    @Inject
    protected UserSerializer<S, U> userSerializer;

    @Inject
    protected MemberRepository<M, S, U> memberRepository;

}
