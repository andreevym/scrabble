package server.player;

import static server.player.LetterEnum.Type.*;

public enum LetterEnum {
    а('а', 8, 1, VOWEL),
    б('б', 2, 3, CONSONANTS),
    в('в', 4, 1, CONSONANTS),
    г('г', 2, 3, CONSONANTS),
    д('д', 4, 2, CONSONANTS),
    е('е', 8, 1, VOWEL),
    ё('ё', 1, 3, VOWEL),
    ж('ж', 1, 5, CONSONANTS),
    з('з', 2, 5, CONSONANTS),
    и('и', 5, 1, VOWEL),
    й('й', 1, 4, CONSONANTS),
    к('к', 4, 2, CONSONANTS),
    л('л', 4, 2, CONSONANTS),
    м('м', 3, 2, CONSONANTS),
    н('н', 5, 1, CONSONANTS),
    о('о', 10, 1, VOWEL),
    п('п', 4, 2, CONSONANTS),
    р('р', 5, 1, CONSONANTS),
    с('с', 5, 1, CONSONANTS),
    т('т', 5, 1, CONSONANTS),
    у('у', 4, 2, VOWEL),
    ф('ф', 1, 10, CONSONANTS),
    х('х', 1, 5, CONSONANTS),
    ц('ц', 1, 5, CONSONANTS),
    ч('ч', 1, 5, CONSONANTS),
    ш('ш', 1, 8, CONSONANTS),
    щ('щ', 1, 10, CONSONANTS),
    ъ('ъ', 1, 10, CONSONANTS),
    ы('ы', 2, 4, VOWEL),
    ь('ь', 2, 3, CONSONANTS),
    э('э', 1, 8, VOWEL),
    ю('ю', 1, 8, VOWEL),
    я('я', 2, 3, VOWEL),
    empty('*', 2, 0, ANY); // специальный символ обозначает любую букву

    private final char letter;
    private final int count;
    private final int cost;
    private final Type type;

    LetterEnum(char letter, int count, int cost, Type type) {
        this.cost = cost;
        this.count = count;
        this.letter = letter;
        this.type = type;
    }

    public char getLetter() {
        return letter;
    }

    public int getCount() {
        return count;
    }

    public int getCost() {
        return cost;
    }
    
    public enum Type {
        VOWEL,
        CONSONANTS,
        ANY
    }
}
