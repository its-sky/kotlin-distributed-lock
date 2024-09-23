package distribution.lock.aspect

import distribution.lock.annotation.RedissonLock
import distribution.lock.util.CustomSpringELParser
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.redisson.api.RLock
import org.redisson.api.RedissonClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit
import kotlin.jvm.Throws

@Aspect
@Component
class RedissonLockAspect(
        private val redissonClient: RedissonClient
) {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @Throws(Throwable::class)
    @Around("@annotation(distribution.lock.annotation.RedissonLock)")
    fun redissonLock(joinPoint: ProceedingJoinPoint) {
        val signature = joinPoint.signature as MethodSignature
        val method = signature.method
        val annotation = method.getAnnotation(RedissonLock::class.java)
        val lockKey = method.name + CustomSpringELParser.getDynamicValue(
                signature.parameterNames, joinPoint.args, annotation.value
        )

        val lock: RLock = redissonClient.getLock(lockKey)

        try {
            val lockable = lock.tryLock(annotation.waitTime, annotation.leaseTime, TimeUnit.MILLISECONDS)
            if (!lockable) {
                logger.info("Lock 획득 실패 = $lockKey")
                return
            }
            joinPoint.proceed()
        } catch (e: InterruptedException) {
            logger.error("에러 발생", e)
            throw e
        } finally {
            logger.info("락 해제")
            lock.unlock()
        }
    }
}