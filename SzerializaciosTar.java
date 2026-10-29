
package konyvtar;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class SzerializaciosTar implements KonyvtarTar {

    @Override
    public void ment(Konyvtar konyvtar, Path fajl) throws IOException {
        Path cel = fajl.toAbsolutePath();
        Path mappa = cel.getParent();

        if (mappa != null) {
            Files.createDirectories(mappa);
        }

        Path ideiglenes = Files.createTempFile(
                mappa, "konyvtar-", ".tmp");

        try {
            try (ObjectOutputStream ki = new ObjectOutputStream(
                    Files.newOutputStream(ideiglenes))) {
                ki.writeObject(konyvtar);
                ki.flush();
            }

            atnevez(ideiglenes, cel);
        } finally {
            Files.deleteIfExists(ideiglenes);
        }
    }

    @Override
    public Konyvtar betolt(Path fajl) throws IOException {
        if (!Files.exists(fajl)) {
            throw new IOException("A mentesi fajl nem letezik: " + fajl);
        }

        try (ObjectInputStream be = new ObjectInputStream(
                Files.newInputStream(fajl))) {

            Object adat = be.readObject();

            if (!(adat instanceof Konyvtar konyvtar)) {
                throw new IOException(
                        "A fajl nem konyvtari adatokat tartalmaz.");
            }

            return konyvtar;
        } catch (ClassNotFoundException | RuntimeException e) {
            throw new IOException(
                    "A szerializalt fajl serult vagy nem olvashato.", e);
        }
    }

    static void atnevez(Path ideiglenes, Path cel) throws IOException {
        try {
            Files.move(ideiglenes, cel,
                    StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(ideiglenes, cel,
                    StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
