package test;

import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class ClusteringComparator {
    private final String filename1, filename2;

    public ClusteringComparator(String filename1, String filename2) {
        this.filename1 = filename1;
        this.filename2 = filename2;
    }

    public void compareAndPrintResults() {
        try {
            int[] clusters1 = readFile(filename1);
            int[] clusters2 = readFile(filename2);
            System.out.println("Files read correctly, executing comparison for selected files");
            compareClusterings(clusters1, clusters2);
        } catch (Exception e) {
            System.out.println("Error during file reading, please provide a correct CSV file with classes");
        }

    }

    private void compareClusterings(int[] clusters1, int[] clusters2) {
        int n00 = 0;    //same in both
        int n10 = 0;    //same in first
        int n01 = 0;    //same in second
        int n11 = 0;    //different in both

        for (int i = 0; i < clusters1.length; i++) {
            for (int j = i + 1; j < clusters2.length; j++) {
                boolean same_in_1 = (clusters1[i] == clusters1[j]);
                boolean same_in_2 = (clusters2[i] == clusters2[j]);
                if (same_in_1) {
                    if (same_in_2) {
                        n00++;
                    } else {
                        n10++;
                    }
                } else {
                    if (same_in_2) {
                        n01++;
                    } else {
                        n11++;
                    }
                }
            }
        }

        int total = n00 + n10 + n01 + n11;

        double rand_index = (n00 + n11) / (double) total;
        System.out.println(String.format("Results:\n%s pairs;\n%s pairs with same group in both\n" +
                        "%s pairs with same group in first clustering only\n" +
                        "%s pairs with same group in second clustering only\n" +
                        "%s pairs with diff. groups in both" +
                        "\nrand index: %s",
                total, n00, n01, n10, n11, rand_index));
    }

    private int[] readFile(final String filename) throws Exception {
        List<Integer> readClasses = new ArrayList<>();

        CSVReader reader = new CSVReader(new FileReader(filename), ';');

        String[] record;
        while ((record = reader.readNext()) != null) {
            int value = Integer.valueOf(record[record.length - 1]);
            readClasses.add(value);
        }

        reader.close();
        return readClasses.stream().mapToInt(Integer::intValue).toArray();
    }

    public static void main(String argv[]) {
        if (argv.length < 2) {
            System.out.println("Incorrect argument count - please invoke the program with names of 2 CSV files to compare");
            return;
        }
        String filename1 = argv[0];
        String filename2 = argv[1];
        if (!(new File(filename1).exists() && new File(filename2).exists())) {
            System.out.println("Incorrect argument - file doesn't exist");
            return;
        }
        ClusteringComparator c = new ClusteringComparator(filename1, filename2);
        c.compareAndPrintResults();
    }
}