package com.staffmentor.ai.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.staffmentor.ai.interaction.AiInteractionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AiInteractionAspect {

    private final AiInteractionService aiInteractionService;
    private final ObjectMapper objectMapper;

    @Around("@annotation(trackAiInteraction)")
    public Object track(
            ProceedingJoinPoint joinPoint,
            TrackAiInteraction trackAiInteraction
    ) throws Throwable {

        long start = System.currentTimeMillis();

        Object[] args = joinPoint.getArgs();

        String systemPrompt = args.length > 0 ? safeToString(args[0]) : null;
        String userPrompt = args.length > 1 ? safeToString(args[1]) : null;

        String feature = trackAiInteraction.feature();

        String requestBody = objectMapper.writeValueAsString(args);

        try {

            Object result = joinPoint.proceed();

            long latency = System.currentTimeMillis() - start;

            String parsedResponse = safeToString(result);

            aiInteractionService.saveSuccess(
                    feature,
                    extractModel(joinPoint),
                    systemPrompt,
                    userPrompt,
                    requestBody,
                    parsedResponse,
                    parsedResponse,
                    latency
            );

            log.info(
                    "AI interaction success feature={} latencyMs={}",
                    feature,
                    latency
            );

            return result;

        } catch (Exception ex) {

            long latency = System.currentTimeMillis() - start;

            aiInteractionService.saveFailure(
                    feature,
                    extractModel(joinPoint),
                    systemPrompt,
                    userPrompt,
                    requestBody,
                    null,
                    ex.getMessage(),
                    latency
            );

            log.error(
                    "AI interaction failure feature={} latencyMs={} error={}",
                    feature,
                    latency,
                    ex.getMessage()
            );

            throw ex;
        }
    }

    private String extractModel(ProceedingJoinPoint joinPoint) {
        Object target = joinPoint.getTarget();

        try {
            return (String) target.getClass()
                    .getMethod("modelName")
                    .invoke(target);
        } catch (Exception ignored) {
            return "unknown";
        }
    }

    private String safeToString(Object value) {
        if (value == null) {
            return null;
        }

        String text = String.valueOf(value);

        return text.length() > 100_000
                ? text.substring(0, 100_000)
                : text;
    }
}