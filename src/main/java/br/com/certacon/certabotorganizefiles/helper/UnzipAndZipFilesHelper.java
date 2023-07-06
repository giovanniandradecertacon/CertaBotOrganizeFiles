package br.com.certacon.certabotorganizefiles.helper;

import br.com.certacon.certabotorganizefiles.entity.PathCreationEntity;
import br.com.certacon.certabotorganizefiles.utils.FileFoldersFunction;
import br.com.certacon.certabotorganizefiles.utils.FileStatus;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.utils.FileNameUtils;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;

@Component
public class UnzipAndZipFilesHelper {
    @Value("${config.rootDir}")
    private final String root;

    public UnzipAndZipFilesHelper(@Value("${config.rootDir}") String root) {
        this.root = root;
    }

    public List<Path> directoryCreator(PathCreationEntity entityForCreation) {
        List<Path> pathList = new ArrayList<>();
        Path uuidPath = Path.of(root + FileFoldersFunction.ORGANIZAR
                + File.separator + entityForCreation.getIpServer()
                + File.separator + entityForCreation.getCnpj()
                + File.separator + entityForCreation.getYear()
                + File.separator + UUID.randomUUID());
        Path compactedDir = Path.of(uuidPath
                + File.separator + FileFoldersFunction.COMPACTADOS);
        Path descomPactedDir = Path.of(uuidPath
                + File.separator + FileFoldersFunction.DESCOMPACTADOS);
        if (!uuidPath.toFile().exists()) uuidPath.toFile().mkdirs();
        if (!compactedDir.toFile().exists()) compactedDir.toFile().mkdirs();
        if (!descomPactedDir.toFile().exists()) descomPactedDir.toFile().mkdirs();

        pathList.add(compactedDir);
        pathList.add(descomPactedDir);

        return pathList;
    }


    public FileStatus unzipFile(File zipFile, File destinyDirectory) {
        FileStatus status = null;
        try (ArchiveInputStream entrada = new ZipArchiveInputStream(new FileInputStream(zipFile))) {
            if (!destinyDirectory.exists()) {
                destinyDirectory.mkdirs();
            }

            if (zipFile.exists()) {
                ArchiveEntry entry = entrada.getNextEntry();

                while (entry != null) {
                    String nomeArquivo = entry.getName();
                    File arquivo = new File(destinyDirectory, nomeArquivo);

                    if (entry.isDirectory()) {
                        arquivo.mkdirs();
                    } else {
                        File pastaArquivo = arquivo.getParentFile();
                        if (!pastaArquivo.exists()) {
                            pastaArquivo.mkdirs();
                        }
                        FileOutputStream fos = new FileOutputStream(arquivo);
                        IOUtils.copy(entrada, fos);
                        fos.close();
                    }

                    entry = entrada.getNextEntry();
                }
                status = FileStatus.UNZIPPED;
            } else {
                status = FileStatus.PROCESSING;
            }
        } catch (IOException e) {
            status = FileStatus.PROCESSING;
            throw new RuntimeException(e);
        } finally {
            return status;
        }
    }

    public PathCreationEntity pathSplitter(File folderPath) throws FileNotFoundException {
        if (folderPath.exists()) {
            String folderPathReplaced = folderPath.getPath().replace(File.separator, " ");
            String[] folderPathSplitted = folderPathReplaced.split(" ");
            String root = folderPathSplitted[0];
            String ipServer = folderPathSplitted[2];
            String cnpj = folderPathSplitted[3];
            String year = folderPathSplitted[4];

            return PathCreationEntity.builder()
                    .root(root)
                    .ipServer(ipServer)
                    .cnpj(cnpj)
                    .year(year)
                    .build();
        } else {
            throw new FileNotFoundException("Arquivo inexistente!");
        }
    }

    public FileStatus extractFolder(File directory, File destDir) throws IOException {
        File[] directoryList = directory.listFiles();
        for (File file : directoryList) {
            if (FileNameUtils.getExtension(file.getName()).equals("rar") ||
                    FileNameUtils.getExtension(file.getName()).equals("zip")
            ) {
                Files.move(file.toPath(), Path.of(destDir + File.separator + file.getName()), ATOMIC_MOVE);
            } else {
                Path parentFolder = Path.of(directory.getParentFile().getPath());
                Path filePath = Path.of(file.getPath());
                Files.move(filePath, parentFolder.resolve(UUID.randomUUID() + "." + FileNameUtils.getExtension(file.getName())), ATOMIC_MOVE);
            }
        }
        FileUtils.deleteDirectory(directory);
        return FileStatus.MOVED;
    }


    public Boolean checkFolderExistence(File[] fileList) {
        for (int i = 0; i < fileList.length; i++) {
            if (fileList[i].isDirectory()) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    public Path zipFiles(File descompactedDir, File destiny) throws IOException {
        File[] descompactedList = descompactedDir.listFiles();
        final FileOutputStream fos = new FileOutputStream(Paths.get(destiny.getPath()).toAbsolutePath() + File.separator + descompactedDir.getName() + ".zip");
        ZipOutputStream zipOut = new ZipOutputStream(fos);

        for (File srcFile : descompactedList) {
            File fileToZip = new File(srcFile.toURI());
            FileInputStream fis = new FileInputStream(fileToZip);
            ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
            zipOut.putNextEntry(zipEntry);

            byte[] buffer = new byte[8192];
            int length;
            while ((length = fis.read(buffer)) >= 0) {
                zipOut.write(buffer, 0, length);
            }
            fis.close();
        }
        zipOut.close();
        fos.close();
        descompactedDir.deleteOnExit();
        return Path.of(destiny.getPath() + File.separator + descompactedDir.getName() + ".zip");
    }

    public Path zipOneFile(File sourceFile, File destiny) throws IOException {
        FileOutputStream fos = new FileOutputStream(destiny);
        ZipOutputStream zos = new ZipOutputStream(fos);

        ZipEntry zipEntry = new ZipEntry(sourceFile.getName());
        zos.putNextEntry(zipEntry);

        FileInputStream fis = new FileInputStream(sourceFile);
        byte[] buffer = new byte[8092];
        int length;
        while ((length = fis.read(buffer)) > 0) {
            zos.write(buffer, 0, length);
        }

        fis.close();
        zos.closeEntry();
        zos.close();
        fos.close();

        return destiny.toPath();
    }
}
