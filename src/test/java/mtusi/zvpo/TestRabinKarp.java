package mtusi.zvpo;

import mtusi.zvpo.entites.SignatureEntity;
import org.junit.jupiter.api.Test;

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

        // Та же сигнатура, только со смещением в 3 байта от начала
        var normalOffsetSignature = algorithm.extractSignature("test-normal-string");
        normalOffsetSignature.offsetStart += 3;
        normalOffsetSignature.offsetEnd += 3;
        normalOffsetSignature.threatName = "normal-with-offset";
        signatures.add(normalOffsetSignature);

        var digitSignature = algorithm.extractSignature("111832732671696132692367132");
        digitSignature.threatName = "full-digit";
        signatures.add(digitSignature);

        var eightSymbolsSignature = algorithm.extractSignature("eight888");
        eightSymbolsSignature.threatName = "8-char";
        signatures.add(eightSymbolsSignature);

        // Та же сигнатура, только со смещением в 10 байт от начала
        var eightOffsetSignature = algorithm.extractSignature("eight888");
        eightOffsetSignature.threatName = "8-char-with-offset";
        eightOffsetSignature.offsetStart += 10;
        eightOffsetSignature.offsetEnd += 10;
        signatures.add(eightOffsetSignature);

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
        assertEquals(7, signatures.toArray().length);
    }

    @Test
    void findNormal() {
        var result = algorithm.scanString("test-normal-string", signatures);
        assertEquals(1, result.toArray().length);
        assertEquals("normal", ((SignatureEntity)result.toArray()[0]).threatName);

        result = algorithm.scanString("**test-normal-string", signatures);
        assertEquals(0, result.toArray().length);

        result = algorithm.scanString("test-normal-string**", signatures);
        assertEquals(1, result.toArray().length);
        assertEquals(
                "normal",
                ((SignatureEntity)result.toArray()[0]).threatName);

        result = algorithm.scanString("**test-normal-string**", signatures);
        assertEquals(0, result.toArray().length);

        result = algorithm.scanString("test-normal", signatures);
        assertEquals(0, result.toArray().length);

        result = algorithm.scanString("test-normal*********", signatures);
        assertEquals(0, result.toArray().length);
    }

    @Test
    void findNormalWithOffset() {
        // Должен найти нормальную сигнатуру только со смещением

        var result = algorithm.scanString("***test-normal-string", signatures);
        assertEquals(1, result.toArray().length);
        assertEquals("normal-with-offset", ((SignatureEntity)result.toArray()[0]).threatName);

        result = algorithm.scanString("***test-normal-string***", signatures);
        assertEquals(1, result.toArray().length);
        assertEquals(
                "normal-with-offset",
                ((SignatureEntity)result.toArray()[0]).threatName);

        result = algorithm.scanString("***test-normal", signatures);
        assertEquals(0, result.toArray().length);

        result = algorithm.scanString("***test-normal*******", signatures);
        assertEquals(0, result.toArray().length);
    }

    @Test
    void findDigit() {
        var result = algorithm.scanString("111832732671696132692367132", signatures);
        assertEquals(1, result.toArray().length);
        assertEquals("full-digit", ((SignatureEntity)result.toArray()[0]).threatName);

        result = algorithm.scanString("***111832732671696132692367132", signatures);
        assertEquals(0, result.toArray().length);

        result = algorithm.scanString("111832732671696132692367132*****", signatures);
        assertEquals(1, result.toArray().length);
        assertEquals("full-digit", ((SignatureEntity)result.toArray()[0]).threatName);

        result = algorithm.scanString("***111832732671696132692367132***", signatures);
        assertEquals(0, result.toArray().length);

        result = algorithm.scanString("11183273", signatures);
        assertEquals(0, result.toArray().length);
    }

    @Test
    void findEight() {
        var result = algorithm.scanString("eight888", signatures);
        assertEquals(1, result.toArray().length);
        assertEquals("8-char", ((SignatureEntity)result.toArray()[0]).threatName);

        result = algorithm.scanString("eight888***", signatures);
        assertEquals(1, result.toArray().length);
        assertEquals("8-char", ((SignatureEntity)result.toArray()[0]).threatName);

        result = algorithm.scanString("***eight888", signatures);
        assertEquals(0, result.toArray().length);

        result = algorithm.scanString("***eight888***", signatures);
        assertEquals(0, result.toArray().length);

        result = algorithm.scanString("eight", signatures);
        assertEquals(0, result.toArray().length);
    }

    @Test
    void findEightWithOffset() {
        var result = algorithm.scanString("**********eight888", signatures);
        assertEquals(1, result.toArray().length);
        assertEquals("8-char-with-offset", ((SignatureEntity)result.toArray()[0]).threatName);

        result = algorithm.scanString("**********eight888*****", signatures);
        assertEquals(1, result.toArray().length);
        assertEquals("8-char-with-offset", ((SignatureEntity)result.toArray()[0]).threatName);

        result = algorithm.scanString("**********eight", signatures);
        assertEquals(0, result.toArray().length);

        result = algorithm.scanString("***************eight888*", signatures);
        assertEquals(0, result.toArray().length);
    }

    @Test
    void findShort() {
        var result = algorithm.scanString("short", signatures);
        assertEquals(1, result.toArray().length);
        assertEquals("short", ((SignatureEntity)result.toArray()[0]).threatName);

        result = algorithm.scanString("short*", signatures);
        assertEquals(0, result.toArray().length);

        result = algorithm.scanString("short***", signatures);
        assertEquals(0, result.toArray().length);

        result = algorithm.scanString("*short", signatures);
        assertEquals(0, result.toArray().length);

        result = algorithm.scanString("***short", signatures);
        assertEquals(0, result.toArray().length);
    }

    @Test
    void findTwoSignatures() {
        var result = algorithm.scanString("eight888**eight888*****", signatures);
        assertEquals(2, result.toArray().length);
        assertEquals("8-char", ((SignatureEntity)result.toArray()[0]).threatName);
        assertEquals("8-char-with-offset", ((SignatureEntity)result.toArray()[1]).threatName);

        result = algorithm.scanString("eight888**eight888", signatures);
        assertEquals(2, result.toArray().length);
        assertEquals("8-char", ((SignatureEntity)result.toArray()[0]).threatName);
        assertEquals("8-char-with-offset", ((SignatureEntity)result.toArray()[1]).threatName);

        result = algorithm.scanString("eight888**eight", signatures);
        assertEquals(1, result.toArray().length);
        assertEquals("8-char", ((SignatureEntity)result.toArray()[0]).threatName);

        result = algorithm.scanString("eight*****eight888", signatures);
        assertEquals(1, result.toArray().length);
        assertEquals("8-char-with-offset", ((SignatureEntity)result.toArray()[0]).threatName);
    }

    @Test
    void easyTest() {
        // Проверка, что не находит в случайной строке совпадения
        var result = algorithm.scanString("qwertyuiopoasdfghjklzxcvbnm", signatures);
        assertEquals(0, result.toArray().length);
    }
}
