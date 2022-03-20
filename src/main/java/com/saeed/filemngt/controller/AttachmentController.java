package com.saeed.filemngt.controller;

import com.saeed.filemngt.entity.Attachment;
import com.saeed.filemngt.model.ResponseData;
import com.saeed.filemngt.service.AttachmentService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Optional;

@RestController
public class AttachmentController {
    private AttachmentService attachmentService;

    public AttachmentController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    @PostMapping("/upload")
    public ResponseEntity<Optional<ResponseData>> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            Attachment attachment = attachmentService.saveAttachment(file);
            String downloadURL = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/download/")
                    .path(attachment.getId())
                    .toUriString();
            return ResponseEntity.ok(Optional.of(new ResponseData(attachment.getFilename(), downloadURL, file.getContentType(), file.getSize())));
        } catch (Exception ex) {
            return ResponseEntity.of(Optional.empty());
        }
    }

    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileId) {
        try {
            Attachment attachment = attachmentService.getAttachment(fileId);
            return ResponseEntity.ok().
                    contentType(MediaType.parseMediaType(attachment.getFileType())).
                    header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"" + attachment.getFilename() + "\"").
                    body(new ByteArrayResource(attachment.getData()));
        } catch (Exception ex) {
            return ResponseEntity.of(Optional.empty());
        }
    }
}
