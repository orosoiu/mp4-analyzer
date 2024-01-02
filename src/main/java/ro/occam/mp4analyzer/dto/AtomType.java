package ro.occam.mp4analyzer.dto;

import java.util.Set;

public enum AtomType {
    MOOF("Movie Fragment Box"),
    MFHD("Movie Fragment Header Box"),
    TRAF("Track Fragment Box"),
    TFHD("Track Fragment Header Box"),
    TRUN("Track Fragment Run Box"),
    UUID("User Defined Box"),
    MDAT("Media Data Box"),
    OTHER("Other Atom Box");

    private static final Set<AtomType> CONTAINERS = Set.of(MOOF, TRAF);

    public final String description;

    /**
     * Verifies whether a specific atom is a container, meaning it will only
     * contain other atoms and no data payload
     *
     * @return true is atom is a container, false otherwise
     */
    public boolean isContainer() {
        return CONTAINERS.contains(this);
    }

    /**
     * Maps the internal atom type as represented in the mp4 file to an AtomType instance
     * If no match is found we assume the atom is of generic type OTHER
     *
     * @param internalAtomType the atom type as represented in the mp4 file
     * @return the mapped atom type
     */
    public static AtomType fromInternalAtomType(String internalAtomType) {
        try {
            return AtomType.valueOf(internalAtomType.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return OTHER;
        }
    }

    AtomType(String description) {
        this.description = description;
    }
}
