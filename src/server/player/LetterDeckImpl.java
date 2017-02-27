package server.player;

import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Колода карт
 */
public class LetterDeckImpl implements LetterDeck {

    private static CopyOnWriteArrayList<Character> letterDeck = new CopyOnWriteArrayList<>();

    /**
     * Инициализация колоды
     */
    public LetterDeckImpl() {
        fill();
    }

    /**
     * Извлечение карт из колоды
     * После каждого хода необходимо добирать карточки из голоды если их меньше 7
     *
     * TODO обработка конца игры
     * @return
     * @throws IndexOutOfBoundsException
     */
    @Override
    public Character poll() throws IndexOutOfBoundsException {
        return letterDeck.remove(0);
    }

    /**
     * Заполняем колоду карт
     * Всего 104 карточки с буками
     */
    private void fill() {
        System.out.println("Добавить буквы в колоду карт 'Letter Deck'");
        for (LetterEnum letterEnum : LetterEnum.values()) {
            for (int i = 0; i < letterEnum.getCount(); i++) {
                letterDeck.add(letterEnum.getLetter());
            }
        }

        Collections.shuffle(letterDeck);

        System.out.println("Буквы в колоде: " + letterDeck);
        System.out.println("Колличество букв в колоде: " + letterDeck.size());
    }
}
