package server.player;

import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class LetterDeckImpl implements LetterDeck {


    private static final char FIRST_LETTER = 'а'; // 1072
    private static final char LAST_LETTER = 'я'; // 1103
    private static CopyOnWriteArrayList<Character> letterDeck = new CopyOnWriteArrayList<>();

    public LetterDeckImpl() {
        generateLetterDeck();
    }

    private static char generateLetter() {
        return (char) ThreadLocalRandom.current().nextInt(FIRST_LETTER, LAST_LETTER + 1);
    }

    @Override
    public void generateLetterDeck() {
        System.out.println("fill 'Letter Deck'");
        fill();
        System.out.println(letterDeck);
    }

    /**
     * TODO обработка конца игры
     * @return
     * @throws IndexOutOfBoundsException
     */
    @Override
    public Character poll() throws IndexOutOfBoundsException {
        return letterDeck.remove(0);
    }

    private void fill() {
        for (LetterEnum letterEnum : LetterEnum.values()) {
            for (int i = 0; i < letterEnum.getCount(); i++) {
                letterDeck.add(letterEnum.getLetter());
            }
        }
        Collections.shuffle(letterDeck);

        System.out.printf("Deck of %s letters was created\n", letterDeck.size());
    }
}
