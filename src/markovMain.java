import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class markovMain{
    static final String DEFAULT_INPUT_FILE = "sample.txt"; //can change this in argument line

    public static void main(String[] args) {
        System.out.println("Welcome to the Markov Client");

        String input_file = DEFAULT_INPUT_FILE;
        int prefixLength = 2; //can change this in argument line
        if (args.length > 0) {
            input_file = args[0];
        }
        if (args.length > 1) {
            prefixLength = Integer.parseInt(args[1]);
        }

        System.out.printf("using '%s' as the input file\n", input_file);
        System.out.printf("using prefix length of %d\n", prefixLength);

        ArrayList<String> lines_from_file = null;
        try {
            lines_from_file = readLinesFromFile(input_file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ArrayList<String> words = splitLinesIntoWords(lines_from_file);
        System.out.printf("%s has %d words\n", input_file, words.size());

        HashMap<String, ArrayList<String>> markovMap = trainModel(words, prefixLength);
        String result = generateStory(markovMap, words, prefixLength);
        System.out.println(result);
        System.out.println("Thanks for using the Markov Client");
    }


    private static String generateStory(HashMap<String, ArrayList<String>> markovMap, ArrayList<String> words, int prefixLength) {
        StringBuilder story = new StringBuilder();
        Random random = new Random();

        StringBuilder prefixBuilder = new StringBuilder();
        for (int i = 0; i < prefixLength; i++) {
            if (i > 0) {
                prefixBuilder.append(" ");
            }
            prefixBuilder.append(words.get(i));
        }
        String currentPrefix = prefixBuilder.toString();
        story.append(currentPrefix).append(" ");

        int wordCount = prefixLength;
        int maxWords = 100;  //this is to avoid the infinite loops but i'm not sure if there is another way
        while (markovMap.containsKey(currentPrefix) && wordCount < maxWords) {
            ArrayList<String> nextWords = markovMap.get(currentPrefix);
            if (nextWords.isEmpty()) {
                break;
            }
            String nextWord = nextWords.get(random.nextInt(nextWords.size()));
            story.append(nextWord).append(" ");
            String[] currentPrefixWords = currentPrefix.split(" ");
            StringBuilder newPrefixBuilder = new StringBuilder();
            for (int i = 1; i < currentPrefixWords.length; i++) {
                newPrefixBuilder.append(currentPrefixWords[i]).append(" ");
            }
            newPrefixBuilder.append(nextWord);
            currentPrefix = newPrefixBuilder.toString().trim();
            wordCount++;
        }

        return story.toString();
    }







    private static HashMap<String, ArrayList<String>> trainModel(ArrayList<String> words, int prefixLength) {
        HashMap<String, ArrayList<String>> markovMap = new HashMap<>();
        for (int i = 0; i < words.size() - prefixLength; i++) {
            StringBuilder prefixBuilder = new StringBuilder();
            for (int j = 0; j < prefixLength; j++) {
                if (j > 0) {
                    prefixBuilder.append(" ");
                }
                prefixBuilder.append(words.get(i + j));
            }
            String prefix = prefixBuilder.toString();
            String nextWord = words.get(i + prefixLength);
            markovMap.computeIfAbsent(prefix, k -> new ArrayList<>()).add(nextWord);
        }

        // debugging statement to see the map values
//        for (Map.Entry<String, ArrayList<String>> entry : markovMap.entrySet()) {
//            System.out.println("Key: " + entry.getKey() + " -> Value: " + entry.getValue());
//        }

        return markovMap;
    }



    private static ArrayList<String> splitLinesIntoWords(ArrayList<String> linesFromFile){
        ArrayList<String> words = new ArrayList<>();
        for (String line : linesFromFile) {
            String[] splitWords = line.split("\\s+");
            for (String word : splitWords) {
                words.add(word);
            }
        }
        return words;
    }

    private static ArrayList<String> readLinesFromFile(String input_file) throws IOException {
        ArrayList<String> lines = new ArrayList<>();

        FileReader fr = new FileReader(input_file);
        BufferedReader br = new BufferedReader(fr);

        String line;

        while ((line = br.readLine()) != null) {
            lines.add(line);
        }

        br.close();

        return lines;
    }

}