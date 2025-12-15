package com.rpgnexus.core.data.storage;

import com.rpgnexus.core.data.dto.NexusProfile;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * 플레이어 데이터 저장소 인터페이스입니다.
 * 추후 MySQL, SQLite 등으로 구현체를 교체할 수 있습니다.
 */
public interface DataStorage {

    /**
     * 데이터를 로드합니다. (비동기 권장)
     * 
     * @param uuid 플레이어 UUID
     * @return NexusProfile future
     */
    CompletableFuture<NexusProfile> load(UUID uuid);

    /**
     * 데이터를 저장합니다. (비동기 권장)
     * 
     * @param profile 저장할 프로필
     * @return 완료된 future
     */
    CompletableFuture<Void> save(NexusProfile profile);

    /**
     * 초기 저장소 설정 (테이블 생성 등)
     */
    void initialize();

    /**
     * 종료 작업 (커넥션 풀 종료 등)
     */
    void close();
}
