package tests;

import com.ols.ruslan.neo.Tester;

import org.junit.Before;
import org.junit.Test;

import javax.xml.parsers.ParserConfigurationException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class ResultTest {
    private static Tester tester;

    @Before
    public void init() {
        try {
            tester = new Tester();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test1() {

        byte[] record = tester.getSourceByteArray("file.xml");
        String transformedRecord = tester.transform(record);

        try {
            assertEquals(transformedRecord.replaceAll("\n", ""), String.join("", Files.readAllLines(Paths.get("src/main/resources/bibtex.txt"))));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
