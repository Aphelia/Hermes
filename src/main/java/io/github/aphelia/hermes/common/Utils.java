package io.github.aphelia.hermes.common;

import java.util.logging.Level;

public interface Utils {
    void sendMessage(String string);
    void log(Level level, String string);
    String getFormat();
}
