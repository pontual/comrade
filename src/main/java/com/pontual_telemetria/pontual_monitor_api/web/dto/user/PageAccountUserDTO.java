package com.pontual_telemetria.pontual_monitor_api.web.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(name = "PageAccountUserDTO")
@Data
public class PageAccountUserDTO {
    @Schema(description = "Conteúdo paginado")
    private List<AccountUserDetailsDTO> content;

    private int totalPages;
    private long totalElements;
    private int number;
    private int size;
    private int numberOfElements;
    private boolean first;
    private boolean last;
    private boolean empty;
}
