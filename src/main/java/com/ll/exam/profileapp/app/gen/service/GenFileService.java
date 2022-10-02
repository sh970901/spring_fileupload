package com.ll.exam.profileapp.app.gen.service;

import com.ll.exam.profileapp.app.Util;
import com.ll.exam.profileapp.app.article.entity.Article;
import com.ll.exam.profileapp.app.base.AppConfig;
import com.ll.exam.profileapp.app.base.dto.RsData;
import com.ll.exam.profileapp.app.gen.entity.GenFile;
import com.ll.exam.profileapp.app.gen.repository.GenFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GenFileService {
    private final GenFileRepository genFileRepository;

    private String getCurrentDirName(String relTypeCode) {
        return relTypeCode + "/" + Util.date.getCurrentDateFormatted("yyyy_MM_dd");
    }

    public RsData<Map<String, GenFile>> saveFiles(Article article, Map<String, MultipartFile> fileMap) {
        String relTypeCode = "article";
        long relId = article.getId();

        Map<String, GenFile> genFileIds = new HashMap<>();

        for (String inputName : fileMap.keySet()) {
            MultipartFile multipartFile = fileMap.get(inputName);

            if (multipartFile.isEmpty()) {
                continue;
            }

            String[] inputNameBits = inputName.split("__");

            String typeCode = inputNameBits[0];
            String type2Code = inputNameBits[1];
            String originFileName = multipartFile.getOriginalFilename();
            String fileExt = Util.file.getExt(originFileName);
            String fileExtTypeCode = Util.file.getFileExtTypeCodeFromFileExt(fileExt);
            String fileExtType2Code = Util.file.getFileExtType2CodeFromFileExt(fileExt);
            int fileNo = Integer.parseInt(inputNameBits[2]);
            int fileSize = (int) multipartFile.getSize();
            String fileDir = getCurrentDirName(relTypeCode);

            GenFile genFile = GenFile
                    .builder()
                    .relTypeCode(relTypeCode)
                    .relId(relId)
                    .typeCode(typeCode)
                    .type2Code(type2Code)
                    .fileExtTypeCode(fileExtTypeCode)
                    .fileExtType2Code(fileExtType2Code)
                    .fileNo(fileNo)
                    .fileSize(fileSize)
                    .fileDir(fileDir)
                    .fileExt(fileExt)
                    .originFileName(originFileName)
                    .build();

            genFileRepository.save(genFile);
            String filePath = AppConfig.GET_FILE_DIR_PATH + "/" + fileDir + "/" + genFile.getFileName();

            File file = new File(filePath);

            file.getParentFile().mkdirs();

            try {
                multipartFile.transferTo(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            genFileIds.put(inputName, genFile);
        }
        return new RsData("S-1", "파일을 업로드했습니다.", genFileIds);
    }
    @Transactional
    public GenFile save(GenFile genFile) {
        Optional<GenFile> opOldGenFile = genFileRepository.findByRelTypeCodeAndRelIdAndTypeCodeAndType2CodeAndFileNo(genFile.getRelTypeCode(), genFile.getRelId(), genFile.getTypeCode(), genFile.getType2Code(), genFile.getFileNo());

        if (opOldGenFile.isPresent()) {
            GenFile oldGenFile = opOldGenFile.get();
            deleteFileFromStorage(oldGenFile);

            oldGenFile.merge(genFile);

            genFileRepository.save(oldGenFile);

            return oldGenFile;
        }

        genFileRepository.save(genFile);

        return genFile;
    }

    private void deleteFileFromStorage(GenFile genFile) {
        new File(genFile.getFilePath()).delete();
    }

    public void addGenFileByUrl(String relTypeCode, Long relId, String typeCode, String type2Code, int fileNo, String url) {
        String fileDir = getCurrentDirName(relTypeCode);

        String downFilePath = Util.file.downloadImg(url, AppConfig.GET_FILE_DIR_PATH + "/" + fileDir + "/" + UUID.randomUUID());

        File downloadedFile = new File(downFilePath);

        String originFileName = downloadedFile.getName();
        String fileExt = Util.file.getExt(originFileName);
        String fileExtTypeCode = Util.file.getFileExtTypeCodeFromFileExt(fileExt);
        String fileExtType2Code = Util.file.getFileExtType2CodeFromFileExt(fileExt);
        int fileSize = 0;
        try {
            fileSize = (int) Files.size(Paths.get(downFilePath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        GenFile genFile = GenFile
                .builder()
                .relTypeCode(relTypeCode)
                .relId(relId)
                .typeCode(typeCode)
                .type2Code(type2Code)
                .fileExtTypeCode(fileExtTypeCode)
                .fileExtType2Code(fileExtType2Code)
                .fileNo(fileNo)
                .fileSize(fileSize)
                .fileDir(fileDir)
                .fileExt(fileExt)
                .originFileName(originFileName)
                .build();

        genFile = save(genFile);

        String filePath = AppConfig.GET_FILE_DIR_PATH + "/" + fileDir + "/" + genFile.getFileName();

        File file = new File(filePath);

        file.getParentFile().mkdirs();

        downloadedFile.renameTo(file);
    }

    public Map<String, GenFile> getRelGenFileMap(Article article) {
        List<GenFile> genFiles = genFileRepository.findByRelTypeCodeAndRelIdOrderByTypeCodeAscType2CodeAscFileNoAsc("article", article.getId());
        return getRelGenFileMap(genFiles);
    }

    public Map<String, GenFile> getRelGenFileMap(List<GenFile> genFiles) {
        return genFiles
                .stream()
                .collect(Collectors.toMap(
                        genFile -> genFile.getTypeCode() + "__" + genFile.getType2Code() + "__" + genFile.getFileNo(),
                        genFile -> genFile,
                        (genFile1, genFile2) -> genFile1,
                        LinkedHashMap::new
                ));
    }

    public void deleteFiles(Article article, Map<String, String> params) {
        List<String> deleteFilesArgs = params.keySet()
                .stream()
                .filter(key -> key.startsWith("delete___"))
                .map(key -> key.replace("delete___", ""))
                .collect(Collectors.toList());

        deleteFiles(article, deleteFilesArgs);
    }
    public void deleteFiles(Article article, List<String> params) {
        String relTypeCode = "article";
        Long relId = article.getId();

        params
                .stream()
                .forEach(key -> {
                    String[] keyBits = key.split("__");

                    String typeCode = keyBits[0];
                    String type2Code = keyBits[1];
                    int fileNo = Integer.parseInt(keyBits[2]);

                    Optional<GenFile> optGenFile = genFileRepository.findByRelTypeCodeAndRelIdAndTypeCodeAndType2CodeAndFileNo(relTypeCode, relId, typeCode, type2Code, fileNo);

                    if (optGenFile.isPresent()) {
                        delete(optGenFile.get());
                    }
                });
    }

    private void delete(GenFile genFile) {
        deleteFileFromStorage(genFile);
        genFileRepository.delete(genFile);
    }

    public Optional<GenFile> getById(Long id) {
        return genFileRepository.findById(id);
    }
    public List<GenFile> getRelGenFilesByRelIdIn(String relTypeCode, long[] relIds) {
        return genFileRepository.findAllByRelTypeCodeAndRelIdInOrderByTypeCodeAscType2CodeAscFileNoAsc(relTypeCode, relIds);
    }
}
