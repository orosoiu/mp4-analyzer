package ro.occam.mp4analyzer.service.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.List;

/**
 * A decoder that splits the received ByteBufs dynamically by the value of an Atom's length header
 *
 * Automatically detects header/body boundary in a HTTP response and returns the header
 * as a single frame without further processing
 * Returns the response body as individual frames based on the length of each (atom) frame
 * as indicated by the first 4 bytes of metadata for each frame
 *
 * The decoder does not validate the contents of the mp4 file, it assumes that it is a properly formatted
 * fragmented mp4 file and the reported metadata (e.g. length) for each atom is correct.
 */
public class AtomFrameDecoder extends ByteToMessageDecoder {

    private final byte[] htmlBodyDelimiter = HexFormat.ofDelimiter(":").parseHex("0D:0A:0D:0A");
    private boolean htmlHeadersFrameProcessed = false;

    @Override
    protected final void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        if (!htmlHeadersFrameProcessed) {
            int bodyBoundary = detectHtmlBodyBoundary(in);
            if (bodyBoundary > 0) {
                out.add(in.retainedSlice(0, bodyBoundary));
                in.readerIndex(bodyBoundary);
                htmlHeadersFrameProcessed = true;
            } else {
                // header spans multiple frames, defer processing for next frame
                return;
            }
        }
        extractAtomFrames(in, out);
    }

    private void extractAtomFrames(ByteBuf in, List<Object> out) {
        if (in.readableBytes() < 4) {
            return;
        }

        do {
            int readerIndex = in.readerIndex();
            byte[] sizeMetadataBuffer = new byte[4];
            in.retainedSlice(readerIndex, 4).readBytes(sizeMetadataBuffer);
            int size = ByteBuffer.wrap(sizeMetadataBuffer).getInt();
            if (size > in.readableBytes()) {
                return;
            }
            out.add(in.retainedSlice(readerIndex, size));
            in.readerIndex(readerIndex + size);
        } while (in.readableBytes() > 4);
    }

    private int detectHtmlBodyBoundary(ByteBuf in) {
        byte[] contents = new byte[in.readableBytes()];
        in.retainedSlice(0, in.readableBytes()).readBytes(contents);

        // look for a body boundary match in the buffer contents
        for (int i = 0; i < contents.length - 4; i++) {
            if(Arrays.equals(htmlBodyDelimiter, Arrays.copyOfRange(contents, i, i+4))) {
                return i + 4;
            }
        }

        return 0;
    }
}
