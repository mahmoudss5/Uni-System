package UnitSystem.demo.Aspect.Security;

import UnitSystem.demo.ExcHandler.Entites.RateLimitExceededException;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Aspect
@Component
public class RateLimitAspect {


    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Around("@annotation(rateLimit)")
    public Object handle(ProceedingJoinPoint pjp, RateLimit rateLimit)
            throws Throwable {


        String user = currentUser();
        String key  = rateLimit.key() + ":" + user;

        Bucket bucket = buckets.computeIfAbsent(key, k ->
                Bucket.builder()
                        .addLimit(Bandwidth.classic(
                                rateLimit.requests(),
                                Refill.intervally(
                                        rateLimit.requests(),
                                        Duration.ofSeconds(rateLimit.perSeconds()))))
                        .build()
        );

        if (bucket.tryConsume(1)) {
            return pjp.proceed();
        }

        throw new RateLimitExceededException(
                "Limit: " + rateLimit.requests() +
                        " requests/" + rateLimit.perSeconds() + "s exceeded."
        );
    }

    private String currentUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null) ? auth.getName() : "anonymous";
    }
}
