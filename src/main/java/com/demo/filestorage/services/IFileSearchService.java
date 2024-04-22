package com.demo.filestorage.services;

import org.springframework.core.io.ByteArrayResource;

public interface IFileSearchService {

    ByteArrayResource searchFile(String userName, String fileName) throws Exception;
}
