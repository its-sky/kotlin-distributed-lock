package distribution.lock.annotation

/**
 * Redisson 분산 락을 적용하기 위한 어노테이션.
 *
 * @property value Lock의 이름 (고유값)
 * @property waitTime Lock을 획득하려고 시도하는 최대 시간 (ms)
 * @property leaseTime Lock을 획득한 후 점유할 수 있는 최대 시간 (ms)
 */
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
annotation class RedissonLock(
        val value: String,
        val waitTime: Long = 5000L,
        val leaseTime: Long = 2000L
)
