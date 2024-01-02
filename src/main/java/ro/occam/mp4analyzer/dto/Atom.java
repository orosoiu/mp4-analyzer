package ro.occam.mp4analyzer.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class Atom {
    private String type;                // atom type identifier as contained in the mp4 file
    private String description;         // human-readable description of the atom type
    private int sizeInBytes;            // size of the atom box in bytes
    private List<Atom> children;        // list of atom children, only for container boxes
}
