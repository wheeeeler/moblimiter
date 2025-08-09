package net.wheel.api.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class FileUtil {

    public static FileReader createReader(File file) {
        if (file.exists()) {
            try {
                return new FileReader(file);
            } catch (FileNotFoundException e) {

            }
        }
        return null;
    }

    public static void closeReader(FileReader reader) {
        try {
            reader.close();
        } catch (IOException e) {
            System.out.println("IOException thrown, could not close file");
        }
    }

    public static File createDirectory(String dir) {
        File folder = new File(PathUtil.getBaseDir(), dir);
        if (!folder.exists())
            folder.mkdirs();
        return folder;
    }

    public static File createJsonFile(File dir, String name) {
        return new File(dir, name + ".json");
    }

    public static File recreateFile(File file) {
        if (file.exists()) {
            file.delete();
        }

        try {
            file.createNewFile();
        } catch (IOException e) {
            System.err.println("IOException thrown, could not recreate file.");
        }

        return file;
    }

    public static void saveJsonFile(File file, JsonObject jsonObject) {
        try {

            FileWriter writer = new FileWriter(file);
            Throwable throwable = null;
            try {
                writer.write(new GsonBuilder().setPrettyPrinting().create().toJson(jsonObject));
            } catch (Throwable var6_9) {
                throwable = var6_9;
                throw var6_9;
            } finally {
                if (throwable != null) {
                    try {
                        writer.close();
                    } catch (Throwable var6_8) {
                        throwable.addSuppressed(var6_8);
                    }
                } else {
                    writer.close();
                }
            }
        } catch (IOException e) {
            file.delete();
        }
    }

    public static List<String> read(final File inputFile) {
        final List<String> fileContentList = new ArrayList<>();
        FileReader fileReader = null;
        BufferedReader bufferedFileReader = null;

        try {
            fileReader = new FileReader(inputFile);
            bufferedFileReader = new BufferedReader(fileReader);

            String currentReadLine;

            while ((currentReadLine = bufferedFileReader.readLine()) != null) {
                fileContentList.add(currentReadLine);
            }
        } catch (FileNotFoundException e) {
            System.err.println("FileNotFoundException thrown, make sure the file exists.");
        } catch (IOException e) {
            System.err.println("IOException thrown, can't read the file's content.");
        } finally {
            try {
                if (bufferedFileReader != null) {
                    bufferedFileReader.close();
                }
                if (fileReader != null) {
                    fileReader.close();
                }
            } catch (IOException e) {
                System.err.println("IOException thrown, can't close the file reader.");
            }
        }

        return fileContentList;
    }

    public static String write(final File outputFile, final List<String> writeContent, boolean override) {
        BufferedWriter bufferedFileWriter = null;
        FileWriter fileWriter = null;
        String message = "";

        try {
            fileWriter = new FileWriter(outputFile, !override);
            bufferedFileWriter = new BufferedWriter(fileWriter);

            for (final String outputLine : writeContent) {
                bufferedFileWriter.write(outputLine);
                bufferedFileWriter.flush();
                bufferedFileWriter.newLine();
            }

            message = "Completed writing to the file.";
        } catch (IOException e) {
            message = "IOException thrown while attempting to write.";
        } finally {
            try {
                if (bufferedFileWriter != null) {
                    bufferedFileWriter.close();
                }
                if (fileWriter != null) {
                    fileWriter.close();
                }
            } catch (IOException e) {
                message = "IOException thrown while attempting to close the file writer.";
            }
        }

        return message;
    }

    public static boolean ensureExistance(File targetFile) {
        if (!targetFile.exists()) {
            try {
                targetFile.createNewFile();
            } catch (IOException e) {
                System.err.println("IOException thrown, can't create file.");
            }
        }

        return targetFile.exists();
    }
}
