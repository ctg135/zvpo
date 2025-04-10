package mtusi.zvpo;

import mtusi.zvpo.entites.SignatureEntity;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;

public class RabinKarpAlgorithm {

    /*
    TODO:
        1. Сделать второй конструктор класса для задания констант
        2. Написать тесты алгоритма
     */

    // Простое число для хеширования
    private final int MOD = 101;
//    private final int MOD = 1695191;
    // Алфавит значений байта
    private final int BASE = 256;
    // Длина окна
    private final int M = 8;

    public int getFirstHash(String value){
        // Высчитывает хеш от строки (для firstBytes)
        int result = 0;
        for (int i = 0; i < value.length(); i++){
            result = (result * BASE + (byte)value.charAt(i)) % MOD;
        }
        return result < 0 ? result + MOD : result;
    }

    public String getStringHash(String value){
        // Для расчета хеша строки
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public int getRollingHash(
            int prevHash,
            char firstByte,
            char lastByte,
            int h){
        // Пересчитывает хеш для окна смещения
//        int result = (BASE * (prevHash - firstByte * h) + lastByte) % MOD;
        // haystackHash = (d * (haystackHash - haystack.charCodeAt(i) * fastHash) + haystack.charCodeAt(i + needleLen)) % prime
        int result = (BASE * (prevHash - (byte)firstByte * h) + (byte)lastByte) % MOD;
        return result < 0 ? result + MOD : result;
    }

    public int initH(int length){
        // Инициализация константы для расчета
        int h = 1;
        for (int i = 0; i < length - 1; i++)
            h = (h * BASE) % MOD;
        return h;
    }

    public List<SignatureEntity> scanString(
            String text,
            List<SignatureEntity> signatures
    ){
        // Спискок найденных сигнатур
        List<SignatureEntity> founded = new LinkedList<>();

        // Переменная размерности окна смещения
        int m = Math.min(text.length(), M);

        // Инициализация константы для расчета
        int h = 1;
        int rollingHash = 0;

        for (int i = 0; i < m - 1; i++) {
            h = (h * BASE) % MOD;
        }

        for (int i = 0; i < m; i++) {
            rollingHash = (BASE * rollingHash + (byte)text.charAt(i)) % MOD;
        }

        for (int i = 0; i <= text.length() - m; i++){
            for (SignatureEntity signature : signatures)
            {
                // Если не совпал хеш, ищем совпадения дальше
                if (rollingHash != signature.firstBytesHash)
                    continue;
                // Если не совпали первые байты, то ищем совпадения дальше
                if (!text.substring(i, i + m).equals(signature.firstBytes))
                    continue;
                // Если длинны хвоста хватает, то хвост точно не совпадает, ищем дальше
                if (text.substring(i + m).length() < signature.remainderLength)
                    continue;
                // Получение "хвоста" проверяемой строки
                String remainder = text.substring(i + m, i + m + signature.remainderLength);
                var remainderHash = getStringHash(remainder);
                // Если не совпадает хеш хвоста, то ищем дальше
                if (!remainderHash.equals(signature.remainderHash))
                    continue;
                // Если хеш совпал, то дописываем найденные смещения в результат
                signature.offsetStart = i;
                signature.offsetEnd = i + m + signature.remainderLength;
                founded.add(signature);
            }

            if (i < text.length() - m) {
                // haystackHash = (d * (haystackHash - haystack.charCodeAt(i) * fastHash) + haystack.charCodeAt(i + needleLen)) % prime
                rollingHash = (BASE * (rollingHash - (byte)text.charAt(i) * h) + (byte)text.charAt(i + m)) % MOD;

                if (rollingHash < 0)
                    rollingHash += MOD;
            }

        }

        return founded;


//        String currentText = text.substring(0, m);
//
//        for (int i = 0; i <= text.length() - m; i++)
//        {
//            for (SignatureEntity signature : signatures)
//            {
//                // Если не совпал хеш, ищем совпадения дальше
//                if (rollingHash != signature.firstBytesHash)
//                    continue;
//                // Если не совпали первые байты, то ищем совпадения дальше
//                if (!currentText.equals(signature.firstBytes))
//                    continue;
//                // Если длинны хвоста хватает, то хвост точно не совпадает, ищем дальше
//                if (text.substring(i + m).length() < signature.remainderLength)
//                    continue;
//                // Получение "хвоста" проверяемой строки
//                String remainder = text.substring(i + m, i + m + signature.remainderLength);
//                var remainderHash = getStringHash(remainder);
//                // Если не совпадает хеш хвоста, то ищем дальше
//                if (!remainderHash.equals(signature.remainderHash))
//                    continue;
//                // Если хеш совпал, то дописываем найденные смещения в результат
//                signature.offsetStart = i;
//                signature.offsetEnd = i + m + signature.remainderLength;
//                founded.add(signature);
//            }
//
//            if (i < text.length() - m){
//                // haystackHash = (d * (haystackHash - haystack.charCodeAt(i) * fastHash) + haystack.charCodeAt(i + needleLen)) % prime
//                currentText = text.substring(i, i + m);
//                //  Пока хеш считается в m итераций
////                rollingHash = getFirstHash(currentText);
//                rollingHash = getRollingHash(
//                        rollingHash,
//                        currentText.charAt(0),
//                        currentText.charAt(m - 1),
//                        h);
//            }
//        }
//
//        return founded;
    }

    public SignatureEntity extractSignature(
        String text
    ){
        // Преобразует полученную строку в сигнатуру

        if (text.length() <= M) {
            SignatureEntity signature = new SignatureEntity();

            signature.firstBytes = text;
            signature.firstBytesHash = getFirstHash(text);
            signature.remainderHash = getStringHash("");
            signature.remainderLength = 0;
            signature.offsetStart = 0;
            signature.offsetEnd = text.length();

            return signature;
        }

        String firstBytes = text.substring(0, M);
        String remainder = text.substring(M);

        SignatureEntity signature = new SignatureEntity();

        signature.firstBytes = firstBytes;
        signature.firstBytesHash = getFirstHash(firstBytes);
        signature.remainderHash = getStringHash(remainder);
        signature.remainderLength = remainder.length();
        signature.offsetStart = 0;
        signature.offsetEnd = text.length();

        return signature;
    }
}
