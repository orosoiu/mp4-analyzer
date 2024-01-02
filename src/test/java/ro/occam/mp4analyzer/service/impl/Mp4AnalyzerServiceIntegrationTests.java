package ro.occam.mp4analyzer.service.impl;

import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okio.Buffer;
import org.junit.Assert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ro.occam.mp4analyzer.Constants;
import ro.occam.mp4analyzer.util.TestData;

import java.io.IOException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("standard")
class StandardMp4AnalyzerServiceIntegrationTests extends Mp4AnalyzerServiceIntegrationTests {}

@ActiveProfiles("reactive")
class ReactiveMp4AnalyzerServiceIntegrationTests extends Mp4AnalyzerServiceIntegrationTests {}

@SpringBootTest
@AutoConfigureMockMvc
abstract public class Mp4AnalyzerServiceIntegrationTests {

    public static MockWebServer mockWebServer;
    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    static void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @BeforeEach
    public void setUpMocks() {
        mockWebServer.enqueue(new MockResponse().setBody(new Buffer().write(TestData.RAW_MP4_CONTENTS)));
    }

    @Test
    public void testAnalyze() {
        HttpUrl baseUrl = mockWebServer.url("test.mp4");

        try {
            mockMvc.perform(get("/")
                            .header(Constants.X_VIDEO_URL_REQUEST_HEADER, baseUrl)
                            .accept(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().is2xxSuccessful())
                    .andExpect(content().json(TestData.EXPECTED_JSON_RESPONSE));;
        } catch (Exception e) {
            Assert.fail();
        }
    }

}