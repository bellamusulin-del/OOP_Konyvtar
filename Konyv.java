
package konyvtar;

import java.io.Serializable;
import java.util.Objects;

public class Konyv implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String isbn;
    private final String cim;
    private final String szerzo;
    private final int kiadasEve;
    private final int peldanyszam;

    public Konyv(String isbn, String cim, String szerzo,
                 int kiadasEve, int peldanyszam) {

        if (isbn == null || isbn.isBlank()) {
            throw new IllegalArgumentException("Az ISBN kotelezo.");
        }
        if (cim == null || cim.isBlank()) {
            throw new IllegalArgumentException("A cim kotelezo.");
        }
        if (szerzo == null || szerzo.isBlank()) {
            throw new IllegalArgumentException("A szerzo kotelezo.");
        }
        if (kiadasEve < 0) {
            throw new IllegalArgumentException("Hibas kiadasi ev.");
        }
        if (peldanyszam < 1) {
            throw new IllegalArgumentException(
                    "Legalabb egy peldany szukseges.");
        }

        this.isbn = isbn.strip();
        this.cim = cim.strip();
        this.szerzo = szerzo.strip();
        this.kiadasEve = kiadasEve;
        this.peldanyszam = peldanyszam;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getCim() {
        return cim;
    }

    public String getSzerzo() {
        return szerzo;
    }

    public int getKiadasEve() {
        return kiadasEve;
    }

    public int getPeldanyszam() {
        return peldanyszam;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Konyv masik)) {
            return false;
        }
        return isbn.equals(masik.isbn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isbn);
    }

    @Override
    public String toString() {
        return cim + " - " + szerzo + " (" + kiadasEve
                + "), ISBN: " + isbn + ", peldany: " + peldanyszam;
    }
}

