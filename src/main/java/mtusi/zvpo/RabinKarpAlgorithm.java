package mtusi.zvpo;

import mtusi.zvpo.entites.SignatureEntity;

import java.util.List;

public class RabinKarpAlgorithm {

    // Простое число для хеширования
    private final int MOD = 1695191;
    // Алфавит значений байта
    private final int BASE = 255;
    // Длина окна
    private final int M = 8;

    /*
    TODO:
     Основное
        0. Поиск в файле совпадение на сигнатуру
        1. Функция для поиска хеша в строке/байтах
        2. Добавить в сигнатуру хеш от первых байт, чтоб по ним искать
     Пригодятся
        1. Функция вычисления хеша из строки/байт одного окна
        2. Функия вычисления следующего хеша
        3. Константы
            - MOD: Простое число
            - BASE: обычно 255 (из-за значений байта)
            - Длина окна m
     */

    public int getHash(
            String value,
            int iteration){
        // Высчитывает хеш от строки
        int hash = 0;
        for (int i = 0; i < iteration; i++){
            hash = ((hash * BASE) + value.charAt(i)) % MOD;
        }
        return hash;
    }

    public int getRollingHash(
            int oldHash,
            char oldByte,
            char newByte){
        // Пересчитывает хеш для смещения
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
        /*
        Алгоритм работы:
        1. Получаем хеши всех сигнатур от окна (первых восьми байт)
        2. Получаем хеш первого окна строки
        3. Цикл по строке:
            Сравниваем хеш
               Если хеш совпадает с кем-нибудь из списка
                Проверяем что первые 8 байт равны
                Проверяем что хвосты равны по хешу
                Если все ок, то записываем, что одна сигнтура совпала
                TODO: Как правильно получить хеш хвоста?
                 Например, первые 8 байт одинаковые, а остальная часть файла разная
                 то что он найдет как хвост и до какого размера?
                 А понял, он возьмет со строки хвост длины сигнатуры и сравнит его хеш
                 с хешем хвоста от сигнатуры
               Если хеши не совпали
                Считаем хеш для следующего окна (rollingHash)
        4. Возвращаем список сигнатур, которые совпали

         */

    }
}
