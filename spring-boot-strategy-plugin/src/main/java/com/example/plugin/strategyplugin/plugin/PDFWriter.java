package com.example.plugin.strategyplugin.plugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.example.plugin.strategyplugin.domain.GenericDTO;

@Component
class PDFWriter implements WriterPlugin {

    private static final Logger logger = LoggerFactory.getLogger(PDFWriter.class);

    @Override
    public GenericDTO write(String message) {
        logger.info("writing data for type pdf with message :{}", message);
        return new GenericDTO("Writing pdf " + message);
    }

    @Override
    public boolean supports(String delimiter) {
        return delimiter.equalsIgnoreCase("pdf");
    }
}
