package com.snbnk.documentation.api;

import com.snbnk.documentation.api.dto.DocumentResponse;
import com.snbnk.documentation.domain.DocumentType;
import com.snbnk.documentation.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService service;

    @PostMapping("/upload")
    public DocumentResponse upload(
            @RequestParam String applicationId,
            @RequestParam DocumentType type,
            @RequestParam MultipartFile file
    ) {

        return service.upload(applicationId, type, file);
    }

    @GetMapping("/{applicationId}")
    public List<DocumentResponse> list(
            @PathVariable String applicationId
    ) {
        return service.list(applicationId);
    }
}
