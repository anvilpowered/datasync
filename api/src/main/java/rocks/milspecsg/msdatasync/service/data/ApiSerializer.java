package rocks.milspecsg.msdatasync.service.data;

import com.google.inject.Inject;
import rocks.milspecsg.msdatasync.api.data.*;
import rocks.milspecsg.msdatasync.api.member.MemberRepository;
import rocks.milspecsg.msdatasync.model.core.Member;

public abstract class ApiSerializer<M extends Member, P, K, U> implements Serializer<M, P> {

    @Inject
    protected MemberRepository<M, P, K, U> memberRepository;

}