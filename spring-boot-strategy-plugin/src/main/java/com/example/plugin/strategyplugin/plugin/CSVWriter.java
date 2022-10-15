package com.example.plugin.strategyplugin.plugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
class CSVWriter implements WriterPlugin {

    private static final Logger logger = LoggerFactory.getLogger(CSVWriter.class);

    @Override
    public String write(String message) {
        logger.info("writing data for type csv with message :{}", message);
        return "Writing CSV " + message;
    }

    @Override
    public boolean supports(String delimiter) {
        return delimiter.equalsIgnoreCase("csv");
    }
}
