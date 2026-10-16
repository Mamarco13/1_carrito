package com.practicas.__carrito.controller;

import com.practicas.__carrito.service.DatabaseExportService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final DatabaseExportService databaseExportService;

    public AdminController(DatabaseExportService databaseExportService) {
        this.databaseExportService = databaseExportService;
    }

    @GetMapping("/export")
    public ResponseEntity<ByteArrayResource> exportDatabase() {
        byte[] sqlContent = databaseExportService.exportDatabaseToSql();
        String filename = "products.sql";

        ByteArrayResource resource = new ByteArrayResource(sqlContent);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("application/sql"))
                .contentLength(sqlContent.length)
                .body(resource);
    }
}
