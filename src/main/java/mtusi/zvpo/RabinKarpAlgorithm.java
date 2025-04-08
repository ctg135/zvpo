package mtusi.zvpo;

import mtusi.zvpo.entites.SignatureEntity;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;

public class RabinKarpAlgorithm {

    // Простое число для хеширования
    private final int MOD = 1695191;
    // Алфавит значений байта
    private final int BASE = 255;
    // Длина окна
    private final int M = 8;

    public int getHash(
            String value,
            int iterations){
        // Высчитывает хеш от строки (для firstBytes)
        int hash = 0;
        for (int i = 0; i < iterations; i++){
            hash = ((hash * BASE) + value.charAt(i)) % MOD;
        }
        return hash;
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
            int oldHash,
            char oldByte,
            char newByte){
        // Пересчитывает хеш для окна смещения
        int result = ((oldHash - oldByte * BASE) + newByte) % MOD;
        if (result < 0)
            return result + MOD;
        else
            return result;
    }

    public List<SignatureEntity> scanString(
            String text,
            List<SignatureEntity> signatures
    ){
        // Переменная размерности смещения
        int m = (text.length() < 8) ? text.length() : M;

        // Спискок найденных сигнатур
        List<SignatureEntity> founded = new LinkedList<>();

        String currentText = text.substring(0, m);
        int rollingHash = getHash(currentText, m);

        for (int i = 0; i <= text.length() - m; i++)
        {
            for (SignatureEntity signature : signatures)
            {
                // Если не совпал хеш, ищем совпадения дальше
                if (rollingHash != signature.firstBytesHash)
                    continue;
                // Если не совпали первые байты, то ищем совпадения дальше
                if (!currentText.equals(signature.firstBytes))
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

            if (i < text.length() - m){
                currentText = text.substring(i + 1, i + 1 + m);
                // TODO: Не работает оптимизация при помощи rollingHash
                //  Пока хеш считается в m итераций
                rollingHash = getHash(currentText, m);
//                rollingHash = getRollingHash(
//                        rollingHash,
//                        currentText.charAt(0),
//                        currentText.charAt(m - 1));
            }
        }

        return founded;
    }

    public SignatureEntity extractSignature(
        String text
    ){
        // Преобразует полученную строку в сигнатуру

        if (text.length() <= M) {
            SignatureEntity signature = new SignatureEntity();

            signature.firstBytes = text;
            signature.firstBytesHash = getHash(text, text.length());
            signature.remainderHash = getStringHash("");
            signature.remainderLength = 0;
            signature.offsetStart = 0;
            signature.offsetEnd = 0;

            return signature;
        }

        String firstBytes = text.substring(0, M);
        String remainder = text.substring(M);

        SignatureEntity signature = new SignatureEntity();

        signature.firstBytes = firstBytes;
        signature.firstBytesHash = getHash(firstBytes, M);
        signature.remainderHash = getStringHash(remainder);
        signature.remainderLength = remainder.length();
        signature.offsetStart = 0;
        signature.offsetEnd = 0;

        return signature;
    }
}
