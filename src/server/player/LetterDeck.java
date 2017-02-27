package server.player;

/**
 * Колода карт
 */
public interface LetterDeck {

    /**
     * Извлечение карт из колоды
     * После каждого хода необходимо добирать карточки из голоды если их меньше 7
     */
    Character poll();
}
