package ro.occam.mp4analyzer.service.impl;

import ro.occam.mp4analyzer.dto.Atom;
import ro.occam.mp4analyzer.service.UrlConnectionService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ro.occam.mp4analyzer.util.TestData;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StandardMp4AnalyzerServiceUnitTests {

    @Mock
    private UrlConnectionService urlConnectionService;

    @InjectMocks
    private StandardMp4AnalyzerService standardMp4AnalyzerService;

    /**
     * Small sanity test for StandardMp4AnalyzerService, verifies decoding a hardcoded byte array returns correct data
     */
    @Test
    public void testAnalyze() {
        try {
            when(urlConnectionService.getUrlConnectionInputStream(anyString()))
                    .thenReturn(new ByteArrayInputStream(TestData.RAW_MP4_CONTENTS));

            List<Atom> result = standardMp4AnalyzerService.analyze("test");

            assertEquals(result.size(), 2);

            assertEquals(result.get(0).getType(), "moof");
            assertEquals(result.get(0).getSizeInBytes(), 32);
            assertEquals(result.get(0).getChildren().size(), 2);

            assertEquals(result.get(0).getChildren().get(0).getType(), "mfhd");
            assertEquals(result.get(0).getChildren().get(0).getSizeInBytes(), 8);

            assertEquals(result.get(0).getChildren().get(1).getType(), "traf");
            assertEquals(result.get(0).getChildren().get(1).getSizeInBytes(), 16);
            assertEquals(result.get(0).getChildren().get(1).getChildren().size(), 1);

            assertEquals(result.get(0).getChildren().get(1).getChildren().get(0).getType(), "tfhd");
            assertEquals(result.get(0).getChildren().get(1).getChildren().get(0).getSizeInBytes(), 8);

            assertEquals(result.get(1).getType(), "mdat");
            assertEquals(result.get(1).getSizeInBytes(), 16);
        } catch (IOException e) {
            Assertions.fail();
        }
    }
}
