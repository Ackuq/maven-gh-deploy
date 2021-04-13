import io.github.ackuq.StringArrayUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class StringArrayUtilsTest {

    @Test
    public void trimTest() {
        String[] testArray = {"abc", " abc ", "  abc", "abc  ", "a b c", " a b c ", " ", "  ", null};
        String[] expected = {"abc", "abc", "abc", "abc", "a b c", "a b c", "", "", null};

        String[] actual = StringArrayUtils.trim(testArray);

        assertArrayEquals(expected, actual);
    }

    @Test
    public void capitalizeTest() {
        String[] testArray = {"abc", " abc ", "Abc", "ABC", "abc  ", "a b c", " a b c ", " ", null};
        String[] expected = {"Abc", " abc ", "Abc", "ABC", "Abc  ", "A b c", " a b c ", " ", null};

        String[] actual = StringArrayUtils.capitalize(testArray);

        assertArrayEquals(expected, actual);
    }

    @Test
    public void toLowerCaseTest() {
        String[] testArray = {"Abc", " Abc ", "aBc", "ABC", "abc  ", "A B C", " a b C ", " ", null};
        String[] expected = {"abc", " abc ", "abc", "abc", "abc  ", "a b c", " a b c ", " ", null};

        String[] actual = StringArrayUtils.toLowerCase(testArray);

        assertArrayEquals(expected, actual);
    }

    @Test
    public void toUpperCaseTest() {
        String[] testArray = {"abc", " Abc ", "aBc", "ABC", "aBC  ", "A B C", " a b C ", " ", null};
        String[] expected = {"ABC", " ABC ", "ABC", "ABC", "ABC  ", "A B C", " A B C ", " ", null};

        String[] actual = StringArrayUtils.toUpperCase(testArray);

        assertArrayEquals(expected, actual);
    }
}
