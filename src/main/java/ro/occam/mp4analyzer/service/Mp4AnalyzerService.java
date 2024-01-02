package ro.occam.mp4analyzer.service;

import ro.occam.mp4analyzer.dto.Atom;
import ro.occam.mp4analyzer.dto.AtomType;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * This service reads an mp4 file from the URL sent as a parameter
 * and returns a representation of the file contents as a list of nested atom boxes.
 * All reported IOException instances should be delegated to be handled by the controller.
 * The service uses a recursive approach to parse nested atoms (containers).
 *
 * Assumptions:
 * - the service does not validate the contents of the mp4 file, it assumes that it is a properly formatted
 *   fragmented mp4 file and the reported length of each atom is correct, and the summed lengths of all
 *   container's children matches the reported length of the container (minus the 8 bytes of metadata)
 * - stop condition for recursive calls - the service relies on the correctness of the mp4 file and it's
 *   contents to ensure proper stop condition for recursive calls; in theory a sufficiently large mp4 file
 *   consisting only of infinitely nested container atoms could be crafted so that it would result in a
 *   stack overflow when parsing it, but such edge case is beyond the scope of current exercise
*/
public abstract class Mp4AnalyzerService {

    public abstract List<Atom> analyze(String mp4Url) throws IOException;

    /**
     * Extracts all atoms from an input stream and return them as a list
     * @param inputStream the input stream
     * @return list of converted Atom objects
     * @throws IOException
     */
    protected List<Atom> extractAtoms(InputStream inputStream) throws IOException {
        List<Atom> result = new ArrayList<>();
        Atom atom;
        while ((atom = getNextAtom(inputStream)) != null) {
            result.add(atom);
        }
        return result;
    }

    /**
     * Convert an array of bytes to a single atom
     * @param bytes the contents of the atom, including metadata
     * @return the converted Atom object
     */
    protected Atom extractAtom(byte[] bytes) {
        try {
            return getNextAtom(new ByteArrayInputStream(bytes));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Atom getNextAtom(InputStream inputStream) throws IOException {
        byte[] metadataBuffer = new byte[4];
        if(inputStream.read(metadataBuffer, 0, 4) < 4) {
            return null;
        }
        int size = ByteBuffer.wrap(metadataBuffer).getInt();
        if(inputStream.read(metadataBuffer, 0, 4) < 4) {
            return null;
        }
        String type = new String(metadataBuffer);
        List<Atom> children = null;
        AtomType atomType = AtomType.fromInternalAtomType(type);
        if (atomType.isContainer()) {
            // current atom is a container, recursively parse its children
            children = getAtomChildren(inputStream, size - 8);
        } else {
            // current atom is a standard data box, ignore the payload
            inputStream.skipNBytes(size - 8);
        }
        return Atom.builder()
                .sizeInBytes(size)
                .type(type)
                .description(atomType.description)
                .children(children)
                .build();
    }

    private List<Atom> getAtomChildren(InputStream inputStream, long containerSize) throws IOException {
        long bytesParsed = 0;
        boolean childrenRemaining = containerSize > bytesParsed;
        List<Atom> children = new ArrayList<>();
        Atom atom;
        while (childrenRemaining && ((atom = getNextAtom(inputStream)) != null)) {
            children.add(atom);
            bytesParsed += atom.getSizeInBytes();
            childrenRemaining = containerSize > bytesParsed;
        }
        return children;
    }
}
