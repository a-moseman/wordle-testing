import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Script {
    public static void main(String[] args) {
        ArrayList<String> wordList = new ArrayList<>();
        try {
            File file = new File("./words.txt");
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                wordList.add(scanner.nextLine());
            }
            scanner.close();
        } catch (FileNotFoundException e) {
        }
        System.out.println("Finished loading data");

        double[] scores = runSim(wordList);

        System.out.println("Writing to file");
        String text = "";
        for (int i = 0; i < wordList.size(); i++) {
            text += wordList.get(i) + ", " + scores[i] + "\n";
        }
        try {
            File file = new File("results.csv");
            file.createNewFile();
            FileWriter fileWriter = new FileWriter("results.csv");
            fileWriter.write(text);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static double[] runSim(ArrayList<String> wordList) {
        double[] scores = new double[wordList.size()];
        int NUM_THREADS = 100;
        int SIM_COUNT = 5000;
        int wordsPerThread = wordList.size() / NUM_THREADS;
        ArrayList<SimulatorWorker> workers = new ArrayList<>();

        for (int i = 0; i < NUM_THREADS; i++) {
            ArrayList<String> wordSubList = new ArrayList<>();
            for (int j = wordsPerThread * i; j < wordsPerThread * (i + 1); j++) {
                wordSubList.add(wordList.get(j));
            }
            if (i == NUM_THREADS - 1) {
                for (int j = wordsPerThread * NUM_THREADS; j < wordList.size(); j++) {
                    wordSubList.add(wordList.get(j));
                }
            }

            SimulatorWorker worker = new SimulatorWorker(wordSubList, i * wordsPerThread, scores, SIM_COUNT);
            workers.add(worker);
        }

        System.out.println("Workers built");

        for (int i = 0; i < NUM_THREADS; i++) {
            workers.get(i).start();
        }
        System.out.println("Threads started");

        for (int i = 0; i < NUM_THREADS; i++) {
            try {
                workers.get(i).join();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("Threads finished");

        int best = 0;
        for (int i = 0; i < scores.length; i++) {
            if (scores[best] < scores[i]) {
                best = i;
            }
        }
        System.out.println("Best word: " + wordList.get(best) + " | " + scores[best]);
        return scores;
    }
}
