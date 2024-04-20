import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Scanner;

import static java.nio.file.StandardOpenOption.CREATE;
//Import all libraries needed for this program

public class ListMaker {
    //initializes the scanner, arraylist, File, fileName, and isDirty boolean
    private static Scanner in = new Scanner(System.in);
    private static ArrayList<String> lines = new ArrayList<>();

    private static File curFile;
    private static String curFileName;
    private static boolean isDirty = false;

    public static void main(String[] args) {


        String menuPrompt = "A - Add D - Delete V - View O - Open S - Save C -Clear Q - Quit";
        String cmd = ""; // A D P or Q
        boolean done = false;

        try {
            do {
                showList();
                cmd = SafeInput.getRegExString(in, menuPrompt, "[AaDdVvPpQqOoSsCc]");
                cmd = cmd.toUpperCase();
                switch (cmd) {

                    case "A":
                        add();
                        isDirty = true;
                        break;

                    case "D":
                        delete();
                        isDirty = true;
                        break;

                    case "V":
                        showList();
                        break;

                    case "O":
                        if (isDirty) {
                            System.out.println("You are about to lose the current memo data.");
                            boolean saveYN = SafeInput.getYNConfirm(in, "Do you want to save the current memo?");
                            if (saveYN) {
                                if (curFile == null)
                                    curFileName = SafeInput.getNonZeroLengthString(in, "Enter the name of the file:");
                                else
                                    curFileName = curFile.getName();
                                saveFile(curFileName);
                                isDirty = false;
                            } else {
                                openFile();
                            }
                        }

                    case "S":
                        if (curFile == null)
                            curFileName = SafeInput.getNonZeroLengthString(in, "Enter the name of the file:");
                        else
                            curFileName = curFile.getName();
                        saveFile(curFileName);
                        isDirty = false;
                        break;
                    case "C":
                        if (isDirty) {
                            boolean clearFileYN = SafeInput.getYNConfirm(in, "Are you sure you want to clear the list without saving?");
                            if (clearFileYN) {
                                lines.clear();
                                isDirty = false;
                            }
                        } else {
                            lines.clear();
                            isDirty = false;
                        }
                        break;

                    case "Q":
                        quit();
                        break;

                }
            } while (!done);

        } catch (FileNotFoundException e) {
            System.out.println("File not found error");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void showList() {

        System.out.println("-----------------------------------------------");
        if (lines.size() == 0) {
            System.out.println("\nThe list is currently empty.\n");
        } else {
            for (String l : lines)
                System.out.println("\t" + l);
        }
        System.out.println("-----------------------------------------------");
    }

    private static void quit() {
        boolean quitYN = false;
        quitYN = SafeInput.getYNConfirm(in, "Are you sure");

        if (quitYN) {
            System.exit(0);
        }
    }

    /**
     * Adds a line to the memo list
     */
    public static void add() {
        String lineItem = "";
        lineItem = SafeInput.getNonZeroLengthString(in, "Enter the new line for the memo");
        lines.add(lineItem);

    }

    private static void delete() {
        System.out.println("------------------------------------");
        if (lines.size() == 0) {
            System.out.println("\nNothing to delete.\n");
            System.out.println("--------------------------------");
            return;
        } else {
            int ln = 0;

            for (String l : lines) {
                ln++;
                System.out.printf("\t%3d %-80s\n", ln, l);

            }
        }
        System.out.println("-------------------------------------");
        int low = 1;
        int high = lines.size();
        int target = SafeInput.getRangedInt(in, "Enter the number of the line you want to delete", low, high);

        target--;

        lines.remove(target);

        return;

    }

    public static void openFile() throws IOException, FileNotFoundException {

        JFileChooser chooser = new JFileChooser();
        File selectedFile;
        String rec = "";

        File workingDirectory = new File(System.getProperty("user.dir"));

        System.out.println("Path " + workingDirectory.getPath());

        chooser.setCurrentDirectory(workingDirectory);

        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            selectedFile = chooser.getSelectedFile();
            curFile = selectedFile;
            Path file = selectedFile.toPath();
            InputStream in =
                    new BufferedInputStream(Files.newInputStream(file, CREATE));
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(in));

            int line = 0;
            while (reader.ready()) {
                rec = reader.readLine();
                line++;
                lines.add(rec);

                System.out.printf("\nLine %4d %-60s ", line, rec);
            }
            reader.close();
            System.out.println("\n\nData file read!");


        } else
        {
            System.out.println("No file selected!!! ... exiting.\nRun the program again and select a file.");
        }
    }

    private static void saveFile(String fileName) throws IOException {
        File workingDirectory = new File(System.getProperty("user.dir"));
        Path file = Paths.get(workingDirectory.getPath() + "\\src\\" + fileName);

        System.out.println("Path " + file.toString());

        OutputStream out =
                new BufferedOutputStream(Files.newOutputStream(file, CREATE));
        BufferedWriter writer =
                new BufferedWriter(new OutputStreamWriter(out));
        if (curFile == null)
            curFile = file.toFile();

        for (String rec : lines) {
            writer.write(rec, 0, rec.length());
            writer.newLine();
            System.out.println("Writing file data: " + rec);

        }
        writer.close(); 
        System.out.println("Data file written: " + curFile.getName());
    }
}
