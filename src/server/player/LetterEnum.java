package server.player;

public enum LetterEnum {
    а('а', 8, 1),
    б('б', 2, 3),
    в('в', 4, 1),
    г('г', 2, 3),
    д('д', 4, 2),
    е('е', 8, 1),
    ё('ё', 1, 3),
    ж('ж', 1, 5),
    з('з', 2, 5),
    и('и', 5, 1),
    й('й', 1, 4),
    к('к', 4, 2),
    л('л', 4, 2),
    м('м', 3, 2),
    н('н', 5, 1),
    о('о', 10, 1),
    п('п', 4, 2),
    р('р', 5, 1),
    с('с', 5, 1),
    т('т', 5, 1),
    у('у', 4, 2),
    ф('ф', 1, 10),
    х('х', 1, 5),
    ц('ц', 1, 5),
    ч('ч', 1, 5),
    ш('ш', 1, 8),
    щ('щ', 1, 10),
    ъ('ъ', 1, 10),
    ы('ы', 2, 4),
    ь('ь', 2, 3),
    э('э', 1, 8),
    ю('ю', 1, 8),
    я('я', 2, 3);

    private final char letter;
    private final int count;
    private final int cost;

    LetterEnum(char letter, int count, int cost) {
        this.cost = cost;
        this.count = count;
        this.letter = letter;
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
}