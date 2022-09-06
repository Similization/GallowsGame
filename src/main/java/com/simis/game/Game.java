package com.simis.game;

import com.simis.entity.Word;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.*;

import static com.simis.HibernateRunner.getTableRowCount;

public class Game {
    private String word;
    private int guessedLettersCount;
    private int[] guessedChars;

    public String createHiddenWord() {
        guessedLettersCount = 0;
        StringBuilder sb = new StringBuilder();

        for (var ch : word.toCharArray()) {
            if (guessedChars[ch - 'a'] == 1) {
                sb.append(ch);
                ++guessedLettersCount;
            } else {
                sb.append('_');
            }
        }
        return sb.toString();
    }

    public void generateNewWord() {
        // take new word from database
        Configuration config = new Configuration();
        config.addAnnotatedClass(Word.class);
        config.configure();

        try (SessionFactory sessionFactory = config.buildSessionFactory();
             Session session = sessionFactory.openSession()
        ) {
            session.beginTransaction();
            int randomId = (int) (Math.random() * getTableRowCount(Word.class));
            word = session.get(Word.class, randomId).getName().strip();
            session.getTransaction().commit();
        }
        // take new word from array ()
        // word = getNewWord();
    }

    public void start() {
        int countOfTries = 7;
        generateNewWord();
        guessedChars = new int[26];
        guessedLettersCount = 0;
        System.out.println("Tries left: " + countOfTries + ", current guess: " + "_".repeat(word.length()));

        Scanner sc = new Scanner(System.in);
        while (countOfTries != 0) {
            // get guess word
            char guess = Character.toLowerCase(sc.next().charAt(0));
            if (!Character.isLetter(guess) || guessedChars[guess - 'a'] == 1) {
                System.out.println("You have already entered such letter");
                System.out.println("Tries left: " + countOfTries + ", current guess: " + createHiddenWord());
                continue;
            }
            // check
            guessedChars[guess - 'a'] = 1;
            // return some result
            var prevGuessedLettersCount = guessedLettersCount;
            String hiddenWord = createHiddenWord();
            if (Objects.equals(hiddenWord, word)) {
                break;
            }
            if (prevGuessedLettersCount == guessedLettersCount) {
                --countOfTries;
            }
            System.out.println("Tries left: " + countOfTries + ", current guess: " + hiddenWord);
        }
        if (countOfTries == 0) {
            System.out.println("Nice try, but you didn't guess correct word, it was: " + word);
        } else {
            System.out.println("Congratulations, your guess was correct, the word was: " + word);
        }
    }

    public static void main(String[] args) {
        Game game = new Game();
        Scanner sc = new Scanner(System.in);
        String result;
        do {
            game.start();
            System.out.println("Type NO if you don't want to exit");
            result = sc.next();
        } while (Objects.equals(result, "NO"));
        sc.close();
    }
}
