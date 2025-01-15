package com.nl.pm.server.operationLog;

import java.io.IOException;

public interface ILogFormatter {
    void beforeContext(LogFormatterContext context) throws IOException;

    void afterContext(LogFormatterContext context);
}
