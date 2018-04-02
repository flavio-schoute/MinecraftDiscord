/*
 *     This file is part of MinecraftDiscord.
 *
 *     MinecraftDiscord is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     MinecraftDiscord is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with MinecraftDiscord.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.jordieh.minecraftdiscord.util;

import io.github.jordieh.minecraftdiscord.MinecraftDiscord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.*;

public class LangUtil {

    private static LangUtil instance;
    private final Logger logger = LoggerFactory.getLogger(LangUtil.class);
    private final String MESSAGES;
    private final ResourceBundle EMPTY_BUNDLE = new ResourceBundle() {
        @Override
        protected Object handleGetObject(String key) {
            return null;
        }

        @Override
        public Enumeration<String> getKeys() {
            return null;
        }
    };
    private ResourceBundle resourceBundle;
    private ResourceBundle languageBundle;
    private ResourceBundle customBundle;
    private Locale locale;
    private Map<String, MessageFormat> formatMap;
    private TranslationLoader loader;

    private LangUtil() {
        MESSAGES = "messages";
        loader = new TranslationLoader(LangUtil.class.getClassLoader());
        resourceBundle = ResourceBundle.getBundle(MESSAGES, Locale.ENGLISH, loader); //@TODO ++
        languageBundle = resourceBundle;
        formatMap = new HashMap<>();


    }

    public static LangUtil getInstance() {
        return instance == null ? instance = new LangUtil() : instance;
    }

    public static String tr(String message, Object... objects) {
        return FormatUtil.formatColors(getInstance().format(message, objects));
    }

    private String translate(String message) {
        try {
            return languageBundle.getString(message);
        } catch (MissingResourceException e) {
            logger.warn("Missing translation key ({}) detected in translation file {}", e.getKey(), languageBundle.getLocale().toString(), e);
            return resourceBundle.getString(message);
        }
    }

    public String format(String message, Object... objects) {
        String pattern = translate(message);
        MessageFormat messageFormat = formatMap.get(message);
        if (messageFormat == null) {
            try {
                messageFormat = new MessageFormat(pattern);
            } catch (IllegalArgumentException e) {
                logger.warn("Invalid translation key detected for {}: {}", message, e.getMessage());
                pattern = pattern.replaceAll("\\{(\\D*?)}", "[$1]");
                messageFormat = new MessageFormat(pattern);
            }
            formatMap.put(pattern, messageFormat);
        }
        return messageFormat.format(objects);
    }

    public void updateLocale(String str) {
        if (str != null) {
            String[] parts = str.split("[_.]");
            if (parts.length == 1) {
                locale = new Locale(parts[0]);
            } else if (parts.length == 2) {
                locale = new Locale(parts[0], parts[1]);
            } else if (parts.length == 3) {
                locale = new Locale(parts[0], parts[1], parts[2]);
            }
        }

        ResourceBundle.clearCache();
        formatMap.clear();
        logger.info("Now using language {}", locale.toString());

        try { //@TODO ++
            languageBundle = ResourceBundle.getBundle(MESSAGES, locale, loader);
        } catch (MissingResourceException e) {
            languageBundle = EMPTY_BUNDLE;
        }

        try {
            customBundle = ResourceBundle.getBundle(MESSAGES, locale, loader);
        } catch (MissingResourceException e) {
            customBundle = EMPTY_BUNDLE;
        }

    }

    private static final class TranslationLoader extends ClassLoader {
        private final File DATAFOLDER;

        public TranslationLoader(ClassLoader parent) {
            super(parent);
            DATAFOLDER = MinecraftDiscord.getInstance().getDataFolder();
        }

        @Override
        public URL getResource(String name) {
            File file = new File(DATAFOLDER.getAbsolutePath() + File.separator + "language", name);
            System.out.println(file.getAbsolutePath());
            if (file.exists()) {
                try {
                    return file.toURI().toURL();
                } catch (MalformedURLException ignored) {
                }
            }
            return null;
        }

        @Override
        public InputStream getResourceAsStream(String name) {
            File file = new File(DATAFOLDER.getAbsolutePath() + File.separator + "language", name);
            System.out.println(file.getAbsolutePath());
            if (file.exists()) {
                try {
                    return new FileInputStream(file);
                } catch (FileNotFoundException ignored) {
                }
            }
            return null;
        }
    }
}
