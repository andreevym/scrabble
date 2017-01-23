package server.player;

/**
 * Колода карт
 */
public interface LetterDeck {

    /**
     * Для загрузки букв 104
     */
    void generateLetterDeck();

    /**
     * Извлечение карт из колоды
     * После каждого хода необходимо добирать карточки из голоды если их меньше 7
     */
    Character poll();
}