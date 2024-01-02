# Fragmented MP4 analyzer

The project exposes a REST controller with a single endpoint that accepts an URL to an mp4 file as a parameter - sent as the value of the `X-VIDEO-URL` header - then reads the file at that location and returns the json representation of the file contents as a series nested atoms.

## Implementation details

The project ships with two implementations for an mp4 file parser: `standard` and `reactive`.

`standard` implementation reads the file contents as a continuous stream of bytes which is processes on the fly to extract the atoms.

`reactive` implementation uses Netty to read the file, partition it's contents in variable sized frames - one per each root atom - which is then returned and processed as a `Flux` of objects. Each frame has the length indicated by the corresponding atom's length metadata; the frames are processed using a custom `ByteToMessageDecoder` implementation at [AtomFrameDecoder](src/main/java/ro/occam/mp4analyzer/service/decoder/AtomFrameDecoder.java).

The implementation does not validate the file structure, if the indicated file is not a valid mp4 file then the service will error or return inconsistent data.

## Prerequisites

The project requires Maven and JRE 17 to run.

## Running the application

On Unix systems to start the application simply run the following command (Maven needs to be installed on the system):
```shell
mvn spring-boot:run
```

On Windows systems the wrapper supplied with the app can be used:
```shell
./mvnw spring-boot:run
```

By default the app runs in standard mode; to run it in reactive mode change the value of the `mp4.analyzer.read.strategy` parameter to `reactive` as such:
```shell
mvn spring-boot:run -Dspring-boot.run.arguments=--mp4.analyzer.read.strategy=reactive
```

## Running the test suite

The app ships with some unit and integration tests for the standard and reactive implementations; the following command will run the test suite:
```shell
mvn clean test
```

## Making a request

To manually make a request using `curl` start the application and then run the following command:
```shell
curl -H "X-VIDEO-URL: http://example.com/test.mp4" http://localhost:8080
```

For any other HTTP/Rest client just set the `X-VIDEO-URL` header value to the URL of the file to be analyzed.