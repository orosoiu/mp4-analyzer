package ro.occam.mp4analyzer.service.impl;

import ro.occam.mp4analyzer.dto.Atom;
import ro.occam.mp4analyzer.service.Mp4AnalyzerService;
import ro.occam.mp4analyzer.service.UrlConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * This implementation of {@link Mp4AnalyzerService} uses a standard approach to parse the input file
 * by reading its contents as a continuous input stream of bytes and processing it linearly on-the-fly.
 */
@Component("mp4AnalyzerService")
@ConditionalOnProperty(
        value="mp4.analyzer.read.strategy",
        havingValue = "standard")
public class StandardMp4AnalyzerService extends Mp4AnalyzerService {

    @Autowired
    private UrlConnectionService urlConnectionService;

    @Override
    public List<Atom> analyze(String mp4Url) throws IOException {
        try (InputStream inputStream = urlConnectionService.getUrlConnectionInputStream(mp4Url)) {
            return extractAtoms(inputStream);
        }
    }
}
