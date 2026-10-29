
package konyvtar;

import java.io.IOException;
import java.nio.file.Path;

public interface KonyvtarTar {

    void ment(Konyvtar konyvtar, Path fajl) throws IOException;

    Konyvtar betolt(Path fajl) throws IOException;
}
