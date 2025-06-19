package com.pontual_telemetria.pontual_monitor_api.web.controller;

import com.pontual_telemetria.pontual_monitor_api.application.service.DataReadingApplicationService;
import com.pontual_telemetria.pontual_monitor_api.web.dto.sismetro.DataExtractionResult;
import com.pontual_telemetria.pontual_monitor_api.web.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/data-reading")
public class DataReadingController {

    final DataReadingApplicationService dataReadingApplicationService;

    @Operation(
            summary = "Importador de dados do Sismetro",
            description = "Importa dados de tabela em Excel para JSON"
    )
    @Parameter(
            description = "Arquivo Excel (.xlsx)",
            content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Importação realizada com sucesso", content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(
                            schema = @Schema(implementation = DataExtractionResult.class)
                    )
            )),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            )),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            ))
    })
    @PostMapping(value = "/excel/sismetro-reading-control", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DataExtractionResult> sismetroDataExtraction(@RequestParam("file") MultipartFile file){
        DataExtractionResult result = dataReadingApplicationService.sismetroDataExtraction(file);
        return ResponseEntity.ok(result);
    }
}
