package com.demo.filestorage.services;

import org.springframework.web.multipart.MultipartFile;

public interface IFileUploadService {
    boolean uploadFile(String userName, MultipartFile file);
}