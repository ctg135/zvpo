package mtusi.zvpo;

import mtusi.zvpo.entites.SignatureEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestRabinKarp {

    private List<SignatureEntity> signatures;
    private RabinKarpAlgorithm algorithm;

    public TestRabinKarp() {
        signatures = new ArrayList<>();

        algorithm = new RabinKarpAlgorithm();

        var normalSignature = algorithm.extractSignature("test-normal-string");
        normalSignature.threatName = "normal";
        signatures.add(normalSignature);

        var digitSignature = algorithm.extractSignature("111832732671696132692367132");
        digitSignature.threatName = "full-digit";
        signatures.add(digitSignature);

        var eightSymbolsSignature = algorithm.extractSignature("eight888");
        eightSymbolsSignature.threatName = "8-char";
        signatures.add(eightSymbolsSignature);

        var shortSignature = algorithm.extractSignature("short");
        shortSignature.threatName = "short";
        signatures.add(shortSignature);

        var identicalSignature = algorithm.extractSignature("0000000000");
        identicalSignature.threatName = "ten-zeros";
        signatures.add(identicalSignature);
    }

    @Test
    void addedSignatures() {
        // Проверка количества добавленных сигнатур
        assertEquals(5, signatures.toArray().length);
    }

    @Test
    void findNormal() {
        var result = algorithm.scanString("test-normal-string", signatures);
        assertEquals(1, result.toArray().length);
        assertEquals("normal", ((SignatureEntity)result.toArray()[0]).threatName);
        assertEquals(0, ((SignatureEntity)result.toArray()[0]).offsetStart);

        result = algorithm.scanString("asdtest-normal-string", signatures);
        assertEquals(1, result.toArray().length);
        assertEquals("normal", ((SignatureEntity)result.toArray()[0]).threatName);
        assertEquals(3, ((SignatureEntity)result.toArray()[0]).offsetStart);

        result = algorithm.scanString("test-normal-string1233", signatures);
        assertEquals(1, result.toArray().length);
        assertEquals("normal", ((SignatureEntity)result.toArray()[0]).threatName);
        assertEquals(0, ((SignatureEntity)result.toArray()[0]).offsetStart);

        result = algorithm.scanString("ds*adastest-normal-stringdad/asf", signatures);
        assertEquals(1, result.toArray().length);
        assertEquals("normal", ((SignatureEntity)result.toArray()[0]).threatName);
        assertEquals(7, ((SignatureEntity)result.toArray()[0]).offsetStart);

        result = algorithm.scanString("test-normal-strin", signatures);
        assertEquals(0, result.toArray().length);

        result = algorithm.scanString("est-normal-string", signatures);
        assertEquals(0, result.toArray().length);

        result = algorithm.scanString("test-string", signatures);
        assertEquals(0, result.toArray().length);
    }

    @Test
    void findDigit() {
        var result = algorithm.scanString("111832732671696132692367132", signatures);
        assertEquals("full-digit", ((SignatureEntity)result.toArray()[0]).threatName);
        assertEquals(0, ((SignatureEntity)result.toArray()[0]).offsetStart);
        assertEquals(1, result.toArray().length);

        result = algorithm.scanString("pop111832732671696132692367132", signatures);
        assertEquals("full-digit", ((SignatureEntity)result.toArray()[0]).threatName);
        assertEquals(1, result.toArray().length);
        assertEquals("full-digit", ((SignatureEntity)result.toArray()[0]).threatName);
        assertEquals(3, ((SignatureEntity)result.toArray()[0]).offsetStart);

        result = algorithm.scanString("111832732671696132692367132kfsdjgklahj", signatures);
        assertEquals(1, result.toArray().length);
        assertEquals("full-digit", ((SignatureEntity)result.toArray()[0]).threatName);
        assertEquals(0, ((SignatureEntity)result.toArray()[0]).offsetStart);

        result = algorithm.scanString("1pop111832732671696132692367132 jfhdsjkg", signatures);
        assertEquals(1, result.toArray().length);
        assertEquals("full-digit", ((SignatureEntity)result.toArray()[0]).threatName);
        assertEquals(4, ((SignatureEntity)result.toArray()[0]).offsetStart);

        result = algorithm.scanString("1118327326716961326923671", signatures);
        assertEquals(0, result.toArray().length);

        result = algorithm.scanString("11183273", signatures);
        assertEquals(0, result.toArray().length);
    }

    @Test
    void findEight() {
        var result = algorithm.scanString("eight888", signatures);
        assertEquals("8-char", ((SignatureEntity)result.toArray()[0]).threatName);
        assertEquals(0, ((SignatureEntity)result.toArray()[0]).offsetStart);
        assertEquals(1, result.toArray().length);

        result = algorithm.scanString("888eight888", signatures);
        assertEquals("8-char", ((SignatureEntity)result.toArray()[0]).threatName);
        assertEquals(3, ((SignatureEntity)result.toArray()[0]).offsetStart);
        assertEquals(1, result.toArray().length);

        result = algorithm.scanString("eight888888", signatures);
        assertEquals("8-char", ((SignatureEntity)result.toArray()[0]).threatName);
        assertEquals(0, ((SignatureEntity)result.toArray()[0]).offsetStart);
        assertEquals(1, result.toArray().length);

        result = algorithm.scanString("88888888", signatures);
        assertEquals(0, result.toArray().length);

        result = algorithm.scanString("das", signatures);
        assertEquals(0, result.toArray().length);
    }

    @Test
    void findShort() {
        var result = algorithm.scanString("short", signatures);
        assertEquals(1, result.toArray().length);

        result = algorithm.scanString("short1", signatures);
        assertEquals(0, result.toArray().length);

        result = algorithm.scanString("ashort", signatures);
        assertEquals(0, result.toArray().length);

        result = algorithm.scanString("short   ", signatures);
        assertEquals(0, result.toArray().length);

        result = algorithm.scanString("________________short", signatures);
        assertEquals(0, result.toArray().length);
    }

    @Test
    void findZeros() {
        var result = algorithm.scanString("0000000000", signatures);
        assertEquals("ten-zeros", ((SignatureEntity)result.toArray()[0]).threatName);
        assertEquals(0, ((SignatureEntity)result.toArray()[0]).offsetStart);
        assertEquals(1, result.toArray().length);

        result = algorithm.scanString("00000000000", signatures);
        assertEquals(2, result.toArray().length);
        assertEquals("ten-zeros", ((SignatureEntity)result.toArray()[0]).threatName);
        assertEquals(1, ((SignatureEntity)result.toArray()[1]).offsetStart);

        result = algorithm.scanString("asdasdasdasd0000000000asdasdasd", signatures);
        assertEquals(1, result.toArray().length);
        assertEquals("ten-zeros", ((SignatureEntity)result.toArray()[0]).threatName);
        assertEquals(12, ((SignatureEntity)result.toArray()[0]).offsetStart);
    }

    @Test
    void findSome() {
        var result = algorithm.scanString("test-normal-string__short__eight888", signatures);
        assertEquals(2, result.toArray().length);
        assertEquals("normal", ((SignatureEntity)result.toArray()[0]).threatName);
        assertEquals("8-char", ((SignatureEntity)result.toArray()[1]).threatName);

        result = algorithm.scanString("eight888test-normal-string", signatures);
        assertEquals(2, result.toArray().length);
        assertEquals("normal", ((SignatureEntity)result.toArray()[1]).threatName);
        assertEquals("8-char", ((SignatureEntity)result.toArray()[0]).threatName);
    }


}
