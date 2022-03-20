package com.saeed.filemngt.service;

import com.saeed.filemngt.entity.Attachment;
import com.saeed.filemngt.repository.AttachmentRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@Service
public class AttachmentServiceImpl implements AttachmentService {

    private AttachmentRepository attachmentRepository;
    private static final Logger LOGGER = LogManager.getLogger();

    public AttachmentServiceImpl(AttachmentRepository attachmentRepository) {
        this.attachmentRepository = attachmentRepository;
    }

    @Override
    public Attachment saveAttachment(MultipartFile file) throws Exception {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        try {
            if (fileName.contains("..")) {
                throw new Exception("Filename contains invalid path sequence");
            }
            Attachment attachment = new Attachment(fileName, file.getContentType(), file.getBytes());
            return attachmentRepository.save(attachment);
        } catch (Exception ex) {
            LOGGER.info("the file with the name" + fileName + "could not be saved");
            throw new Exception("Could not save file" + fileName);
        }
    }

    @Override
    public Attachment getAttachment(String fileId) throws Exception {
        return attachmentRepository.findById(fileId).orElseThrow(() -> new Exception("file could not be found"));
    }
}
