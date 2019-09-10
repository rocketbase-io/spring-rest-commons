package io.rocketbase.commons.logging;

import io.rocketbase.commons.config.LoggingAspectProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.slf4j.event.Level;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoggableConfig {

    private boolean trim;
    private boolean duration;
    private boolean skipArgs;
    private boolean skipResult;

    @Builder.Default
    private Level logLevel = Level.DEBUG;

    @Builder.Default
    private Level errorLogLevel = Level.WARN;

    @Builder.Default
    private int trimLength = 100;

    public LoggableConfig(Loggable loggable) {
        trim = loggable.trimLength() > 0;
        duration = loggable.duration();
        skipArgs = loggable.skipArgs();
        skipResult = loggable.skipResult();
        logLevel = Level.valueOf(loggable.logLevel());
        if (loggable.errorLogLevel() != null && !loggable.errorLogLevel().equalsIgnoreCase("NONE")) {
            errorLogLevel = Level.valueOf(loggable.errorLogLevel());
        }
        trimLength = loggable.trimLength();
    }

    public LoggableConfig(LoggingAspectProperties config) {
        trim = config.isTrim();
        duration = config.isDuration();
        skipArgs = config.isSkipArgs();
        skipResult = config.isSkipResult();
        logLevel = Level.valueOf(config.getLogLevel());
        if (config.getErrorLogLevel() != null && !config.getErrorLogLevel().equalsIgnoreCase("NONE")) {
            errorLogLevel = Level.valueOf(config.getErrorLogLevel());
        }
        trimLength = config.getTrimLength();
    }
}
