package guru_qa.parsing;

import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.opencsv.CSVReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


public class FileParsingTest {

    private ClassLoader cl = FileParsingTest.class.getClassLoader();

    @Test
    void readFromPdfFileZip() throws Exception {
        try (InputStream files = cl.getResourceAsStream("samples.zip");
             ZipInputStream zs = new ZipInputStream(files)) {
            ZipEntry entry;
            while ((entry = zs.getNextEntry()) != null) {
                if (entry.getName().equals("lorem_ipsum.pdf")) {
                    PDF pdf = new PDF(zs);
                    Assertions.assertEquals(3, pdf.numberOfPages);
                }
            }
    }
    }

    @Test
    void readFromXlsFileZip() throws Exception {
        try (InputStream files = cl.getResourceAsStream("samples.zip");
             ZipInputStream zs = new ZipInputStream(files)) {
            ZipEntry entry;
            while ((entry = zs.getNextEntry()) != null) {
                if (entry.getName().equals("graph.xls")) {
                    XLS xls = new XLS(zs);
                    Assertions.assertTrue(
                            xls.excel.getSheetAt(0).getRow(16).getCell(91).getStringCellValue()
                                    .startsWith("")
                    );
                }
            }
        }
    }

    @Test
    void readFromCsvFileZip()throws Exception {
        try (InputStream files = cl.getResourceAsStream("samples.zip");
             ZipInputStream zs = new ZipInputStream(files)) {
            ZipEntry entry;
            while ((entry = zs.getNextEntry()) != null) {
                if (entry.getName().equals("cities.csv")) {
                    CSVReader csvReader = new CSVReader(new InputStreamReader(zs));
                    List<String[]> string = csvReader.readAll();
                    Assertions.assertArrayEquals(new String[] {"   41","    5","   59", "N","     80","   39","    0", "W", "Youngstown"," OH"}, string.get(1));
                }
            }
        }
    }


    @Test
    void readFromJsonFile() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream is = cl.getResourceAsStream("quiz.json");
             InputStreamReader isr = new InputStreamReader(is)) {
            Movie movie = mapper.readValue(isr, Movie.class);
            Assertions.assertEquals("It follows", movie.title);
            Assertions.assertEquals("2015", movie.year);
            Assertions.assertEquals("Approved", movie.rated);
            Assertions.assertEquals("25 Jun 2015", movie.released);
            Assertions.assertEquals(132, movie.runtime);
            Assertions.assertEquals(List.of("Horror","Supernatural","Mystery"), movie.genre);
        }
    }
}
