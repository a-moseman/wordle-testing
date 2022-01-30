import java.util.ArrayList;
import java.util.Random;

public class SimulatorWorker extends Thread {
    private ArrayList<String> wordList;
    private ArrayList<Character> wrongLetters;
    private ArrayList<Character> correctLetters;
    private String[] correctPlacements;
    private Random random;
    private int start;
    private double[] scores;

    private final int SIM_COUNT;

    public SimulatorWorker(ArrayList<String> wordList, int start, double[] scores, int simCount) {
        this.wordList = wordList;
        this.wrongLetters = new ArrayList<>();
        this.correctLetters = new ArrayList<>();
        this.correctPlacements = new String[5];
        this.random = new Random();
        this.start = start;
        this.scores = scores;
        this.SIM_COUNT = simCount;
    }

    private void reset() {
        wrongLetters.clear();
        correctLetters.clear();
        correctPlacements = new String[5];
    }

    private String randomWord(ArrayList<String> words) {
        return words.get(random.nextInt(words.size()));
    }

    private boolean checkWord(String word, String wordle) {
        for (int i = 0; i < 5; i++) {
            if (wordle.charAt(i) == word.charAt(i)) {
                correctPlacements[i] = "" + word.charAt(i);
            } else if (wordle.contains("" + word.charAt(i))) {
                correctLetters.add(word.charAt(i));
            } else {
                wrongLetters.add(word.charAt(i));
            }
        }
        return word.equals(wordle);
    }

    private ArrayList<String> filteredWordList() {
        ArrayList<String> filteredWordList = new ArrayList<>();
        for (String word : wordList) {
            // Filter out words that are missing determined letter placements
            boolean flag = true;
            for (int i = 0; i < 5; i++) {
                if (correctPlacements[i] != null && !correctPlacements[i].equals("" + word.charAt(i))) {
                    flag = false;
                }
            }
            if (!flag) {
                continue;
            }

            // Filter out words that do not contain confirmed letters
            boolean containsLetter = true;
            for (Character letter : correctLetters) {
                if (!word.contains("" + letter)) {
                    containsLetter = false;
                    break;
                }
            }
            if (!containsLetter) {
                continue;
            }

            // Filter out words that contain a wrong letter
            boolean containsWrongLetter = false;
            for (Character letter : wrongLetters) {
                if (word.contains("" + letter)) {
                    containsWrongLetter = true;
                }
            }
            if (containsWrongLetter) {
                continue;
            }

            filteredWordList.add(word);
        }
        return filteredWordList;
    }

    private int simulate(String firstWord) {
        reset();
        ArrayList<String> words = wordList;
        String wordle = randomWord(words);
        String word = firstWord;
        int score = 6;
        for (int i = 0; i < 6; i++) {
            if (checkWord(word, wordle)) {
                break;
            }
            words = filteredWordList();
            word = randomWord(words);
            score--;
        }
        return score;
    }

    public void run() {
        for (int i = 0; i < wordList.size(); i++) {
            String word = wordList.get(i);
            for (int j = 0; j < SIM_COUNT; j++) {
                scores[i + start] += (double) simulate(word) / SIM_COUNT;
            }
        }
    }
}
