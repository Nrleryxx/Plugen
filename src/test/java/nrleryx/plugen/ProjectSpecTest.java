package nrleryx.plugen;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProjectSpecTest {

    private ProjectSpec spec(String mc) {
        return new ProjectSpec("TestPlugin", "me.test", "tester", "paper", mc, "maven", false, false, false, false, false);
    }

    @Test
    void apiVersionStripsPatchNumber() {
        assertEquals("1.21", spec("1.21.4").apiVersion());
        assertEquals("1.20", spec("1.20.6").apiVersion());
    }

    @Test
    void apiVersionKeepsTwoPartVersions() {
        assertEquals("1.21", spec("1.21").apiVersion());
        assertEquals("1.20", spec("1.20").apiVersion());
    }

    @Test
    void javaReleaseIs17Before1205() {
        assertEquals(17, spec("1.19.4").javaRelease());
        assertEquals(17, spec("1.20").javaRelease());
        assertEquals(17, spec("1.20.4").javaRelease());
    }

    @Test
    void javaReleaseIs21From1205On() {
        assertEquals(21, spec("1.20.5").javaRelease());
        assertEquals(21, spec("1.20.6").javaRelease());
        assertEquals(21, spec("1.21").javaRelease());
        assertEquals(21, spec("1.21.9").javaRelease());
        assertEquals(21, spec("1.22").javaRelease());
    }

    @Test
    void defaultPackageCleansAuthor() {
        assertEquals("me.Nrleryx", ProjectSpec.defaultPackage("Nrleryx"));
        assertEquals("me.weirdname", ProjectSpec.defaultPackage("--weird name--"));
        assertNull(ProjectSpec.defaultPackage("123abc"));
        assertNull(ProjectSpec.defaultPackage(""));
    }
}
