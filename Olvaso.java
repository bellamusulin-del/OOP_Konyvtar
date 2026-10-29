package konyvtar;

import java.io.Serializable;
import java.util.Objects;

public class Olvaso implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String olvasojegySzam;
    private final String nev;

    public Olvaso(String olvasojegySzam, String nev) {
        if (olvasojegySzam == null || olvasojegySzam.isBlank()) {
            throw new IllegalArgumentException(
                    "Az olvasojegy szama kotelezo.");
        }
        if (nev == null || nev.isBlank()) {
            throw new IllegalArgumentException("A nev kotelezo.");
        }

        this.olvasojegySzam = olvasojegySzam.strip();
        this.nev = nev.strip();
    }

    public String getOlvasojegySzam() {
        return olvasojegySzam;
    }

    public String getNev() {
        return nev;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Olvaso masik)) {
            return false;
        }
        return olvasojegySzam.equals(masik.olvasojegySzam);
    }

    @Override
    public int hashCode() {
        return Objects.hash(olvasojegySzam);
    }

    @Override
    public String toString() {
        return nev + " (olvasojegy: " + olvasojegySzam + ")";
    }
}
