package net.hawkengine.agent.services;

import net.hawkengine.agent.services.interfaces.IFileManagementService;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tools.ant.DirectoryScanner;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileManagementService implements IFileManagementService {

    @Override
    public String zipFiles(String zipFilePath, List<File> files, String filesRootPath, boolean includeRootPath) {

        String errorMessage = null;
        ZipParameters parameters = new ZipParameters();
        parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
        parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_FAST);
        parameters.setEncryptionMethod(Zip4jConstants.ENC_NO_ENCRYPTION);
        parameters.setDefaultFolderPath(filesRootPath);
        parameters.setIncludeRootFolder(includeRootPath);

        try {
            ZipFile zipFile = new ZipFile(zipFilePath);

            for (int i = 0; i < files.size(); i++) {
                File file = files.get(i);
                if (file.isFile()) {
                    zipFile.addFile(file, parameters);
                }
                if (file.isDirectory()) {
                    zipFile.addFolder(file, parameters);
                }
            }
        } catch (ZipException e) {
            errorMessage = e.getMessage();
        }

        return errorMessage;
    }

    public void generateDirectory(File file){
        file.getParentFile().mkdirs();
    }

    @Override
    public File generateUniqueFile(String filePath, String fileExtension) {

        File directory = new File(filePath);

        if (!directory.exists()) {
            directory.mkdirs();
        }
        String zipFileName = UUID.randomUUID() + "." + fileExtension;
        String zipFilePath = Paths.get(filePath, zipFileName).toString();
        zipFilePath = this.normalizePath(zipFilePath);
        File file = new File(zipFilePath);

        return file;
    }

    @Override
    public String unzipFile(String filePath, String destination) {

        String errorMessage = null;
        try {
            ZipFile zipFile = new ZipFile(filePath);
            zipFile.extractAll(destination);
        } catch (ZipException e) {
            errorMessage = e.getMessage();
        }

        return errorMessage;
    }

    @Override
    public String initiateFile(File file,InputStream stream, String filePath) {
        String errorMessage = null;
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                errorMessage = e.getMessage();
                e.printStackTrace();
            }
        }

        try {
            byte[] bytes = IOUtils.toByteArray(stream);
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            fileOutputStream.write(bytes);
            fileOutputStream.close();
        } catch (IOException e) {
            errorMessage = e.getMessage();
        }

        return errorMessage;
    }

    @Override
    public String deleteFile(String filePath) {
        String errorMessage = null;
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }

        return errorMessage;
    }

    @Override
    public String deleteFilesInDirectory(String directoryPath){
        String errorMessage = null;
        if((directoryPath == null) || (directoryPath == "")){
            return errorMessage = "Directory Path arguments is empty or null!";
        }

        File directory = new File(directoryPath);
        for (File file : directory.listFiles()) {
            if (!file.isDirectory()) {
                file.delete();
            }
        }

        return errorMessage;
    }

    @Override
    public String deleteDirectoryRecursively(String directoryPath) {

        String errorMessage = null;

        File directory = new File(directoryPath);

        try {
            FileUtils.deleteDirectory(directory);
        } catch (IOException e) {
            errorMessage = e.getMessage();
            return errorMessage;
        }

        return errorMessage;
    }

    @Override
    public String getRootPath(String path) {

        String rootPath = path;

        rootPath = this.normalizePath(path);

        File file = new File(rootPath);

        int wildCardCharIndex = path.indexOf('*');

        if (wildCardCharIndex != -1) {
            rootPath = path.substring(0, wildCardCharIndex - 1);
            return rootPath;
        }

        if (file.isFile()) {
            rootPath = file.getAbsolutePath().replace(file.getName(), "");
            FilenameUtils.normalizeNoEndSeparator(rootPath);
            return rootPath;
        }

        if (file.isDirectory()) {
            rootPath = file.getAbsolutePath();
            return rootPath;
        }

        return "";
    }

    @Override
    public String getPattern(String rootPath, String path) {

        String pattern = path.replace(rootPath, "");

        if (pattern.isEmpty()) {
            pattern = "**";
        }

        pattern = this.normalizePath(pattern);

        return StringUtils.strip(pattern,"/") ;
    }

    @Override
    public List<File> getFiles(String rootPath, String wildCardPattern) {

        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir(this.normalizePath(rootPath));
        scanner.setIncludes(new String[]{wildCardPattern});
        scanner.scan();
        String[] allPaths = scanner.getIncludedFiles();

        List<File> allFiles = new ArrayList<File>();
        String[] directories = scanner.getIncludedDirectories();
        for (String directory : directories) {
            allFiles.add(new File(rootPath, directory));
        }

        for (int i = 0; i < allPaths.length; i++) {
            File file = new File(rootPath, allPaths[i]);
            if (!allFiles.contains(file.getParentFile())) {
                allFiles.add(file);
            }
        }

        return allFiles;
    }

    @Override
    public String pathCombine(String... args) {
        String output = "";
        for (String arg : args) {
            arg = this.normalizePath(arg);
            output = FilenameUtils.concat(output, arg);
        }
        FilenameUtils.normalizeNoEndSeparator(output);

        return output;
    }

    @Override
    public String normalizePath(String filePath) {

        return StringUtils.replace(filePath, "\\", "/");
    }

    @Override
    public String urlCombine(String... args) {

        String output = null;

        StringBuilder argsHolder = new StringBuilder();
        for (String arg : args) {
            arg = this.normalizePath(arg);
            argsHolder.append(arg);
            argsHolder.append("/");
        }
        output = argsHolder.toString();
        output = StringUtils.stripEnd(output, "/");

        return output;
    }
}
