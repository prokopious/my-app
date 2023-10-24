package treePrinter;

import java.io.File;

public class DirectoryTreePrinter {

    public static void main(String[] args) {
        // Specify the path to the directory you want to print
        String path = "path_to_your_directory"; // replace this with your directory

        // Create a file that represents the root directory
        File rootDirectory = new File(path);

        // Print the tree starting from the root directory, with an initial indentation of 0
        printDirectoryTree(rootDirectory, 0);
    }

    public static void printDirectoryTree(File folder, int indent) {
        // Print out the name of the current file or directory
        System.out.println(getIndentString(indent) + "+-- " + folder.getName());
        
        // Get a list of all the files in the directory
        File[] files = folder.listFiles();
        
        // Check if the directory was empty
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                // If it's a directory, we recursively call this method to print its contents
                printDirectoryTree(file, indent + 1);
            } else {
                // If it's a file, we just print its name with an increased indentation
                System.out.println(getIndentString(indent + 1) + "+-- " + file.getName());
            }
        }
    }

    private static String getIndentString(int indent) {
        // Builds the string used to indent the lines in the tree, depending on their depth
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < indent; i++) {
            sb.append("|  ");
        }
        return sb.toString();
    }
}

