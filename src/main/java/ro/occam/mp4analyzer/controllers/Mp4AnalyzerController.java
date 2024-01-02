package ro.occam.mp4analyzer.controllers;

import ro.occam.mp4analyzer.dto.Atom;
import ro.occam.mp4analyzer.service.Mp4AnalyzerService;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import ro.occam.mp4analyzer.Constants;

import java.io.IOException;
import java.util.List;

@RestController
@Validated
public class Mp4AnalyzerController {

    @Autowired
    private Mp4AnalyzerService mp4AnalyzerService;

    @GetMapping
    public List<Atom> analyzeMp4File(@RequestHeader(Constants.X_VIDEO_URL_REQUEST_HEADER) @NotBlank @URL String videoUrl) throws IOException {
        return mp4AnalyzerService.analyze(videoUrl);
    }
}
