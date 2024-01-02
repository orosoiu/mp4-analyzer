package ro.occam.mp4analyzer.util;

public final class TestData {

    public static final byte[] RAW_MP4_CONTENTS = new byte[]{
            // moof
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x20,             // size metadata
            (byte) 0x6D, (byte) 0x6F, (byte) 0x6F, (byte) 0x66,             // type metadata
                // mfhd
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x08,         // size metadata
                (byte) 0x6D, (byte) 0x66, (byte) 0x68, (byte) 0x64,         // type metadata
                // traf
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x10,         // size metadata
                (byte) 0x74, (byte) 0x72, (byte) 0x61, (byte) 0x66,         // type metadata
                    // tfhd
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x08,     // size metadata
                    (byte) 0x74, (byte) 0x66, (byte) 0x68, (byte) 0x64,     // type metadata
            // mdat
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x10,             // size metadata
            (byte) 0x6D, (byte) 0x64, (byte) 0x61, (byte) 0x74,             // type metadata
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,             // payload
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00              // payload
    };
    public static final String EXPECTED_JSON_RESPONSE = """
            [
                {
                    "type": "moof",
                    "description": "Movie Fragment Box",
                    "sizeInBytes": 32,
                    "children": [
                        {
                            "type": "mfhd",
                            "description": "Movie Fragment Header Box",
                            "sizeInBytes": 8,
                            "children": null
                        },
                        {
                            "type": "traf",
                            "description": "Track Fragment Box",
                            "sizeInBytes": 16,
                            "children": [
                                {
                                    "type": "tfhd",
                                    "description": "Track Fragment Header Box",
                                    "sizeInBytes": 8,
                                    "children": null
                                }
                            ]
                        }
                    ]
                },
                {
                    "type": "mdat",
                    "description": "Media Data Box",
                    "sizeInBytes": 16,
                    "children": null
                }
            ]
            """;

    private TestData() {
    }
}
