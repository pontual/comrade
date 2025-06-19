package com.pontual_telemetria.pontual_monitor_api.application.service;

import com.pontual_telemetria.pontual_monitor_api.domain.service.DataReadingDomainService;
import com.pontual_telemetria.pontual_monitor_api.web.dto.sismetro.DataExtractionResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@RequiredArgsConstructor
public class DataReadingApplicationService {
    final DataReadingDomainService dataReadingDomainService;

    public DataExtractionResult sismetroDataExtraction(MultipartFile file) {
        log.info("Iniciada extração dos dados do arquivo: {}", file.getOriginalFilename());
        DataExtractionResult result = dataReadingDomainService.excelToJsonParse(file);
        log.info("Finalizada a extração dos dados do arquivo: {}", file.getOriginalFilename());
        return result;
    }
}
