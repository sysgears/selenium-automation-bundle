package com.sysgears.seleniumbundle.core.data.cloud

interface ICloudService {

    void downloadFile(String remotePath, String localPath)

    void downloadFiles(String remotePath, String localPath)

    void uploadFile(String localPath, String remotePath)

    void uploadFiles(String localPath, String remotePath)
}